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

public abstract class MutableDelegate<T> implements ConfigurationDelegate {
    private ServiceClient client;
    protected T value;

    @SuppressWarnings("WeakerAccess")
    protected MutableDelegate(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
        if (client != null) {
            this.client.configure();
        } else {
            throw new IllegalStateException("client wasn't attached when set called");
        }
    }

    @Override
    public void attachToClient(ServiceClient client) {
        this.client = client;
    }
}
