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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.util.IRunnable;

public class RemoteItem implements Parcelable {
    public String title;
    public Bitmap icon;
    public IRunnable click;

    @SuppressWarnings("WeakerAccess")
    protected RemoteItem(Parcel in) {
        title = in.readString();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        click = IRunnable.Stub.asInterface(in.readStrongBinder());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(icon, flags);
        dest.writeStrongInterface(click);
    }

    public ChipProvider.Item toItem(Context context) {
        ChipProvider.Item item = new ChipProvider.Item();
        item.title = title;
        item.icon = new BitmapDrawable(context.getResources(), icon);
        item.click = () -> {
            try {
                click.run();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemoteItem> CREATOR = new Creator<RemoteItem>() {
        @Override
        public RemoteItem createFromParcel(Parcel in) {
            return new RemoteItem(in);
        }

        @Override
        public RemoteItem[] newArray(int size) {
            return new RemoteItem[size];
        }
    };
}
