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

package com.android.overlayclient.compat;

import android.os.Bundle;

public class ColorDelegate extends MutableDelegate<Integer> {
    public static final String PRIMARY = "background_color_hint";
    public static final String SECONDARY = "background_secondary_color_hint";
    public static final String TERTIARY = "background_tertiary_color_hint";
    public static final String ACCENT_COLOR = "launcher_user_accent";

    private final String key;

    public ColorDelegate(Integer value, String key) {
        super(value);
        this.key = key;
    }

    @Override
    public void bind(Bundle opt) {
        opt.putInt(key, value);
    }
}
