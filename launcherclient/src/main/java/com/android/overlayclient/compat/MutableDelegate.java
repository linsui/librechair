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

package com.android.overlayclient.compat;

import com.android.overlayclient.client.ServiceClient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class MutableDelegate<T> implements ConfigurationDelegate {
    private ServiceClient client;
    private List<Runnable> clientConnectQueue;
    protected T value;

    @SuppressWarnings("WeakerAccess")
    protected MutableDelegate(T value) {
        this.value = value;
        this.clientConnectQueue = new LinkedList<>();
    }

    public void set(T value) {
        this.value = value;
        if (client != null) {
            this.client.configure();
        } else {
            clientConnectQueue.clear();
            clientConnectQueue.add(() -> this.client.configure());
        }
    }

    @Override
    public void attachToClient(ServiceClient client) {
        this.client = client;
        Iterator<Runnable> queueIter = clientConnectQueue.iterator();
        while (queueIter.hasNext()) {
            queueIter.next().run();
            queueIter.remove();
        }
    }
}
