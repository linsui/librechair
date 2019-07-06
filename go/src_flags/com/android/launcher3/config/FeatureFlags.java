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

package com.android.launcher3.config;

/**
 * Defines UIUpdateHandler set of flags used to control various launcher behaviors
 */
public final class FeatureFlags extends BaseFlags {

    private FeatureFlags() {}

    // Features to control Launcher3Go behavior
    public static final boolean GO_DISABLE_WIDGETS = true;
    public static final boolean LAUNCHER3_SPRING_ICONS = false;

    // When enabled, icons not supporting {@link AdaptiveIconDrawable} will be wrapped in {@link FixedScaleDrawable}.
    public static final boolean LEGACY_ICON_TREATMENT = false;

    // Feature flag to enable moving the QSB on the 0th screen of the workspace.
    public static boolean QSB_ON_FIRST_SCREEN = true;

    // When enabled, app shortcuts are extracted from the package XML.
    public static final boolean LAUNCHER3_BACKPORT_SHORTCUTS = true;


    public static boolean REFLECTION_FORCE_OVERVIEW_MODE = true;

    public static boolean FORCE_FEED_BRIDGE = false;

}
