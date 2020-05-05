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

package ch.deletescape.lawnchair.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.colorEngine
import ch.deletescape.lawnchair.feed.preview.FeedPreviewAdapter
import ch.deletescape.lawnchair.lawnchairPrefs

class FeedPreview(context: Context, attrs: AttributeSet?)
    : RecyclerView(context, attrs), LawnchairPreferences.OnPreferenceChangeListener {

    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        (adapter as FeedPreviewAdapter).apply {
            backgroundColor = context.colorEngine.feedBackground.value.resolveColor()
            notifyDataSetChanged()
            background = ColorDrawable(context.colorEngine.feedBackground.value.resolveColor())
        }
    }

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = FeedPreviewAdapter(context.colorEngine.feedBackground.value.resolveColor(), context)
        background = ColorDrawable(context.colorEngine.feedBackground.value.resolveColor())
        context.lawnchairPrefs.addOnPreferenceChangeListener("pref_feed_preview", this)
    }
}