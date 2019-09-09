package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.graphics.ColorUtils;
import android.view.View;

import com.android.launcher3.AppInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.LauncherExterns;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.plugin.PluginManager;
import com.android.launcher3.plugin.button.ButtonPluginClient;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.Themes;
import com.google.android.apps.nexuslauncher.PredictionUiStateManager.Client;
import com.google.android.apps.nexuslauncher.qsb.QsbAnimationController;
import com.google.android.apps.nexuslauncher.reflection.ReflectionClient;
import com.google.android.apps.nexuslauncher.search.ItemInfoUpdateReceiver;
import com.google.android.apps.nexuslauncher.smartspace.SmartspaceController;
import com.google.android.apps.nexuslauncher.smartspace.SmartspaceView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import ch.deletescape.lawnchair.LawnchairLauncher;
import ch.deletescape.lawnchair.settings.ui.SettingsActivity;

public class NexusLauncher {
    private final Launcher mLauncher;
    final NexusLauncherCallbacks mCallbacks;
    private boolean mFeedRunning;
    private final LauncherExterns mExterns;
    private boolean mRunning;
    private boolean mStarted;
    private final Bundle mUiInformation = new Bundle();
    private ItemInfoUpdateReceiver mItemInfoUpdateReceiver;
    QsbAnimationController mQsbAnimationController;
    private Handler handler = new Handler(LauncherModel.getUiWorkerLooper());

    public NexusLauncher(NexusLauncherActivity activity) {
        mLauncher = activity;
        mExterns = activity;
        mCallbacks = new ShadeCompatLauncherCallbacks();
        mExterns.setLauncherCallbacks(mCallbacks);
    }

    void registerSmartspaceView(SmartspaceView smartspace) {
        mCallbacks.registerSmartspaceView(smartspace);
    }

    public static int primaryColor(WallpaperColorInfo wallpaperColorInfo, Context context,
            int alpha) {
        return compositeAllApps(
                ColorUtils.setAlphaComponent(wallpaperColorInfo.getMainColor(), alpha), context);
    }

    public static int secondaryColor(WallpaperColorInfo wallpaperColorInfo, Context context,
            int alpha) {
        return compositeAllApps(
                ColorUtils.setAlphaComponent(wallpaperColorInfo.getSecondaryColor(), alpha),
                context);
    }

    private static int compositeAllApps(int color, Context context) {
        return ColorUtils
                .compositeColors(Themes.getAttrColor(context, R.attr.allAppsScrimColor), color);
    }

    public boolean isFeedRunning() {
        return mFeedRunning;
    }

    public void setFeedRunning(boolean mFeedRunning) {
        this.mFeedRunning = mFeedRunning;
    }

    public class NexusLauncherCallbacks implements LauncherCallbacks,
            SharedPreferences.OnSharedPreferenceChangeListener,
            WallpaperColorInfo.OnChangeListener {

        private Set<SmartspaceView> mSmartspaceViews = Collections
                .newSetFromMap(new WeakHashMap<>());

        private final Runnable mUpdatePredictionsIfResumed = this::updatePredictionsIfResumed;

        private ItemInfoUpdateReceiver getUpdateReceiver() {
            if (mItemInfoUpdateReceiver == null) {
                mItemInfoUpdateReceiver = new ItemInfoUpdateReceiver(mLauncher, mCallbacks);
            }
            return mItemInfoUpdateReceiver;
        }

        protected final Launcher getLauncher() {
            return mLauncher;
        }

        public void bindAllApplications(final ArrayList<AppInfo> list) {
            getUpdateReceiver().di();
            PredictionUiStateManager.getInstance(mLauncher).dispatchOnChange();
            mLauncher.getUserEventDispatcher().updatePredictions();
        }

        public void dump(final String s, final FileDescriptor fileDescriptor,
                final PrintWriter printWriter, final String[] array) {
            SmartspaceController.get(mLauncher).dumpInfo(s, printWriter);
        }

        public void finishBindingItems(final boolean b) {
        }

        public boolean handleBackPressed() {
            return false;
        }

        public boolean hasCustomContentToLeft() {
            return false;
        }

        public boolean hasSettings() {
            return true;
        }

        public void onActivityResult(final int n, final int n2, final Intent intent) {
        }

        public void onAttachedToWindow() {
            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onAttachedToWindow();
            }
        }

        void registerSmartspaceView(SmartspaceView smartspace) {
            mSmartspaceViews.add(smartspace);
        }

        public void onCreate(final Bundle bundle) {
            SharedPreferences prefs = Utilities.getPrefs(mLauncher);

            prefs.registerOnSharedPreferenceChangeListener(this);

            SmartspaceController.get(mLauncher).sendMessage();

            mQsbAnimationController = new QsbAnimationController(mLauncher);

            mUiInformation.putInt("system_ui_visibility",
                    mLauncher.getWindow().getDecorView().getSystemUiVisibility());
            applyFeedTheme(false);
            WallpaperColorInfo instance = WallpaperColorInfo.getInstance(mLauncher);
            instance.addOnChangeListener(this);
            onExtractedColorsChanged(instance);

            getUpdateReceiver().onCreate();

            PredictionUiStateManager predictionUiStateManager = PredictionUiStateManager
                    .getInstance(mLauncher);
            predictionUiStateManager.setTargetAppsView(mLauncher.getAppsView());
            if (FeatureFlags.REFLECTION_FORCE_OVERVIEW_MODE) {
                predictionUiStateManager.switchClient(Client.OVERVIEW);
            }
        }

