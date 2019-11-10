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

package ch.deletescape.lawnchair.awareness

import android.annotation.MainThread
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter

object TickManager {
    private val tickHandlers: MutableList<() -> Unit> = mutableListOf()

    @MainThread
    fun subscribe(@MainThread handler: () -> Unit) {
        handler()
        tickHandlers += handler
    }

    @MainThread
    fun bindToContext(context: Context) {
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                tickHandlers.forEach { it() }
            }
        }, IntentFilter().apply {
            addAction(ACTION_TIME_TICK)
            addAction(ACTION_TIME_CHANGED)
            addAction(ACTION_TIME_TICK)
            addAction(ACTION_DATE_CHANGED)
        })
    }
}