package com.android.launcher3.plugin;

import static android.content.Intent.URI_INTENT_SCHEME;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Pair;
import com.android.launcher3.R;
import java.net.URISyntaxException;

public class SerializableShortcut {

    public String mPackageName;
    public ComponentName mActivity;

    public int mIcon;
    public String mId;
    public String mComponentPackage;
    public boolean mEnabled;
    public String mShortLabel;
    public String mLongLabel;
    public String mDisabledMessage;

    public Intent mIntent;

    public SerializableShortcut(Bundle from) throws URISyntaxException {
        String activityTemp = from.getString("activity");
        if (activityTemp != null) {
            mActivity = ComponentName.unflattenFromString(activityTemp);
        }
        mId = from.getString("id");
        mComponentPackage = from.getString("package");
        mEnabled = from.getBoolean("enabled");
        mShortLabel = from.getString("shortLabel");
        mLongLabel = from.getString("longLabel");
        mDisabledMessage = from.getString("disabledMessage");
        mPackageName = mComponentPackage;
        mIntent = Intent.getIntent(from.getString("intent"));
    }

    public Drawable getIcon(Context context, int density) {
        try {
            return context.getPackageManager()
                    .getResourcesForApplication(mPackageName)
                    .getDrawableForDensity(mIcon, density);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException ignored) {
            return context.getResources().getDrawableForDensity(
                    R.drawable.ic_default_shortcut, density);
        }
    }

    public ComponentName getActivity() {
        return mIntent.getComponent() == null ? mActivity : mIntent.getComponent();
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(mId) && !TextUtils.isEmpty(mShortLabel);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public String getKey() {
        return getActivity().flattenToShortString() + "/" + mId;
    }

    public Pair<ComponentName, String> fromKey(String key) {
        return new Pair<>(
                ComponentName.unflattenFromString(key.substring(0, key.lastIndexOf('/') - 1)),
                key.substring(key.lastIndexOf('/')));
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("key", getKey());
        bundle.putString("id", mId);
        bundle.putString("intent", mIntent.toUri(URI_INTENT_SCHEME));
        bundle.putString("package", mComponentPackage);
        bundle.putString("activity", getActivity().flattenToShortString());
        bundle.putString("shortLabel", mShortLabel);
        bundle.putString("longLabel", mLongLabel);
        bundle.putString("disabledMessage", mDisabledMessage);
        bundle.putParcelable("userHandle", Process.myUserHandle());
        bundle.putInt("rank", -1);
        bundle.putBoolean("enabled", mEnabled);
        return bundle;
    }

    public Bitmap getBitmap(Context context, int density) {
        Drawable icon = getIcon(context, density);
        Bitmap bitmap = Bitmap.createBitmap(
                icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        return bitmap;
    }
}