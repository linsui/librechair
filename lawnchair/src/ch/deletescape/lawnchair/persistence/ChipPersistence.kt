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

package ch.deletescape.lawnchair.persistence

import android.content.Context
import ch.deletescape.lawnchair.util.SingletonHolder

class ChipPersistence private constructor(val context: Context) {
    val chipOpacity by NumberDelegate(context, "feed_chip_opacity", 1.0)
    val chipsOnTop by BooleanDelegate(context, "feed_chips_on_top", false)
    val outlineChips by BooleanDelegate(context, "feed_outline_chips", false)
    val weatherItems by NumberDelegate(context, "feed_chip_weather_item_count", 5.0)
    val elevation by NumberDelegate(context, "feed_chip_elevation", 8.0)
    val mixChips by BooleanDelegate(context, "feed_mix_chips", false);

    companion object : SingletonHolder<ChipPersistence, Context>(::ChipPersistence)
}

val Context.chipPrefs
    get() = ChipPersistence.getInstance(this)