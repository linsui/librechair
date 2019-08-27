/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
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

package ch.deletescape.lawnchair.views

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.support.v4.graphics.ColorUtils
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import ch.deletescape.lawnchair.getColorAttr
import ch.deletescape.lawnchair.getColorEngineAccent
import ch.deletescape.lawnchair.isVisible
import ch.deletescape.lawnchair.tintDrawable
import kotlinx.android.synthetic.lawnchair.app_categorization_type_item.view.*

class SelectableRoundedView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    init {
        val tintSelected = context.getColorEngineAccent()
        val tintNormal = ColorUtils
                .setAlphaComponent(context.getColorAttr(R.attr.colorControlHighlight), 255)
        val tintList = ColorStateList(arrayOf(intArrayOf(R.attr.state_selected), intArrayOf()),
                                      intArrayOf(tintSelected, tintNormal))
        background.setTintList(tintList)
        val rippleTintList =
                ColorStateList(arrayOf(intArrayOf(R.attr.state_selected), intArrayOf()),
                               intArrayOf(ColorUtils.setAlphaComponent(tintSelected, 31),
                                          ColorUtils.setAlphaComponent(tintNormal, 31)))
        (background as RippleDrawable).setColor(rippleTintList)
    }

    fun tintBackground(selected: Int, normal: Int = ColorUtils.setAlphaComponent(
            context.getColorAttr(android.R.attr.colorControlHighlight), 255)) {
        val tintList = ColorStateList(arrayOf(intArrayOf(R.attr.state_selected), intArrayOf()),
                                      intArrayOf(selected, normal))
        background.setTintList(tintList)
        val rippleTintList =
                ColorStateList(arrayOf(intArrayOf(R.attr.state_selected), intArrayOf()),
                               intArrayOf(ColorUtils.setAlphaComponent(selected, 31),
                                          ColorUtils.setAlphaComponent(normal, 31)))
        (background as RippleDrawable).setColor(rippleTintList)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                checkMark.imageTintList = ColorStateList.valueOf(selected)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        checkMark.tintDrawable(context.getColorEngineAccent())
    }

    fun setup(title: String, summary: String) {
        this.title.text = title
        this.summary.text = summary
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        checkMark.isVisible = selected
    }
}
