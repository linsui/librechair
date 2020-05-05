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

package com.android.overlayclient.state;

import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class ServiceState {
    public static final int FLAG_ATTACHED = 1;
    public static final int FLAG_SEARCH_ATTACHED = 24;

    private boolean overlayAttached;
    private boolean searchAttached;

    private Consumer<ServiceState> changeListener;

    public int toMask() {
        return overlayAttached ? searchAttached ? FLAG_ATTACHED
                | FLAG_SEARCH_ATTACHED : FLAG_ATTACHED : 0;
    }


    public void setSearchAttached(boolean searchAttached) {
        this.searchAttached = searchAttached;
        changeListener.accept(this);
    }

    public void setOverlayAttached(boolean overlayAttached) {
        this.overlayAttached = overlayAttached;
        if (changeListener != null) {
            changeListener.accept(this);
        }
    }

    public boolean isOverlayAttached() {
        return overlayAttached;
    }

    public boolean isSearchAttached() {
        return searchAttached;
    }

    public void setChangeListener(
            Consumer<ServiceState> changeListener) {
        this.changeListener = changeListener;
        changeListener.accept(this);
    }
}
