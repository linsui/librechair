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

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import ch.deletescape.lawnchair.util.IRunnable;

@SuppressWarnings("WeakerAccess")
public class RemoteAction implements Parcelable {
    public String name;
    public Bitmap icon;
    public IRunnable onClick;

    public RemoteAction(String name, Bitmap icon, IRunnable onClick) {
        this.name = name;
        this.icon = icon;
        this.onClick = onClick;
    }

    protected RemoteAction(Parcel in) {
        name = in.readString();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        onClick = IRunnable.Stub.asInterface(in.readStrongBinder());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(icon, flags);
        dest.writeStrongInterface(onClick);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemoteAction> CREATOR = new Creator<RemoteAction>() {
        @Override
        public RemoteAction createFromParcel(Parcel in) {
            return new RemoteAction(in);
        }

        @Override
        public RemoteAction[] newArray(int size) {
            return new RemoteAction[size];
        }
    };

    public FeedProvider.Action toAction(Context context) {
        return new FeedProvider.Action(new BitmapDrawable(context.getResources(), icon), name, () -> {
            try {
                onClick.run();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
