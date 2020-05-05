/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.launcher3.config;

import com.android.launcher3.BuildConfig;

/**
 * Defines a set of flags used to control various launcher behaviors.
 *
 * All the flags should be defined here with appropriate default values. To override a value,
 * redefine it in {@link FeatureFlags}.
 *
 * This class is kept package-private to prevent direct access.
 */
abstract class BaseFlags {

    BaseFlags() {}

    public static final boolean IS_DOGFOOD_BUILD = false;
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".settings".intern();

    // When enabled the promise icon is visible in all apps while installation an app.
    public static final boolean LAUNCHER3_PROMISE_APPS_IN_ALL_APPS = false;

    // Feature flag to enable moving the QSB on the 0th screen of the workspace.
    public static final boolean QSB_ON_FIRST_SCREEN = true;

    // When true, custom widgets are loaded using CustomWidgetParser.
    public static final boolean ENABLE_CUSTOM_WIDGETS = true;

    // Features to control Launcher3Go behavior
    public static final boolean GO_DISABLE_WIDGETS = false;

    // When enabled shows a work profile tab in all apps
    public static final boolean ALL_APPS_TABS_ENABLED = true;

    // When true, overview shows screenshots in the orientation they were taken rather than
    // trying to make them fit the orientation the device is in.
    public static final boolean OVERVIEW_USE_SCREENSHOT_ORIENTATION = true;
}
