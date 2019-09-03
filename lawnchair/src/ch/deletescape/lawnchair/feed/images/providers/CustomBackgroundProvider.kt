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

package ch.deletescape.lawnchair.feed.images.providers

import android.content.Context
import android.graphics.Bitmap
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.feed.images.ImageStore
import ch.deletescape.lawnchair.lawnchairPrefs

class CustomBackgroundProvider(val context: Context) : ImageProvider {
    override fun registerOnChangeListener(listener: () -> Unit) {
        context.lawnchairPrefs.addOnPreferenceChangeListener(
                object : LawnchairPreferences.OnPreferenceChangeListener {
                    override fun onValueChanged(key: String, prefs: LawnchairPreferences,
                                                force: Boolean) {
                        listener()
                    }
                }, "pref_feed_custom_background")
    }

    override val expiryTime: Long = 0
    override suspend fun getBitmap(context: Context): Bitmap? = ImageStore.getInstance(context).getBitmap(
            context.lawnchairPrefs.feedCustomBackground ?: "000000-0000-0000-fffff")
}