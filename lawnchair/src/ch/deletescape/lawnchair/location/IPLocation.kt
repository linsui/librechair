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

package ch.deletescape.lawnchair.location

import android.content.Context
import android.os.NetworkOnMainThreadException
import android.os.SystemClock
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class IPLocation(context: Context,
                 private val cacheValidityMs: Long = TimeUnit.MINUTES.toMillis(30)) :
        LocationManager.LocationProvider(context) {
    init {
        FeedScope.launch {
            get()
        }
    }

    override fun refresh() {
        if (get().success) {
            updateLocation(get().lat, get().lon)
        } else {
            FeedScope.launch {
                delay(1000)
                refresh()
            }
        }
    }

    private val client = OkHttpClientBuilder().build(context)

    private val cacheValid get() = cache != null && cache!!.success && timeLast + cacheValidityMs > SystemClock.uptimeMillis()
    private var timeLast = 0L
    private var cache: Result? = null
        set(value) {
            timeLast = SystemClock.uptimeMillis()
            field = value
        }

    fun get(): Result {
        var success = false
        var lat = Double.NaN
        var lon = Double.NaN

        if (!cacheValid) {
            for (url in URLS) {
                try {
                    val response = client.newCall(getRequest(url)).execute()
                    if (response.isSuccessful && response.body != null) {
                        val json = JSONObject(response.body?.string())
                        d("get: IP location server responded with $json")
                        lat = json.getDouble("latitude")
                        lon = json.getDouble("longitude")
                        success = true
                        break
                    }
                } catch (e: Exception) {
                    if (e is NetworkOnMainThreadException)
                        throw e
                }
            }

            cache = Result(success, lat, lon)
        }

        return cache ?: Result(success, lat, lon).apply {
            cache = this
        }
    }

    private fun getRequest(url: String) = Request.Builder().url(url).get().build()

    data class Result(val success: Boolean, val lat: Double, val lon: Double)

    companion object {
        private val URLS = arrayOf("https://freegeoip.app/json/",
                "https://geoip-db.com/json/",
                "https://api.iplocate.app/json/")
    }
}