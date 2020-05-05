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

package com.android.launcher3.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class ParcelablePair<K extends Parcelable,
        V extends Parcelable> extends Pair<K, V> implements Parcelable, Serializable {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public ParcelablePair(K first, V second) {
        super(first, second);
    }

    protected ParcelablePair(Parcel in) {
        super(in.readParcelable(Parcelable.class.getClassLoader()),
                in.readParcelable(Parcelable.class.getClassLoader()));
    }

    public static final Creator<ParcelablePair> CREATOR = new Creator<ParcelablePair>() {
        @Override
        public ParcelablePair createFromParcel(Parcel in) {
            return new ParcelablePair(in);
        }

        @Override
        public ParcelablePair[] newArray(int size) {
            return new ParcelablePair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(first, flags);
        dest.writeParcelable(second, flags);
    }
}
