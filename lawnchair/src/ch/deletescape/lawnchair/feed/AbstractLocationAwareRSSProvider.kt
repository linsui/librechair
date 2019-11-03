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
import ch.deletescape.lawnchair.lawnchairApp
import ch.deletescape.lawnchair.lawnchairLocationManager
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.extensions.e
import ch.deletescape.lawnchair.util.extensions.w
import com.rometools.rome.feed.synd.SyndFeed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.function.Consumer

abstract class AbstractLocationAwareRSSProvider(c: Context) : AbstractRSSFeedProvider(c) {
    var callbackAdded: Boolean = false

    init {
        ch.deletescape.lawnchair.location.LocationManager.addCallback { _, _ ->
            refresh(context, { }, true)
        }
    }

    @SuppressLint("MissingPermission")
    final override fun bindFeed(callback: BindCallback, token: String) {
        ArticleJobsScope.launch {
            val locale = token.split("@")[1]
            d("bindFeed: binding to locale $locale")
            if (locale != "?") {
                try {
                    callback.onBind(getLocationAwareFeed(Locale("", locale).isO3Country))
                } catch (e1: Exception) {
                    try {
                        callback.onBind(getFallbackFeed())
                    } catch (e: IOException) {
                        w("bindFeed: couldn't retrieve fallback feed", e)
                    } catch (e2: RuntimeException) {
                        e("bindFeed: unknown error retrieving fallback feed", e2)
                    }
                }
            } else {
                try {
                    callback.onBind(getFallbackFeed())
                } catch (e: IOException) {
                    w("bindFeed: couldn't retrieve fallback feed", e)
                } catch (e2: RuntimeException) {
                    e("bindFeed: unknown exception retrieving fallback feed", e2)
                }
            }
        }
    }

    final override fun onInit(tokenCallback: Consumer<String>) {
        if (context.lawnchairPrefs.overrideLocale.isNotEmpty()) {
            ArticleJobsScope.launch {
                try {
                    tokenCallback.accept("${javaClass.name}@${Locale("", context.lawnchairPrefs.overrideLocale).isO3Country}")
                } catch (e: Exception) {
                    tokenCallback.accept("${javaClass.name}@?")
                }
            }
        } else if (context.lawnchairLocationManager.location != null) {
            context.lawnchairLocationManager.location!!.let { (lat, lon) ->
                ArticleJobsScope.launch {
                    val country =
                            context.lawnchairApp.geocoder.nearestPlace(lat, lon).country
                    try {
                        tokenCallback.accept(this@AbstractLocationAwareRSSProvider.javaClass.name + "@" + Locale("", country).isO3Country)
                    } catch (e: Exception) {
                        tokenCallback.accept(this@AbstractLocationAwareRSSProvider.javaClass.name + "@?")
                    }
                    if (!callbackAdded) {
                        context.lawnchairLocationManager.changeCallbacks += { lat, lon ->
                            ArticleJobsScope.launch {
                                val s =
                                        context.lawnchairApp.geocoder.nearestPlace(lat, lon).country
                                try {
                                    tokenCallback.accept(
                                            this@AbstractLocationAwareRSSProvider.javaClass.name + "@" + Locale(
                                                    "", s).isO3Country)
                                } catch (e: Exception) {
                                    tokenCallback.accept(
                                            this@AbstractLocationAwareRSSProvider.javaClass.name + "@?")
                                }
                            }
                        }
                        callbackAdded = true
                    }
                }
            }
        } else {
            ArticleJobsScope.launch {
                try {
                    tokenCallback.accept(javaClass.name + "@?")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!callbackAdded) {
                    delay(5)
                    context.lawnchairLocationManager.addCallback { lat, lon ->
                        ArticleJobsScope.launch {
                            val s =
                                    context.lawnchairApp.geocoder.nearestPlace(lat, lon).country
                            try {
                                tokenCallback.accept(
                                        this@AbstractLocationAwareRSSProvider.javaClass.name + "@" + Locale(
                                                "", s).isO3Country)
                            } catch (e: Exception) {
                                tokenCallback.accept(
                                        this@AbstractLocationAwareRSSProvider.javaClass.name + "@?")
                            }
                        }
                    }
                    callbackAdded = true
                }
            }
        }
    }

    override fun getId(): String = throw UnsupportedOperationException("getId not implemented since token handling is done manually")

    abstract fun getLocationAwareFeed(country: String): SyndFeed
    abstract fun getFallbackFeed(): SyndFeed
}