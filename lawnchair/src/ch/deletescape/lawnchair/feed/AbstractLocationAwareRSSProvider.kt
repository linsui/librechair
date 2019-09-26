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

package ch.deletescape.lawnchair.feed

import android.annotation.SuppressLint
import android.content.Context
import ch.deletescape.lawnchair.checkLocationAccess
import ch.deletescape.lawnchair.lawnchairLocationManager
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.util.extensions.d
import com.rometools.rome.feed.synd.SyndFeed
import geocode.GeocoderCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

abstract class AbstractLocationAwareRSSProvider(c: Context) : AbstractRSSFeedProvider(c) {
    @SuppressLint("MissingPermission")
    final override fun bindFeed(callback: BindCallback) {
        try {
            if (context.checkLocationAccess() && context.lawnchairPrefs.overrideLocale.isEmpty()) {
                context.lawnchairLocationManager.addCallback { lat, lon ->
                    GlobalScope.launch {
                        val country =
                                GeocoderCompat(context, true).nearestPlace(lat, lon).country
                        try {
                            callback.onBind(getLocationAwareFeed(lat to lon,
                                    Locale("", country).isO3Country))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback.onBind(getFallbackFeed())
                        }
                    }
                }
            } else if (context.lawnchairPrefs.overrideLocale.isNotEmpty()) {
                runOnNewThread {
                    try {
                        callback.onBind(getLocationAwareFeed(0.toDouble() to 0.toDouble(),
                                Locale("",
                                        context.lawnchairPrefs.overrideLocale).isO3Country))
                    } catch (e: Exception) {
                        callback.onBind(getFallbackFeed())
                    }
                }
            } else {
                d("bindFeed: location not available; binding to fallback feed")
                runOnNewThread {
                    try {
                        callback.onBind(getFallbackFeed())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun getLocationAwareFeed(location: Pair<Double, Double>, country: String): SyndFeed
    abstract fun getFallbackFeed(): SyndFeed
}