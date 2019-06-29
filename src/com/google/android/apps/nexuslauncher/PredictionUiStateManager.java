/*
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
 *     You should have received a
 *  copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Adapted from smali
 * PredictionUiStateManager$AppPredictionConsumer.smali
 * PredictionUiStateManager$Client.smali
 * PredictionUiStateManager$PredictionState.smali
 * PredictionUiStateManager.smali
 */

/*
 * Contains lambdas adapted from smali
 * UIUpdateHandler.smali
 * -$$Lambda$DlDqw4RH6m7hcgHZ_OpdKc6yQzE.smali
 */

/*
 * Credits to Google (mainly)
 */

package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.IconCache.ItemInfoUpdateReceiver;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Preconditions;
import com.google.android.apps.nexuslauncher.a.a;
import com.google.android.apps.nexuslauncher.b.b;
import com.google.android.apps.nexuslauncher.reflection.ReflectionTimeHelper;
import com.google.android.apps.nexuslauncher.reflection.alpha.PredictionUsageHelper;
import com.google.android.apps.nexuslauncher.util.ComponentKeyMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PredictionUiStateManager implements OnSharedPreferenceChangeListener, OnGlobalLayoutListener, ItemInfoUpdateReceiver {
    private static PredictionUiStateManager sInstance;
    private PredictionUiStateManager.Client mActiveClient;
    private AllAppsContainerView mAppsView;
    private final Context mContext;
    private PredictionUiStateManager.PredictionState mCurrentState;
    private final IconCache mIconCache;
    private final b mInstantAppsController;
    private final SharedPreferences mMainPrefs;
    private final int mMaxIconsPerRow;
    private PredictionUiStateManager.PredictionState mPendingState;
    private final SharedPreferences mPrivatePrefs;
    private final a mShortcutPredictionsController;

    private PredictionUiStateManager(Context var1) {
        this.mContext = var1;
        this.mMainPrefs = Utilities.getPrefs(var1);
        this.mPrivatePrefs = ReflectionTimeHelper.d(var1);
        this.mMaxIconsPerRow = LauncherAppState.getIDP(var1).numColumns;
        this.mInstantAppsController = b.b(var1);
        this.mShortcutPredictionsController = a.a(var1);
        this.mIconCache = LauncherAppState.getInstance(var1).getIconCache();
        this.mActiveClient = PredictionUiStateManager.Client.HOME;
        this.mCurrentState = this.parseLastState();
        this.mMainPrefs.registerOnSharedPreferenceChangeListener(this);
        this.mPrivatePrefs.registerOnSharedPreferenceChangeListener(this);
        /* Adapted from smali -$$Lambda$DlDqw4RH6m7hcgHZ_OpdKc6yQzE.smali */
        this.mInstantAppsController.K = () -> dispatchOnChange();
        /* Adapted from smali -$$Lambda$DlDqw4RH6m7hcgHZ_OpdKc6yQzE.smali */
        this.mShortcutPredictionsController.e = () -> dispatchOnChange();
    }

    private void applyState(PredictionUiStateManager.PredictionState var1) {
        boolean var2 = this.mCurrentState.isEnabled;
        this.mCurrentState = var1;
        if (this.mAppsView != null) {
            ((PredictionUiStateManager.AppPredictionConsumer)this.mAppsView.getFloatingHeaderView()).setPredictedApps(this.mCurrentState.isEnabled, this.mCurrentState.apps);
            if (var2 != this.mCurrentState.isEnabled) {
                Launcher.getLauncher(this.mAppsView.getContext()).getStateManager().reapplyState(true);
            }
        }

    }

    private boolean canApplyPredictions(PredictionUiStateManager.PredictionState var1) {
        if (this.mAppsView == null) {
            return true;
        } else {
            Launcher var2 = Launcher.getLauncher(this.mAppsView.getContext());
            if (this.mAppsView.isShown() && !var2.isForceInvisible()) {
                if (this.mCurrentState.isEnabled == var1.isEnabled && this.mCurrentState.apps.isEmpty() == var1.apps.isEmpty()) {
                    if (var2.getDeviceProfile().isVerticalBarLayout()) {
                        return false;
                    } else if (!var2.isInState(LauncherState.OVERVIEW)) {
                        return false;
                    } else {
                        return var2.getAllAppsController().getProgress() > 1.0F;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    private void dispatchOnChange(boolean var1) {
        PredictionUiStateManager.PredictionState var2;
        if (var1) {
            var2 = this.parseLastState();
        } else if (this.mPendingState == null) {
            var2 = this.mCurrentState;
        } else {
            var2 = this.mPendingState;
        }

        if (var1 && this.mAppsView != null && !this.canApplyPredictions(var2)) {
            this.scheduleApplyPredictedApps(var2);
        } else {
            this.applyState(var2);
        }
    }

    public static PredictionUiStateManager getInstance(Context var0) {
        Preconditions.assertUIThread();
        if (sInstance == null) {
            sInstance = new PredictionUiStateManager(var0.getApplicationContext());
        }

        return sInstance;
    }

    private PredictionUiStateManager.PredictionState parseLastState() {
        PredictionUiStateManager.PredictionState var1 = new PredictionUiStateManager.PredictionState();
        var1.isEnabled = this.mMainPrefs.getBoolean("pref_show_predictions", true);
        if (!var1.isEnabled) {
            var1.apps = Collections.EMPTY_LIST;
            var1.orderedApps = Collections.EMPTY_LIST;
            return var1;
        } else {
            var1.apps = new ArrayList();
            var1.orderedApps = new ArrayList();
            String var2 = this.mPrivatePrefs.getString(this.mActiveClient.mKeyConfg.bc, (String)null);
            boolean var3 = TextUtils.isEmpty(var2);
            byte var4 = 0;
            int var5;
            int var6;
            ComponentKey var7;
            String[] var8;
            if (!var3) {
                var8 = var2.split(";");
                var5 = var8.length;

                for(var6 = 0; var6 < var5; ++var6) {
                    var7 = com.google.android.apps.nexuslauncher.util.a.a(var8[var6], this.mContext);
                    if (var7 != null) {
                        var1.orderedApps.add(var7);
                    }
                }
            }

            var2 = this.mPrivatePrefs.getString(this.mActiveClient.mKeyConfg.aZ, (String)null);
            if (!TextUtils.isEmpty(var2)) {
                var8 = var2.split(";");
                var5 = var8.length;

                for(var6 = var4; var6 < var5; ++var6) {
                    var7 = com.google.android.apps.nexuslauncher.util.a.a(var8[var6], this.mContext);
                    if (var7 != null) {
                        var1.apps.add(new ComponentKeyMapper(this.mContext, var7));
                    }
                }
            }

            this.updateDependencies(var1);
            return var1;
        }
    }

    private void scheduleApplyPredictedApps(PredictionUiStateManager.PredictionState var1) {
        boolean var2;
        if (this.mPendingState == null) {
            var2 = true;
        } else {
            var2 = false;
        }

        this.mPendingState = var1;
        if (var2) {
            this.mAppsView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

    }

    private void updateDependencies(PredictionUiStateManager.PredictionState var1) {
        if (var1.isEnabled && this.mAppsView != null) {
            ArrayList instantAppList = new ArrayList();
            ArrayList var3 = new ArrayList();
            int var4 = var1.apps.size();
            int var5 = 0;

            int var8;
            for(int var6 = 0; var5 < var4 && var6 < this.mMaxIconsPerRow; var6 = var8) {
                label36: {
                    ComponentKeyMapper var7 = (ComponentKeyMapper)var1.apps.get(var5);
                    if ("@instantapp".equals(var7.getComponentClass())) {
                        instantAppList.add(var7.getPackage());
                    } else {
                        AppInfo var11 = (AppInfo)var7.getApp(this.mAppsView.getAppsStore());
                        var8 = var6;
                        if (var11 == null) {
                            break label36;
                        }

                        if (var11.usingLowResIcon) {
                            this.mIconCache.updateIconInBackground(this, var11);
                        }
                    }

                    var8 = var6 + 1;
                }

                ++var5;
            }

            b var9 = this.mInstantAppsController;
            if (!instantAppList.isEmpty()) {
                Message.obtain(var9.b, 1, instantAppList).sendToTarget();
            }

            a var10 = this.mShortcutPredictionsController;
            if (!var3.isEmpty()) {
                Message.obtain(var10.b, 0, var3).sendToTarget();
            }

        }
    }

    public boolean arePredictionsEnabled() {
        return this.mCurrentState.isEnabled;
    }

    public void dispatchOnChange() {
        this.dispatchOnChange(false);
    }

    public PredictionUiStateManager.Client getClient() {
        return this.mActiveClient;
    }

    public PredictionUiStateManager.PredictionState getCurrentState() {
        return this.mCurrentState;
    }

    public void onGlobalLayout() {
        if (this.mAppsView != null) {
            if (this.mPendingState != null && this.canApplyPredictions(this.mPendingState)) {
                this.applyState(this.mPendingState);
                this.mPendingState = null;
            }

            if (this.mPendingState == null) {
                this.mAppsView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        }
    }

    public void onSharedPreferenceChanged(SharedPreferences var1, String var2) {
        if ("pref_show_predictions".equals(var2) || this.mActiveClient.mKeyConfg.aZ.equals(var2) || this.mActiveClient.mKeyConfg.bc.equals(var2)) {
            this.dispatchOnChange(true);
        }

    }

    public void reapplyItemInfo(ItemInfoWithIcon var1) {
    }

    public void setTargetAppsView(AllAppsContainerView var1) {
        this.mAppsView = var1;
        if (this.mPendingState != null) {
            this.applyState(this.mPendingState);
            this.mPendingState = null;
        } else {
            this.applyState(this.mCurrentState);
        }

        this.updateDependencies(this.mCurrentState);
    }

    public void switchClient(PredictionUiStateManager.Client var1) {
        if (var1 != this.mActiveClient) {
            this.mActiveClient = var1;
            this.dispatchOnChange(true);
        }
    }

    public interface AppPredictionConsumer {
        void setPredictedApps(boolean var1, List<ComponentKeyMapper> var2);
    }

    public static enum Client {
        HOME("GEL"),
        OVERVIEW("OVERVIEW_GEL");

        public final String id;
        private final PredictionUsageHelper mKeyConfg;

        private Client(String var3) {
            this.id = var3;
            this.mKeyConfg = PredictionUsageHelper.d(var3);
        }
    }

    public static class PredictionState {
        public List<ComponentKeyMapper> apps;
        public boolean isEnabled;
        public List<ComponentKey> orderedApps;

        public PredictionState() {
        }
    }
}
