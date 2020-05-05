/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.VisibleForTesting;

import com.android.launcher3.util.ConfigMonitor;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.Thunk;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.deletescape.lawnchair.LawnchairPreferences;
import ch.deletescape.lawnchair.settings.IconScale;

import static java.lang.Math.max;

public class InvariantDeviceProfile {

    // We do not need any synchronization for this variable as its only written on UI thread.
    public static final MainThreadInitializedObject<InvariantDeviceProfile> INSTANCE =
            new MainThreadInitializedObject<>((c) -> {
                new ConfigMonitor(c).register();
                return new InvariantDeviceProfile(c);
            });

    private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48;

    // Constants that affects the interpolation curve between statically defined device profile
    // buckets.
    private static float KNEARESTNEIGHBOR = 3;
    private static float WEIGHT_POWER = 5;

    // used to offset float not being able to express extremely small weights in extreme cases.
    private static float WEIGHT_EFFICIENT = 100000f;

    // Profile-defining invariant properties
    String name;
    float minWidthDps;
    float minHeightDps;

    /**
     * Number of icons per row and column in the workspace.
     */
    public int numRows;
    public int numRowsOriginal;
    public int numColumns;
    public int numColumnsOriginal;

    /**
     * Number of icons per row in the drawer
     */
    public int numColsDrawer;
    public int numColsDrawerOriginal;
    public int numPredictions;
    public int numPredictionsOriginal;

    /**
     * Number of icons per row and column in the folder.
     */
    public int numFolderRows;
    public int numFolderColumns;
    public float iconSize;
    public float iconSizeOriginal;
    public float hotseatIconSize;
    public float hotseatIconSizeOriginal;
    public float landscapeIconSize;
    public float landscapeIconSizeOriginal;
    public float landscapeHotseatIconSize;
    public float landscapeHotseatIconSizeOriginal;
    public float allAppsIconSize;
    public float allAppsIconSizeOriginal;
    public float landscapeAllAppsIconSize;
    public float landscapeAllAppsIconSizeOriginal;
    public int iconBitmapSize;
    public int fillResIconDpi;
    public float iconTextSize;

    /**
     * Number of icons inside the hotseat area.
     */
    public int numHotseatIcons;
    public int numHotseatIconsOriginal;

    int defaultLayoutId;
    int demoModeLayoutId;

    public DeviceProfile landscapeProfile;
    public DeviceProfile portraitProfile;

    public Point defaultWallpaperSize;

    @VisibleForTesting
    public InvariantDeviceProfile() {
    }

    private InvariantDeviceProfile(InvariantDeviceProfile p) {
        this(p.name, p.minWidthDps, p.minHeightDps, p.numRows, p.numColumns,
                p.numFolderRows, p.numFolderColumns,
                p.iconSize, p.landscapeIconSize, p.iconTextSize, p.numHotseatIcons,
                p.defaultLayoutId, p.demoModeLayoutId);
    }

    private InvariantDeviceProfile(String n, float w, float h, int r, int c, int fr, int fc,
            float is, float lis, float its, int hs, int dlId, int dmlId) {
        name = n;
        minWidthDps = w;
        minHeightDps = h;
        numRows = r;
        numColumns = c;
        numFolderRows = fr;
        numFolderColumns = fc;
        iconSize = is;
        landscapeIconSize = lis;
        iconTextSize = its;
        numHotseatIcons = hs;
        defaultLayoutId = dlId;
        demoModeLayoutId = dmlId;
    }

