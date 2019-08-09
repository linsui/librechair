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

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class SortingAlgorithmArgumentWrapper implements Parcelable {

    public SortingAlgorithmArgumentWrapper(
            List<List<RemoteCard>> data) {
        this.data = data;
    }

    public List<List<RemoteCard>> data;

    protected SortingAlgorithmArgumentWrapper(Parcel in) {
        data = new ArrayList<>();
        in.readList(data, List.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SortingAlgorithmArgumentWrapper> CREATOR = new Creator<SortingAlgorithmArgumentWrapper>() {
        @Override
        public SortingAlgorithmArgumentWrapper createFromParcel(Parcel in) {
            return new SortingAlgorithmArgumentWrapper(in);
        }

        @Override
        public SortingAlgorithmArgumentWrapper[] newArray(int size) {
            return new SortingAlgorithmArgumentWrapper[size];
        }
    };
}
