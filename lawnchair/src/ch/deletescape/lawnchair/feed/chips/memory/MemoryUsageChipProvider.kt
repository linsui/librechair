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

package ch.deletescape.lawnchair.feed.chips.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.fromDrawableRes
import com.android.launcher3.R
import android.content.Context.ACTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService


class MemoryUsageChipProvider(val context: Context) : ChipProvider() {
    override fun getItems(context: Context): List<Item> = listOf(Item().apply {
        icon = R.drawable.ic_bug_notification.fromDrawableRes(context)
        val mi = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMegs = mi.availMem / 0x100000L
        val totalMegs = mi.totalMem / 0x100000L
        title =
                "$availableMegs MB / ${if (totalMegs < 0x400) totalMegs else totalMegs / 0x400f} ${if (totalMegs < 0x400) "MB" else "GB"}"
    })
}