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

/*
 * This is an implementation of ComponentKeyMapper as a Parcelable.
 * Due to Context not being Parcelable or Serializable, this class only provides a subset of functionality that would normally be provided by ComponentKeyMapper.
 */

package ch.deletescape.lawnchair.allapps;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.launcher3.AppInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.util.ComponentKey;
import com.google.android.apps.nexuslauncher.util.ComponentKeyMapper;

import org.jetbrains.annotations.NotNull;

public class ParcelableComponentKeyMapper implements Parcelable {

    private final ComponentKey componentKey;
    private final Bitmap icon;

    public ParcelableComponentKeyMapper(ComponentKey componentKey, Bitmap icon) {
        this.componentKey = componentKey;
        this.icon = icon;
    }

    public ParcelableComponentKeyMapper(ComponentKeyMapper mapper) {
        componentKey = mapper.getComponentKey();
        icon = null;
    }

    protected ParcelableComponentKeyMapper(Parcel in) {
        componentKey = in.readParcelable(ComponentKey.class.getClassLoader());
        icon = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ParcelableComponentKeyMapper> CREATOR = new Creator<ParcelableComponentKeyMapper>() {
        @Override
        public ParcelableComponentKeyMapper createFromParcel(Parcel in) {
            return new ParcelableComponentKeyMapper(in);
        }

        @Override
        public ParcelableComponentKeyMapper[] newArray(int size) {
            return new ParcelableComponentKeyMapper[size];
        }
    };

    public ItemInfoWithIcon getApp(AllAppsStore allAppsStore) {
        AppInfo info = allAppsStore.getApp(this.componentKey);
        if (info != null) {
            return info;
        } else {
            return null;
        }
    }

    public String getComponentClass() {
        return this.componentKey.componentName.getClassName();
    }

    public Bitmap getIcon() {
        return this.icon;
    }

    public ComponentKey getComponentKey() {
        return this.componentKey;
    }

    public String getPackage() {
        return this.componentKey.componentName.getPackageName();
    }

    @NotNull
    public String toString() {
        return this.componentKey.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(componentKey, flags);
        dest.writeParcelable(icon, flags);
    }
}