        public void onDestroy() {
            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onStop();
            }
            Utilities.getPrefs(mLauncher).unregisterOnSharedPreferenceChangeListener(this);
            WallpaperColorInfo.getInstance(mLauncher).removeOnChangeListener(this);

            getUpdateReceiver().onDestroy();

            PredictionUiStateManager.getInstance(mLauncher).setTargetAppsView(null);
        }

        public void onDetachedFromWindow() {
            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onDetachedFromWindow();
            }
        }

        @Override
        public void onHomeIntent(boolean internalStateHandled) {
            if (mFeedRunning) {
                if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                    ((LawnchairLauncher) mLauncher).getOverlay().getClient().closeOverlay(true);
                }
            }
        }

        public void onLauncherProviderChange() {
            ReflectionClient.getInstance(mLauncher).onProviderChanged();
        }

        public void onPause() {
            mRunning = false;
            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onPause();
            }

            for (SmartspaceView smartspace : mSmartspaceViews) {
                smartspace.onPause();
            }
        }

        public void onRequestPermissionsResult(final int n, final String[] array,
                final int[] array2) {
        }

        public void onResume() {
            mRunning = true;
            if (mStarted) {
                mFeedRunning = true;
            }

            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onResume();
            }

            for (SmartspaceView smartspace : mSmartspaceViews) {
                smartspace.onResume();
            }

            Handler handler = mLauncher.getDragLayer().getHandler();
            if (handler != null) {
                handler.removeCallbacks(mUpdatePredictionsIfResumed);
                Utilities.postAsyncCallback(handler, mUpdatePredictionsIfResumed);
            }
        }

        public void onSaveInstanceState(final Bundle bundle) {
        }

        public void onStart() {
            mStarted = true;
            if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                ((LawnchairLauncher) mLauncher).getOverlay().getClient().onStart();
            }
        }

        public void onStop() {
            mStarted = false;
            if (!mRunning) {
                mFeedRunning = false;
            }
        }

        public void onTrimMemory(int n) {
        }

        public boolean startSearch(String s, boolean b, Bundle bundle) {
            View gIcon = mLauncher.findViewById(R.id.g_icon);
            while (gIcon != null && !gIcon.isClickable()) {
                if (gIcon.getParent() instanceof View) {
                    gIcon = (View) gIcon.getParent();
                } else {
                    gIcon = null;
                }
            }
            if (gIcon != null && gIcon.performClick()) {
//                mExterns.clearTypedText();
                return true;
            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }

        @Override
        public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
            int alpha = mLauncher.getResources()
                    .getInteger(R.integer.extracted_color_gradient_alpha);

            mUiInformation.putInt("background_color_hint",
                    primaryColor(wallpaperColorInfo, mLauncher, alpha));
            mUiInformation.putInt("background_secondary_color_hint",
                    secondaryColor(wallpaperColorInfo, mLauncher, alpha));

            applyFeedTheme(true);
        }

        private void applyFeedTheme(boolean redraw) {
            String prefValue = Utilities.getPrefs(mLauncher)
                    .getString(SettingsActivity.FEED_THEME_PREF, null);
            int feedTheme;
            try {
                feedTheme = Integer.valueOf(prefValue == null ? "1" : prefValue);
            } catch (Exception e) {
                feedTheme = 1;
            }
            boolean auto = (feedTheme & 1) != 0;
            boolean preferDark = (feedTheme & 2) != 0;
            boolean isDark =
                    auto ? Themes.getAttrBoolean(mLauncher, R.attr.isMainColorDark) : preferDark;
            mUiInformation.putBoolean("is_background_dark", isDark);

            if (redraw) {
                if (((LawnchairLauncher) mLauncher).getOverlay() != null) {
                    ((LawnchairLauncher) mLauncher).getOverlay().getClient().putAdditionalParams(
                            mUiInformation);
                }
            }
        }

        private void updatePredictionsIfResumed() {
            if (mLauncher.hasBeenResumed()) {
                ReflectionClient.getInstance(mLauncher).updatePredictionsNow(
                        FeatureFlags.REFLECTION_FORCE_OVERVIEW_MODE ? Client.OVERVIEW.id
                                : Client.HOME.id);
                handler.post(() -> {
                    mLauncher.getUserEventDispatcher().updatePredictions();
                    mLauncher.getUserEventDispatcher().updateActions();
                });
            }
        }
    }

    public class ShadeCompatLauncherCallbacks extends NexusLauncherCallbacks {

        @Override
        public void onHomeIntent(boolean internalStateHandled) {
            super.onHomeIntent(internalStateHandled);
            PluginManager.getInstance(getLauncher()).getClient(ButtonPluginClient.class)
                    .onHomeIntent(intent -> {
                        getLauncher().startActivity(intent);
                        return true;
                    });
        }
    }

}
