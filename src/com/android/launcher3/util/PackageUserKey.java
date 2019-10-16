package com.android.launcher3.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;

import com.android.launcher3.ItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;

import java.util.Arrays;

/** Creates a hash key based on package name and user. */
public class PackageUserKey implements Parcelable {

    public String mPackageName;
    public UserHandle mUser;
    private int mHashCode;

    protected PackageUserKey(Parcel in) {
        mPackageName = in.readString();
        mUser = in.readParcelable(UserHandle.class.getClassLoader());
        mHashCode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPackageName);
        dest.writeParcelable(mUser, flags);
        dest.writeInt(mHashCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PackageUserKey> CREATOR = new Creator<PackageUserKey>() {
        @Override
        public PackageUserKey createFromParcel(Parcel in) {
            return new PackageUserKey(in);
        }

        @Override
        public PackageUserKey[] newArray(int size) {
            return new PackageUserKey[size];
        }
    };

    public static PackageUserKey fromItemInfo(ItemInfo info) {
        return new PackageUserKey(info.getTargetComponent().getPackageName(), info.user);
    }

    public static PackageUserKey fromNotification(StatusBarNotification notification) {
        return new PackageUserKey(notification.getPackageName(), notification.getUser());
    }

    public PackageUserKey(String packageName, UserHandle user) {
        update(packageName, user);
    }

    private void update(String packageName, UserHandle user) {
        mPackageName = packageName;
        mUser = user;
        mHashCode = Arrays.hashCode(new Object[] {packageName, user});
    }

    /**
     * This should only be called to avoid new object creations in a loop.
     * @return Whether this PackageUserKey was successfully updated - it shouldn't be used if not.
     */
    public boolean updateFromItemInfo(ItemInfo info) {
        if (DeepShortcutManager.supportsShortcuts(info)) {
            update(info.getTargetComponent().getPackageName(), info.user);
            return true;
        }
        return false;
    }

    public boolean updateFromNotification(StatusBarNotification sbn) {
        update(sbn.getPackageName(), sbn.getUser());
        return true;
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PackageUserKey)) return false;
        PackageUserKey otherKey = (PackageUserKey) obj;
        return mPackageName.equals(otherKey.mPackageName) && mUser.equals(otherKey.mUser);
    }
}
