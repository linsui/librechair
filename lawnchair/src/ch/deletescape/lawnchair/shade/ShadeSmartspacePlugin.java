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

package ch.deletescape.lawnchair.shade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import androidx.annotation.Nullable;
import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.CardData;
import com.android.launcher3.plugin.unread.IUnreadPlugin.Stub;
import com.android.launcher3.plugin.unread.IUnreadPluginCallback;
import java.util.List;
import java.util.stream.Collectors;

public class ShadeSmartspacePlugin extends Stub {

    private Context context;

    public ShadeSmartspacePlugin(Context context) {
        this.context = context;
    }

    @Override
    public List<String> getText() throws RemoteException {
        CardData card = ((LawnchairApp) context.getApplicationContext()).getSmartspace()
                .getSmartspaceData().getCard();
        if (card != null) {
            return card.getLines().stream().map(it -> it.getText().toString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public void clickView(int index, Bundle launchOptions) throws RemoteException {
        CardData card = ((LawnchairApp) context.getApplicationContext()).getSmartspace()
                .getSmartspaceData().getCard();
        if (card != null && card.getOnClickListener() != null) {
            card.getOnClickListener().onClick(new View(context));
        }
    }

    @Override
    public void addOnChangeListener(IUnreadPluginCallback cb) throws RemoteException {
        ((LawnchairApp) context.getApplicationContext()).getSmartspace().addListener(container -> {
            try {
                if (cb != null) {
                    cb.onChange();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void removeOnChangeListener(IUnreadPluginCallback cb) throws RemoteException {
        // TODO allow removing remote change listeners
    }

    public static final class Service extends android.app.Service {

        public ShadeSmartspacePlugin implementation;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return implementation != null ? implementation
                    : (implementation = new ShadeSmartspacePlugin(getApplicationContext()));
        }
    }
}
