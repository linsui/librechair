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

import com.android.launcher3.R;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public final class ChipStyleRegistry {
    public static final Map<String, ShapeAppearanceModel> ALL = new LinkedHashMap<>();
    public static final Map<String, Integer> NAMES = new LinkedHashMap<>();
    public static final String ROUND = "rnd";
    public static final String SQUARE = "sqr";
    public static final String ROUNDED_SQUARE = "rqr";
    public static final String CUT = "cut";
    public static final String TRIANGULAR = "trg";

    static {
        NAMES.put(ROUND, R.string.title_chip_style_round);
        NAMES.put(ROUNDED_SQUARE, R.string.icon_shape_rounded_square);
        NAMES.put(SQUARE, R.string.icon_shape_square);
        NAMES.put(CUT, R.string.title_chip_style_cut);
        NAMES.put(TRIANGULAR, R.string.title_chip_style_triangular);
    }

    public static void populateWithContext(Context c) {
        ALL.put(ROUND, new ShapeAppearanceModel.Builder().setAllCorners(
                new RoundedCornerTreatment()).setAllCornerSizes(64f).build());
        ALL.put(ROUNDED_SQUARE, new ShapeAppearanceModel.Builder().setAllCorners(
                new RoundedCornerTreatment()).setAllCornerSizes(8f).build());
        ALL.put(SQUARE, new ShapeAppearanceModel());
        ShapeAppearanceModel cut = new ShapeAppearanceModel.Builder().setAllCornerSizes(
                16f).setAllCorners(new CutCornerTreatment()).build();
        ALL.put(CUT, cut);
        ALL.put(TRIANGULAR, ShapeAppearanceModel.builder().setAllCornerSizes(32f).setAllCorners(
                new CutCornerTreatment()).build());
    }
}
