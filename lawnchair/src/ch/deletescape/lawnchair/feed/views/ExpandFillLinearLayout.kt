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

package ch.deletescape.lawnchair.feed.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import ch.deletescape.lawnchair.forEachChild
import ch.deletescape.lawnchair.forEachChildIndexed

class ExpandFillLinearLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var childWidth = 0
    var childHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (orientation) {
            VERTICAL -> forEachChild { view ->
                view.layoutParams.width = (parent as? View)?.measuredWidth ?: ViewGroup.LayoutParams.MATCH_PARENT
                view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            HORIZONTAL -> calculateChildSize(widthMeasureSpec, childWidth) { view, size ->
                view.layoutParams.width = size
                view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private inline fun calculateChildSize(sizeSpec: Int, min: Int, crossinline setSize: (View, Int) -> Unit) {
        var available = MeasureSpec.getSize(sizeSpec)
        if (min * childCount >= available || childCount == 0) {
            forEachChild {
                it.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams.height = childHeight
                setSize(it, min)
            }
        } else {
            forEachChildIndexed { it, i ->
                val size = available / (childCount - i)
                setSize(it, size)
                available -= size
            }
        }
    }
}