    @TargetApi(23)
    public InvariantDeviceProfile(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        Point smallestSize = new Point();
        Point largestSize = new Point();
        display.getCurrentSizeRange(smallestSize, largestSize);

        // This guarantees that width < height
        minWidthDps = Utilities.dpiFromPx(Math.min(smallestSize.x, smallestSize.y), dm);
        minHeightDps = Utilities.dpiFromPx(Math.min(largestSize.x, largestSize.y), dm);

        ArrayList<InvariantDeviceProfile> closestProfiles = findClosestDeviceProfiles(
                minWidthDps, minHeightDps, getPredefinedDeviceProfiles(context));
        InvariantDeviceProfile interpolatedDeviceProfileOut =
                invDistWeightedInterpolate(minWidthDps,  minHeightDps, closestProfiles);

        InvariantDeviceProfile closestProfile = closestProfiles.get(0);
        numRows = closestProfile.numRows;
        numRowsOriginal = numRows;
        numColumns = closestProfile.numColumns;
        numColumnsOriginal = numColumns;
        numColsDrawer = numColumns;
        numColsDrawerOriginal = numColumns;
        numPredictions = numPredictionsOriginal = numColumns;
        numHotseatIcons = closestProfile.numHotseatIcons;
        numHotseatIconsOriginal = numHotseatIcons;
        defaultLayoutId = closestProfile.defaultLayoutId;
        demoModeLayoutId = closestProfile.demoModeLayoutId;
        numFolderRows = closestProfile.numFolderRows;
        numFolderColumns = closestProfile.numFolderColumns;

        iconSize = interpolatedDeviceProfileOut.iconSize;
        iconSizeOriginal = interpolatedDeviceProfileOut.iconSize;
        landscapeIconSize = interpolatedDeviceProfileOut.landscapeIconSize;
        landscapeIconSizeOriginal = interpolatedDeviceProfileOut.landscapeIconSize;
        hotseatIconSize = interpolatedDeviceProfileOut.iconSize;
        hotseatIconSizeOriginal = interpolatedDeviceProfileOut.iconSize;
        landscapeHotseatIconSize = interpolatedDeviceProfileOut.landscapeIconSize;
        landscapeHotseatIconSizeOriginal = interpolatedDeviceProfileOut.landscapeIconSize;
        allAppsIconSize = interpolatedDeviceProfileOut.iconSize;
        allAppsIconSizeOriginal = interpolatedDeviceProfileOut.iconSize;
        landscapeAllAppsIconSize = interpolatedDeviceProfileOut.landscapeIconSize;
        landscapeAllAppsIconSizeOriginal = interpolatedDeviceProfileOut.landscapeIconSize;
        iconTextSize = interpolatedDeviceProfileOut.iconTextSize;

        new IconScale(Utilities.getLawnchairPrefs(context), "iconSize", this);
        new IconScale(Utilities.getLawnchairPrefs(context), "allAppsIconSize", this);
        new IconScale(Utilities.getLawnchairPrefs(context), "hotseatIconSize", "iconSize",this);

        // Initialize these *after* the icon scale has been applied, this ensures we load icons of proper resolution
        iconBitmapSize = Utilities.pxFromDp(max(max(iconSize, allAppsIconSize), hotseatIconSize), dm);
        fillResIconDpi = getLauncherIconDensity(iconBitmapSize);

        // If the partner customization apk contains any grid overrides, apply them
        // Supported overrides: numRows, numColumns, iconSize
        applyPartnerDeviceProfileOverrides(context, dm);

        Point realSize = new Point();
        display.getRealSize(realSize);
        // The real size never changes. smallSide and largeSide will remain the
        // same in any orientation.
        int smallSide = Math.min(realSize.x, realSize.y);
        int largeSide = max(realSize.x, realSize.y);

        landscapeProfile = new DeviceProfile(context, this, smallestSize, largestSize,
                largeSide, smallSide, true /* isLandscape */, false /* isMultiWindowMode */);
        portraitProfile = new DeviceProfile(context, this, smallestSize, largestSize,
                smallSide, largeSide, false /* isLandscape */, false /* isMultiWindowMode */);

        // We need to ensure that there is enough extra space in the wallpaper
        // for the intended parallax effects
        if (context.getResources().getConfiguration().smallestScreenWidthDp >= 720) {
            defaultWallpaperSize = new Point(
                    (int) (largeSide * wallpaperTravelToScreenWidthRatio(largeSide, smallSide)),
                    largeSide);
        } else {
            defaultWallpaperSize = new Point(max(smallSide * 2, largeSide), largeSide);
        }
    }

