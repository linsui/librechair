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

package ch.deletescape.lawnchair.globalsearch.providers

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import ch.deletescape.lawnchair.LawnchairLauncher
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.globalsearch.SearchProvider
import com.android.launcher3.R

class OverlaySearchProvider(context: Context) : SearchProvider(context) {
    override val name: String
        get() = context.getString(R.string.title_search_provider_overlay)
    override val supportsVoiceSearch: Boolean
        get() = true
    override val supportsAssistant: Boolean
        get() = false
    override val supportsFeed: Boolean
        get() = true

    override fun startSearch(callback: (intent: Intent) -> Unit) =
            error("stub!")
    override fun getIcon(): Drawable = R.drawable.ic_search.fromDrawableRes(context)
    override fun getVoiceIcon(): Drawable = R.drawable.ic_mic_color.fromDrawableRes(context)
    override fun startFeed(callback: (intent: Intent) -> Unit) {
        LawnchairLauncher.getLauncher(context).overlay?.client?.openOverlay(true)
    }
}