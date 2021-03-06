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

package ch.deletescape.lawnchair.feed.chips;

import android.content.Context;
import android.util.Pair;

import com.android.launcher3.R;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings("WeakerAccess")
public final class ChipStyleRegistry {
    private static final Map<Pair<Double, String>, ShapeAppearanceModel> MODEL_CACHE = new HashMap<>();
    public static final Map<String, BiFunction<Double, Context, ShapeAppearanceModel>> ALL = new LinkedHashMap<>();
    public static final Map<String, Integer> NAMES = new LinkedHashMap<>();
    public static final String ROUND = "rnd";
    public static final String CUT = "cut";

    static {
        NAMES.put(ROUND, R.string.title_chip_style_round);
        NAMES.put(CUT, R.string.title_chip_style_cut);
    }

    public static void populateWithContext(Context c) {
        ALL.put(ROUND, (aDouble, context) -> {
            if (MODEL_CACHE.get(new Pair<>(aDouble, ROUND)) == null) {
                MODEL_CACHE.put(new Pair<>(aDouble, ROUND),
                        new ShapeAppearanceModel.Builder()
                                .setAllCornerSizes((float) (double) aDouble).setAllCorners(
                                new RoundedCornerTreatment()).build());

            }
            return MODEL_CACHE.get(new Pair<>(aDouble, ROUND));
        });
        ALL.put(CUT, (aDouble, context) -> {
            if (MODEL_CACHE.get(new Pair<>(aDouble, CUT)) == null) {
                MODEL_CACHE.put(new Pair<>(aDouble, CUT),
                        new ShapeAppearanceModel.Builder()
                                .setAllCornerSizes((float) (double) aDouble).setAllCorners(
                                new CutCornerTreatment()).build());

            }
            return MODEL_CACHE.get(new Pair<>(aDouble, CUT));
        });
    }
}
