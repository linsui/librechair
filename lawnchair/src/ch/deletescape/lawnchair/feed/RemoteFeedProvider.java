/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import ch.deletescape.lawnchair.LawnchairPreferences;
import ch.deletescape.lawnchair.reflection.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemoteFeedProvider extends FeedProvider {

    public static final String SERVICE_ACTION = "ch.deletescape.lawnchair.FEED_PROVIDER";
    private Map<ComponentName, IFeedProvider> providerMap = new HashMap<>();

    public static List<ComponentName> availableProviders(Context context) {
        List<ComponentName> infos = new ArrayList<>();
        for (ApplicationInfo packageInfo : context.getPackageManager()
                .getInstalledApplications(0)) {
            if (resolveFeedProvider(packageInfo.packageName, context) != null
                    && packageInfo.enabled) {
                ServiceInfo service = resolveFeedProvider(packageInfo.packageName, context);
                infos.add(new ComponentName(service.packageName, service.name));
            }
        }
        return infos.stream().filter(it -> !LawnchairPreferences.Companion.getInstance(context)
                .getDisabledRemoteFeedProviders().contains(it.flattenToString())).collect(
                Collectors.toList());
    }

    public static ServiceInfo resolveFeedProvider(String packageName, Context context) {
        Intent intent = new Intent(SERVICE_ACTION).setPackage(packageName);
        return context.getPackageManager().resolveService(intent, 0) == null ? null
                : context.getPackageManager().resolveService(intent, 0).serviceInfo;
    }

    public RemoteFeedProvider(Context c) {
        super(c);
    }

    public void refreshIPCBindings(@Nullable ComponentName toRefresh) {
        if (toRefresh != null) {
            try {
                getContext()
                        .bindService(new Intent().setComponent(toRefresh), new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                try {
                                    providerMap.put(name, IFeedProvider.Stub.asInterface(service));
                                } catch (RuntimeException e) {
                                    Log.d(getClass().getName(),
                                            "onServiceConnected: could not convert service to interface",
                                            e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {
                                if (providerMap.containsKey(name)) {
                                    providerMap.remove(name);
                                }
                                Log.d(getClass().getName(),
                                        "onServiceDisconnected: disconnected from service. re-establishing connection");
                                refreshIPCBindings(name);
                            }
                        }, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                Log.w("refreshIPCBindings: bind failed due to SecurityException", e);
            }
        } else {
            for (ComponentName name : availableProviders(getContext())) {
                try {
                    getContext()
                            .bindService(new Intent().setComponent(name), new ServiceConnection() {
                                @Override
                                public void onServiceConnected(ComponentName name,
                                        IBinder service) {
                                    try {
                                        providerMap
                                                .put(name, IFeedProvider.Stub.asInterface(service));
                                    } catch (RuntimeException e) {
                                        Log.d(getClass().getName(),
                                                "onServiceConnected: could not convert service to interface",
                                                e);
                                    }
                                }

                                @Override
                                public void onServiceDisconnected(ComponentName name) {
                                    if (providerMap.containsKey(name)) {
                                        providerMap.remove(name);
                                    }
                                    Log.d(getClass().getName(),
                                            "onServiceDisconnected: disconnected from service. re-establishing connection");
                                    refreshIPCBindings(name);
                                }
                            }, 0);
                } catch (SecurityException e) {
                    Log.w("refreshIPCBindings: bind failed due to SecurityException", e);
                }
            }
        }
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public List<Card> getCards() {
        List<List<Card>> toSort = new ArrayList<>();
        Log.d(getClass().getName(), "getCards: retrieving cards");
        for (ComponentName name : availableProviders(getContext())) {
            Log.d(getClass().getName(), "getCards: found provider " + name);
            if (!providerMap.containsKey(name) || !providerMap.get(name).asBinder().pingBinder()) {
                refreshIPCBindings(name);
            } else {
                try {
                    toSort.add(providerMap.get(name).getCards().stream()
                            .map(remoteCard -> remoteCard.toCard(getContext())).collect(
                                    Collectors.toList()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return ReflectionUtils.inflateSortingAlgorithm(
                    LawnchairPreferences.Companion.getInstance(getContext())
                            .getFeedPresenterAlgorithm()).sort(
                    Arrays.copyOf(toSort.toArray(), toSort.size(), List[].class));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }
}
