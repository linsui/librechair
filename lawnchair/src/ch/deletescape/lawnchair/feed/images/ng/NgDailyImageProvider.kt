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

package ch.deletescape.lawnchair.feed.images.ng

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.images.AbstractImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class NgDailyImageProvider(c: Context) : AbstractImageProvider<String>(c) {
    override val images: MutableMap<Bitmap, String> = mutableMapOf()
    override val headerCard = null
    override val onRemoveListener: (id: String) -> Unit = {}

    init {
        NationalGeographicRetrofitServiceFactory.getApi(context).getPictureOfTheDay().enqueue(object : Callback<Gallery> {
            override fun onFailure(call: Call<Gallery>, t: Throwable) {

            }

            override fun onResponse(call: Call<Gallery>, response: Response<Gallery>) {
                if (response.isSuccessful && response.body() != null) {
                    images.clear()
                    FeedScope.launch {
                        response.body()!!.items.take(4).forEach {
                            images += withContext(Dispatchers.Default) {
                                BitmapFactory.decodeStream(URL(it.image.originalUrl).openStream())
                            } to it.image.originalUrl
                        }
                    }
                }
            }

        })
    }
}