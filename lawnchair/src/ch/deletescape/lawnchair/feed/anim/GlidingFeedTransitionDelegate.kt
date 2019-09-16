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

package ch.deletescape.lawnchair.feed.anim

import android.view.View
import android.view.ViewGroup
import ch.deletescape.lawnchair.rtl

class GlidingFeedTransitionDelegate : AnimationDelegate {
    override fun animate(content: View, background: View, width: Float, progress: Float) {
        if (background.alpha != 1f) {
            background.alpha = 1f
        }
        if (background is ViewGroup && background.rtl) {
            background.translationX = (-1 + progress) * width * -1
            content.translationX = (-1 + progress) * width * -1
        } else {
            background.translationX = (-1 + progress) * width
            content.translationX = (-1 + progress) * width
        }
    }

}