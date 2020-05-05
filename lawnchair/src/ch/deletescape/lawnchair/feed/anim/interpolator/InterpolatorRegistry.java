/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.anim.interpolator;

import android.view.animation.Interpolator;

import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;

import java.util.LinkedHashMap;

import ch.deletescape.lawnchair.feed.util.FeedUtil;

public final class InterpolatorRegistry {
    private InterpolatorRegistry() {
        throw new Error("this class cannot be instantiated");
    }

    public static final LinkedHashMap<String, Interpolator> ALL = FeedUtil.apply(
            new LinkedHashMap<>(), map -> {
                map.put("linear", Interpolators.LINEAR);
                map.put("accel", Interpolators.ACCEL);
                map.put("snappy_accel", Interpolators.ACCEL_2);
                map.put("deaccel", Interpolators.DEACCEL);
                map.put("snappy_deaccel", Interpolators.DEACCEL_2);
                map.put("ease_in_out", Interpolators.AGGRESSIVE_EASE_IN_OUT);
            });
    
    public static final LinkedHashMap<String, Integer> NAMES = FeedUtil.apply(
            new LinkedHashMap<>(), map -> {
                map.put("linear", R.string.title_interpolator_linear);
                map.put("accel", R.string.title_interpolator_accelerating);
                map.put("snappy_accel", R.string.title_interpolator_snappy_accelerating);
                map.put("deaccel", R.string.title_interpolator_decelerating);
                map.put("snappy_deaccel", R.string.title_interpolator_snappy_decelerating);
                map.put("ease_in_out", R.string.title_interpolator_ease_in_out);
            });
}
