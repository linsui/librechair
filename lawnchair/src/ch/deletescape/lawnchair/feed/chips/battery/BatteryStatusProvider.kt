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

package ch.deletescape.lawnchair.feed.chips.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import com.android.launcher3.R

class BatteryStatusProvider(val context: Context) : ChipProvider {

    private val batteryReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            charging = status == BatteryManager.BATTERY_STATUS_CHARGING
            full = status == BatteryManager.BATTERY_STATUS_FULL
            level = (100f
                    * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)).toInt()
        }
    }
    private var charging = false
    private var full = false
    private var level = 100

    init {
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun getItems(context: Context): List<ChipProvider.Item> {
        val lines = mutableListOf<LawnchairSmartspaceController.Line>()
        when {
            full -> lines.add(LawnchairSmartspaceController.Line(context, R.string.battery_full))
            charging -> lines.add(
                    LawnchairSmartspaceController.Line(context, R.string.battery_charging))
            level <= 15 -> lines.add(
                    LawnchairSmartspaceController.Line(context, R.string.battery_low))
            else -> return emptyList()
        }
        if (!full) {
            lines.add(LawnchairSmartspaceController.Line("$level%"))
        }
        return listOf(ChipProvider.Item().apply {
            title = lines.map { it.text }.joinToString(" - ")
            icon = ThemedBatteryDrawable(context, FeedAdapter.getOverrideColor(context)).apply {
                this.charging = charging
                this.setBatteryLevel(level)
            }
        })
    }
}