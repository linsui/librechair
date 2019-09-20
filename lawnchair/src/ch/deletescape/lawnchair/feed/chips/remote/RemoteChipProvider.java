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

package ch.deletescape.lawnchair.feed.chips.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;

public class RemoteChipProvider implements ChipProvider {
    private IChipProvider connection;
    private Context context;

    public RemoteChipProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItems(Context context) {
        try {
            return connection == null ? Collections.EMPTY_LIST :
                    connection.getChips().stream().map(
                            remoteItem -> remoteItem.toItem(context)).collect(
                            Collectors.toList());
        } catch (RemoteException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void acceptArguments(String args) {
        ComponentName name = ComponentName.unflattenFromString(args);
        context.bindService(new Intent().setComponent(name), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connection = IChipProvider.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                connection = null;
            }
        }, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    }
}
