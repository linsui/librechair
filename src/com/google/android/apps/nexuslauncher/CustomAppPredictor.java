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

package com.google.android.apps.nexuslauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.android.launcher3.AppFilter;
import com.android.launcher3.AppInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.util.ComponentKey;

import com.google.android.apps.nexuslauncher.util.ComponentKeyMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.deletescape.lawnchair.settings.ui.SettingsActivity;

public class CustomAppPredictor extends UserEventDispatcher implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected static final int MAX_PREDICTIONS = 12;
    private static final int BOOST_ON_OPEN = 9;
    private static final String PREDICTION_SET = "pref_prediction_set";
    private static final String PREDICTION_PREFIX = "pref_prediction_count_";
    private static final String HIDDEN_PREDICTIONS_SET_PREF = "pref_hidden_prediction_set";
    private static final Set<String> EMPTY_SET = new HashSet<>();
    private final Context mContext;
    protected final AppFilter mAppFilter;
    private final SharedPreferences mPrefs;
    private final PackageManager mPackageManager;

    protected final static String[] PLACE_HOLDERS = new String[] {
            "org.fdroid.fdroid",
            "org.chromium.chromium",
            "com.android.settings",
            "com.android.gallery3d",
            "org.lineageos.snap",
            "com.android.camera2",
            "org.lineageos.jelly",
            "com.android.browser",
            "com.android.calculator2" /* LIBRE-CHANGED: remove Google app placeholders */
    };

    private final UiManager mUiManager;

    public CustomAppPredictor(Context context) {
        mContext = context;
        mAppFilter = AppFilter.newInstance(mContext);
        mPrefs = Utilities.getPrefs(context);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mPackageManager = context.getPackageManager();
        mUiManager = new UiManager(this);
    }

    public List<ComponentKeyMapper> getPredictions() {
        List<ComponentKeyMapper> list = new ArrayList<>();
        if (isPredictorEnabled()) {
            clearNonExistingComponents();

            List<String> predictionList = new ArrayList<>(getStringSetCopy());

            Collections.sort(predictionList, (o1, o2) -> Integer.compare(getLaunchCount(o2), getLaunchCount(o1)));

            for (String prediction : predictionList) {
                ComponentKeyMapper keyMapper = getComponentFromString(prediction);
                if (!isHiddenApp(mContext, keyMapper.getComponentKey())) {
                    list.add(keyMapper);
                }
            }

            for (int i = 0; i < PLACE_HOLDERS.length && list.size() < MAX_PREDICTIONS; i++) {
                String placeHolder = PLACE_HOLDERS[i];
                Intent intent = mPackageManager.getLaunchIntentForPackage(placeHolder);
                if (intent != null) {
                    ComponentName componentInfo = intent.getComponent();
                    if (componentInfo != null) {
                        ComponentKey key = new ComponentKey(componentInfo, Process.myUserHandle());
                        if (!predictionList.contains(key.toString()) && !isHiddenApp(mContext,
                                key)) {
                            list.add(new ComponentKeyMapper(mContext, key));
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void logAppLaunch(View v, Intent intent, UserHandle user) {
        super.logAppLaunch(v, intent, user);
        if (isPredictorEnabled() && recursiveIsDrawer(v)) {
            ComponentName componentInfo = intent.getComponent();
            if (componentInfo != null && mAppFilter.shouldShowApp(componentInfo, user)) {
                clearNonExistingComponents();
                
                Set<String> predictionSet = getStringSetCopy();
                SharedPreferences.Editor edit = mPrefs.edit();

                String prediction = new ComponentKey(componentInfo, user).toString();
                if (predictionSet.contains(prediction)) {
                    edit.putInt(PREDICTION_PREFIX + prediction, getLaunchCount(prediction) + BOOST_ON_OPEN);
                } else if (predictionSet.size() < MAX_PREDICTIONS || decayHasSpotFree(predictionSet, edit)) {
                    predictionSet.add(prediction);
                }

                edit.putStringSet(PREDICTION_SET, predictionSet);
                edit.apply();

                mUiManager.onPredictionsUpdated();
            }
        }
    }

    private boolean decayHasSpotFree(Set<String> toDecay, SharedPreferences.Editor edit) {
        boolean spotFree = false;
        Set<String> toRemove = new HashSet<>();
        for (String prediction : toDecay) {
            int launchCount = getLaunchCount(prediction);
            if (launchCount > 0) {
                edit.putInt(PREDICTION_PREFIX + prediction, --launchCount);
            } else if (!spotFree) {
                edit.remove(PREDICTION_PREFIX + prediction);
                toRemove.add(prediction);
                spotFree = true;
            }
        }
        for (String prediction : toRemove) {
            toDecay.remove(prediction);
        }
        return spotFree;
    }

    /**
     * Zero-based launch count of UIUpdateHandler shortcut
     * @param component serialized component
     * @return the number of launches, at least zero
     */
    private int getLaunchCount(String component) {
        return mPrefs.getInt(PREDICTION_PREFIX + component, 0);
    }

    protected boolean recursiveIsDrawer(View v) {
        if (v != null) {
            ViewParent parent = v.getParent();
            while (parent != null) {
                if (parent instanceof AllAppsContainerView) {
                    return true;
                }
                parent = parent.getParent();
            }
        }
        return false;
    }

    protected boolean isPredictorEnabled() {
        return Utilities.getPrefs(mContext).getBoolean(SettingsActivity.SHOW_PREDICTIONS_PREF, true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.SHOW_PREDICTIONS_PREF)) {
            if (!isPredictorEnabled()) {
                Set<String> predictionSet = getStringSetCopy();

                SharedPreferences.Editor edit = mPrefs.edit();
                for (String prediction : predictionSet) {
                    Log.i("Predictor", "Clearing " + prediction + " at " + getLaunchCount(prediction));
                    edit.remove(PREDICTION_PREFIX + prediction);
                }
                edit.putStringSet(PREDICTION_SET, EMPTY_SET);
                edit.apply();
            }

            mUiManager.onPredictionsUpdated();
        } else if (key.equals(HIDDEN_PREDICTIONS_SET_PREF)) {
            mUiManager.onPredictionsUpdated();
        }
    }

    protected ComponentKeyMapper getComponentFromString(String str) {
        return new ComponentKeyMapper(mContext, new ComponentKey(mContext, str));
    }

    private void clearNonExistingComponents() {
        Set<String> originalSet = mPrefs.getStringSet(PREDICTION_SET, EMPTY_SET);
        Set<String> predictionSet = new HashSet<>(originalSet);

        SharedPreferences.Editor edit = mPrefs.edit();
        for (String prediction : originalSet) {
            ComponentName cn = new ComponentKey(mContext, prediction).componentName;
            try {
                mPackageManager.getActivityInfo(cn, 0);
            } catch (PackageManager.NameNotFoundException e) {
                predictionSet.remove(prediction);
                edit.remove(PREDICTION_PREFIX + prediction);
                Intent intent = mPackageManager.getLaunchIntentForPackage(cn.getPackageName());
                if (intent != null) {
                    ComponentName componentInfo = intent.getComponent();
                    if (componentInfo != null) {
                        ComponentKey key = new ComponentKey(componentInfo, Process.myUserHandle());
                        predictionSet.add(key.toString());
                    }
                }
            }
        }

        edit.putStringSet(PREDICTION_SET, predictionSet);
        edit.apply();
    }

    private Set<String> getStringSetCopy() {
        return new HashSet<>(mPrefs.getStringSet(PREDICTION_SET, EMPTY_SET));
    }

    static void setComponentNameState(Context context, ComponentKey key, boolean hidden) {
        String comp = key.toString();
        Set<String> hiddenApps = getHiddenApps(context);
        while (hiddenApps.contains(comp)) {
            hiddenApps.remove(comp);
        }
        if (hidden) {
            hiddenApps.add(comp);
        }
        setHiddenApps(context, hiddenApps);
    }

    protected static boolean isHiddenApp(Context context, ComponentKey key) {
        return getHiddenApps(context).contains(key.toString());
    }

    @SuppressWarnings("ConstantConditions") // This can't be null anyway
    private static Set<String> getHiddenApps(Context context) {
        return new HashSet<>(Utilities.getLawnchairPrefs(context).getHiddenPredictionAppSet());
    }

    private static void setHiddenApps(Context context, Set<String> hiddenApps) {
        Utilities.getLawnchairPrefs(context).setHiddenPredictionAppSet(hiddenApps);
    }

    public UiManager getUiManager() {
        return mUiManager;
    }

    public static class UiManager {

        private final CustomAppPredictor mPredictor;
        private final List<Listener> mListeners = new ArrayList<>();

        private UiManager(CustomAppPredictor predictor) {
            mPredictor = predictor;
        }

        public void addListener(Listener listener) {
            mListeners.add(listener);
            listener.onPredictionsUpdated();
        }

        public void removeListener(Listener listener) {
            mListeners.remove(listener);
        }

        public boolean isEnabled() {
            return mPredictor.isPredictorEnabled();
        }

        public List<ComponentKeyMapper> getPredictions() {
            return mPredictor.getPredictions();
        }

        public /* private */ void onPredictionsUpdated() {
            for (Listener listener : mListeners) {
                listener.onPredictionsUpdated();
            }
        }

        public interface Listener {

            void onPredictionsUpdated();
        }
    }
}
