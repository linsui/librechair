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

package ch.deletescape.lawnchair.globalsearch.providers

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import ch.deletescape.lawnchair.globalsearch.SearchProvider
import ch.deletescape.lawnchair.globalsearch.activity.WebSearchProviderActivity
import com.android.launcher3.R

class BuiltInSearchProvider(c: Context) : SearchProvider(c) {
    override val name: String
        get() = context.getString(R.string.search_provider_built_in) + " [WIP]"
    override val supportsVoiceSearch: Boolean
        get() = true
    override val supportsAssistant: Boolean
        get() = false
    override val supportsFeed: Boolean
        get() = false


    override fun startSearch(callback: (intent: Intent) -> Unit) {
        val i = Intent(context, WebSearchProviderActivity::class.java);
        callback.invoke(i);
    }

    override fun startVoiceSearch(callback: (intent: Intent) -> Unit) {
        val i = Intent(context, WebSearchProviderActivity::class.java);
        i.putExtra("voice_search", true);
        callback.invoke(i);
    }

    override fun getIcon(): Drawable {
        return context.resources.getDrawable(R.drawable.ic_search);
    }

    override fun getVoiceIcon(): Drawable? {
        return context.resources.getDrawable(R.drawable.ic_mic_color);
    }
}