    ArrayList<InvariantDeviceProfile> getPredefinedDeviceProfiles(Context context) {
        ArrayList<InvariantDeviceProfile> profiles = new ArrayList<>();
        try (XmlResourceParser parser = context.getResources().getXml(R.xml.device_profiles)) {
            final int depth = parser.getDepth();
            int type;

            while (((type = parser.next()) != XmlPullParser.END_TAG ||
                    parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
                if ((type == XmlPullParser.START_TAG) && "profile".equals(parser.getName())) {
                    TypedArray a = context.obtainStyledAttributes(
                            Xml.asAttributeSet(parser), R.styleable.InvariantDeviceProfile);
                    int numRows = a.getInt(R.styleable.InvariantDeviceProfile_numRows, 0);
                    int numColumns = a.getInt(R.styleable.InvariantDeviceProfile_numColumns, 0);
                    float iconSize = a.getFloat(R.styleable.InvariantDeviceProfile_profileIconSize, 0);
                    profiles.add(new InvariantDeviceProfile(
                            a.getString(R.styleable.InvariantDeviceProfile_name),
                            a.getFloat(R.styleable.InvariantDeviceProfile_minWidthDps, 0),
                            a.getFloat(R.styleable.InvariantDeviceProfile_minHeightDps, 0),
                            numRows,
                            numColumns,
                            a.getInt(R.styleable.InvariantDeviceProfile_numFolderRows, numRows),
                            a.getInt(R.styleable.InvariantDeviceProfile_numFolderColumns, numColumns),
                            iconSize,
                            a.getFloat(R.styleable.InvariantDeviceProfile_landscapeIconSize, iconSize),
                            a.getFloat(R.styleable.InvariantDeviceProfile_iconTextSize, 0),
                            a.getInt(R.styleable.InvariantDeviceProfile_numHotseatIcons, numColumns),
                            a.getResourceId(R.styleable.InvariantDeviceProfile_defaultLayoutId, 0),
                            a.getResourceId(R.styleable.InvariantDeviceProfile_demoModeLayoutId, 0)));
                    a.recycle();
                }
            }
        } catch (IOException|XmlPullParserException e) {
            throw new RuntimeException(e);
        }
        return profiles;
    }

    private int getLauncherIconDensity(int requiredSize) {
        // Densities typically defined by an app.
        int[] densityBuckets = new int[] {
                DisplayMetrics.DENSITY_LOW,
                DisplayMetrics.DENSITY_MEDIUM,
                DisplayMetrics.DENSITY_TV,
                DisplayMetrics.DENSITY_HIGH,
                DisplayMetrics.DENSITY_XHIGH,
                DisplayMetrics.DENSITY_XXHIGH,
                DisplayMetrics.DENSITY_XXXHIGH
        };

        int density = DisplayMetrics.DENSITY_XXXHIGH;
        for (int i = densityBuckets.length - 1; i >= 0; i--) {
            float expectedSize = ICON_SIZE_DEFINED_IN_APP_DP * densityBuckets[i]
                    / DisplayMetrics.DENSITY_DEFAULT;
            if (expectedSize >= requiredSize) {
                density = densityBuckets[i];
            }
        }

        return density;
    }

    /**
     * Apply any Partner customization grid overrides.
     *
     * Currently we support: all apps row / column count.
     */
    private void applyPartnerDeviceProfileOverrides(Context context, DisplayMetrics dm) {
        Partner p = Partner.get(context.getPackageManager());
        if (p != null) {
            p.applyInvariantDeviceProfileOverrides(this, dm);
        }
    }

    @Thunk float dist(float x0, float y0, float x1, float y1) {
        return (float) Math.hypot(x1 - x0, y1 - y0);
    }

    /**
     * Returns the closest device profiles ordered by closeness to the specified width and height
     */
    // Package private visibility for testing.
    ArrayList<InvariantDeviceProfile> findClosestDeviceProfiles(
            final float width, final float height, ArrayList<InvariantDeviceProfile> points) {

        // Sort the profiles by their closeness to the dimensions
        ArrayList<InvariantDeviceProfile> pointsByNearness = points;
        Collections.sort(pointsByNearness, new Comparator<InvariantDeviceProfile>() {
            public int compare(InvariantDeviceProfile a, InvariantDeviceProfile b) {
                return Float.compare(dist(width, height, a.minWidthDps, a.minHeightDps),
                        dist(width, height, b.minWidthDps, b.minHeightDps));
            }
        });

        return pointsByNearness;
    }

    // Package private visibility for testing.
    InvariantDeviceProfile invDistWeightedInterpolate(float width, float height,
                ArrayList<InvariantDeviceProfile> points) {
        float weights = 0;

        InvariantDeviceProfile p = points.get(0);
        if (dist(width, height, p.minWidthDps, p.minHeightDps) == 0) {
            return p;
        }

        InvariantDeviceProfile out = new InvariantDeviceProfile();
        for (int i = 0; i < points.size() && i < KNEARESTNEIGHBOR; ++i) {
            p = new InvariantDeviceProfile(points.get(i));
            float w = weight(width, height, p.minWidthDps, p.minHeightDps, WEIGHT_POWER);
            weights += w;
            out.add(p.multiply(w));
        }
        return out.multiply(1.0f/weights);
    }

    private void add(InvariantDeviceProfile p) {
        iconSize += p.iconSize;
        landscapeIconSize += p.landscapeIconSize;
        allAppsIconSize += p.allAppsIconSize;
        landscapeAllAppsIconSize += p.landscapeAllAppsIconSize;
        iconTextSize += p.iconTextSize;
    }

    private InvariantDeviceProfile multiply(float w) {
        iconSize *= w;
        landscapeIconSize *= w;
        allAppsIconSize *= w;
        landscapeAllAppsIconSize *= w;
        iconTextSize *= w;
        return this;
    }

    public DeviceProfile getDeviceProfile(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ? landscapeProfile : portraitProfile;
    }

    private float weight(float x0, float y0, float x1, float y1, float pow) {
        float d = dist(x0, y0, x1, y1);
        if (Float.compare(d, 0f) == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (WEIGHT_EFFICIENT / Math.pow(d, pow));
    }

    /**
     * As a ratio of screen height, the total distance we want the parallax effect to span
     * horizontally
     */
    private static float wallpaperTravelToScreenWidthRatio(int width, int height) {
        float aspectRatio = width / (float) height;

        // At an aspect ratio of 16/10, the wallpaper parallax effect should span 1.5 * screen width
        // At an aspect ratio of 10/16, the wallpaper parallax effect should span 1.2 * screen width
        // We will use these two data points to extrapolate how much the wallpaper parallax effect
        // to span (ie travel) at any aspect ratio:

        final float ASPECT_RATIO_LANDSCAPE = 16/10f;
        final float ASPECT_RATIO_PORTRAIT = 10/16f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE = 1.5f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT = 1.2f;

        // To find out the desired width at different aspect ratios, we use the following two
        // formulas, where the coefficient on x is the aspect ratio (width/height):
        //   (16/10)x + y = 1.5
        //   (10/16)x + y = 1.2
        // We solve for x and y and end up with a final formula:
        final float x =
                (WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE - WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT) /
                        (ASPECT_RATIO_LANDSCAPE - ASPECT_RATIO_PORTRAIT);
        final float y = WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT - x * ASPECT_RATIO_PORTRAIT;
        return x * aspectRatio + y;
    }

    public void onDockStyleChanged(LawnchairPreferences prefs) {
        portraitProfile.onValueChanged("", prefs, false);
        landscapeProfile.onValueChanged("", prefs, false);
    }

}