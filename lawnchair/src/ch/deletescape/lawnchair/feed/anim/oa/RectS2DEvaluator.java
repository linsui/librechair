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
/*
 * Copyright (C) 2013 The Android Open Source Project
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
package ch.deletescape.lawnchair.feed.anim.oa;

import android.animation.TypeEvaluator;
import android.graphics.Rect;

/**
 * This evaluator can be used to perform type interpolation between <code>Rect</code> values.
 */
public class RectS2DEvaluator implements TypeEvaluator<Rect> {
    private Rect mRect;
    private boolean direction;

    public RectS2DEvaluator() {
        this.direction = false;
    }

    public RectS2DEvaluator(boolean direction) {
        this.direction = true;
    }

    @SuppressWarnings("unused")
    public RectS2DEvaluator(Rect reuseRect) {
        mRect = reuseRect;
    }

    @Override
    public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
        int left = startValue.left + (int) ((endValue.left - startValue.left) * (direction ? 1f - fraction : fraction) * 2);
        int top = startValue.top + (int) ((endValue.top - startValue.top) * (direction ? 1f - fraction : fraction));
        int right = startValue.right + (int) ((endValue.right - startValue.right) * (direction ? 1f - fraction : fraction) * 2);
        int bottom = startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * (direction ? 1f - fraction : fraction));
        if (mRect == null) {
            return new Rect(left, top, right, bottom);
        } else {
            mRect.set(left, top, right, bottom);
            return mRect;
        }
    }
}
