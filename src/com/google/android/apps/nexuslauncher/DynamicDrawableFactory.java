package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Process;

import android.os.UserHandle;
import com.android.launcher3.*;
import com.android.launcher3.graphics.BitmapInfo;
import com.android.launcher3.graphics.DrawableFactory;
import com.google.android.apps.nexuslauncher.clock.DynamicClock;

public class DynamicDrawableFactory extends DrawableFactory {
    private final DynamicClock mDynamicClockDrawer;

    public DynamicDrawableFactory(Context context) {
        mDynamicClockDrawer = new DynamicClock(context);
    }

    @Override
    public FastBitmapDrawable newIcon(Context c, ItemInfoWithIcon info) {
        if (info == null || info.itemType != 0 ||
                !DynamicClock.DESK_CLOCK.equals(info.getTargetComponent()) ||
                !info.user.equals(Process.myUserHandle())) {
            return super.newIcon(c, info);
        }
        FastBitmapDrawable dVar = mDynamicClockDrawer.drawIcon(info.iconBitmap);
        dVar.setIsDisabled(info.isDisabled());
        return dVar;
    }
}
