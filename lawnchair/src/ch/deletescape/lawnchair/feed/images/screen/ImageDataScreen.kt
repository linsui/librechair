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

package ch.deletescape.lawnchair.feed.images.screen

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.ProviderScreen
import ch.deletescape.lawnchair.font.CustomFontManager
import ch.deletescape.lawnchair.inflate
import com.android.launcher3.R
import kotlinx.android.synthetic.lawnchair.image_information_screen.view.*

class ImageDataScreen(base: Context, val bitmap: Bitmap, val desc: String, val actionClickListener: (() -> Unit)?)
    : ProviderScreen(base) {
    override fun getView(parent: ViewGroup) = parent.inflate(R.layout.image_information_screen)

    override fun bindView(view: View) {
        view.image.setImageBitmap(bitmap)
        view.image_description.text = desc
        view.more.let { mb ->
            mb.visibility = if (actionClickListener != null) View.VISIBLE else View.GONE
            mb.setOnClickListener {
                actionClickListener?.invoke()
            }
            mb.backgroundTintList = ColorStateList.valueOf(FeedAdapter.getOverrideColor(this))
            CustomFontManager.getInstance(this).setCustomFont(mb, CustomFontManager.FONT_BUTTON);
        }
        CustomFontManager.getInstance(this).setCustomFont(view.image_description, CustomFontManager.FONT_TEXT);
    }
}