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
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ch.deletescape.lawnchair.feed.ProviderScreen
import ch.deletescape.lawnchair.inflate
import com.android.launcher3.R

class ImageDataScreen(base: Context, val bitmap: Bitmap, val desc: String)
    : ProviderScreen(base) {
    override fun getView(parent: ViewGroup) = parent.inflate(R.layout.image_information_screen)

    override fun bindView(view: View) {
        view.findViewById<ImageView>(R.id.image).setImageBitmap(bitmap)
        view.findViewById<TextView>(R.id.image_description).text = desc
    }
}