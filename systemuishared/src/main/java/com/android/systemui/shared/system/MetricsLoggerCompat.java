/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.systemui.shared.system;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

public class MetricsLoggerCompat {

    private final MetricsLogger mMetricsLogger;
    public static final int OVERVIEW_ACTIVITY = MetricsEvent.OVERVIEW_ACTIVITY;

    public MetricsLoggerCompat() {
        mMetricsLogger = new MetricsLogger();
    }

    public void action(int category) {
        try {
        mMetricsLogger.action(category);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public void action(int category, int value) {
        try {
            mMetricsLogger.action(category, value);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public void visible(int category) {
        try {
            mMetricsLogger.visible(category);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public void hidden(int category) {
        try {
            mMetricsLogger.hidden(category);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public void visibility(int category, boolean visible) {
        try {
            mMetricsLogger.visibility(category, visible);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }
}
