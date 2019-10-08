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

public class ParcelableSerializablePair<K extends Parcelable,
        V extends Serializable> extends Pair<K, V> implements Parcelable, Serializable {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public ParcelableSerializablePair(K first, V second) {
        super(first, second);
    }

    protected ParcelableSerializablePair(Parcel in) {
        super(in.readParcelable(Parcelable.class.getClassLoader()), (V) in.readSerializable());
    }

    public static final Creator<ParcelableSerializablePair> CREATOR = new Creator<ParcelableSerializablePair>() {
        @Override
        public ParcelableSerializablePair createFromParcel(Parcel in) {
            return new ParcelableSerializablePair(in);
        }

        @Override
        public ParcelableSerializablePair[] newArray(int size) {
            return new ParcelableSerializablePair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(first, flags);
        dest.writeSerializable(second);
    }
}
