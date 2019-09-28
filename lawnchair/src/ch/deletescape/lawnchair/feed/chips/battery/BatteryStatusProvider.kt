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
import ch.deletescape.lawnchair.fromStringRes
import com.android.launcher3.R

class BatteryStatusProvider(val context: Context) : ChipProvider() {

    private val batteryReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                charging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0).let {
                    it == BatteryManager.BATTERY_PLUGGED_AC ||
                            it == BatteryManager.BATTERY_PLUGGED_USB ||
                            it == BatteryManager.BATTERY_PLUGGED_WIRELESS
                }
                full = status == BatteryManager.BATTERY_STATUS_FULL
                level = (100f
                        * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                        / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)).toInt()
                refresh()
            }
        }
    }
    private var charging = false
    private var full = false
    private var level = 100

    init {
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun getItems(context: Context): List<ChipProvider.Item> {
        val lines = mutableListOf<Any>()
        when {
            full -> lines.add(R.string.battery_full)
            charging -> lines.add(R.string.battery_charging)
            level <= 15 -> lines.add(R.string.battery_low)
            else -> lines.add("$level%")
        }
        return listOf(ChipProvider.Item().apply {
            title = lines.joinToString(" - ") {
                if (it is Int) it.fromStringRes(context) else it as CharSequence
            }
            icon = BatteryMeterDrawableBase(context, FeedAdapter.getOverrideColor(context)).apply {
                this.batteryLevel = this@BatteryStatusProvider.level
                this.charging = this@BatteryStatusProvider.charging
            }
            click = Runnable {
                Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).also {
                    context.startActivity(it)
                }
            }
        })
    }
}