/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.chips.predict;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.allapps.AllAppsStore;

import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.allapps.PredictionsProvider;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.predictions.PredictionsProviderService;

import static java.util.Collections.EMPTY_LIST;

public class PredictedAppsProvider extends ChipProvider {
    private final Context context;
    private PredictionsProvider serviceConn;
    private AllAppsStore appsStore;

    public PredictedAppsProvider(Context context) {
        this.context = context;
        this.appsStore = new AllAppsStore();
    }

    private void connect() {
        if (serviceConn != null && serviceConn.asBinder().pingBinder()) {
            return;
        } else {
            context.bindService(new Intent(context, PredictionsProviderService.class),
                    new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            serviceConn = PredictionsProvider.Stub.asInterface(service);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            serviceConn = null;
                        }
                    }, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_WAIVE_PRIORITY);
        }
    }

    @Override
    public List<Item> getItems(Context context) {
        try {
            return serviceConn == null ? EMPTY_LIST : serviceConn.getPredictions().stream().map(it -> {
                ItemInfoWithIcon info = it.getApp(appsStore);
                Item item = new Item();
                item.icon = new BitmapDrawable(context.getResources(), info.iconBitmap);
                item.title = info.title.toString();
                item.click = () -> {
                    context.startActivity(info.getIntent());
                };
                return item;
            }).collect(Collectors.toList());
        } catch (RemoteException e) {
            return EMPTY_LIST;
        }
    }
}
