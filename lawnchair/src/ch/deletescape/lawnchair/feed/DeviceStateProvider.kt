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

import android.app.NotificationManager
import android.content.Context
import android.view.View
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.fromStringRes
import com.android.launcher3.R

class DeviceStateProvider(c: Context) : FeedProvider(c) {
    override fun onFeedShown() {
        // TODO
    }

    override fun onFeedHidden() {
        // TODO
    }

    override fun onCreate() {
        // TODO
    }

    override fun onDestroy() {
        // TODO
    }

    override fun getCards(): MutableList<Card> {
        val cards = mutableListOf<Card>()
        val dnd = when ((context.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager).currentInterruptionFilter) {
            NotificationManager.INTERRUPTION_FILTER_PRIORITY -> R.string.title_card_dnd_priority_only.fromStringRes(
                    context)
            NotificationManager.INTERRUPTION_FILTER_ALARMS -> R.string.title_card_dnd_alarms_only.fromStringRes(
                    context)
            NotificationManager.INTERRUPTION_FILTER_ALL -> R.string.title_card_dnd_nothing.fromStringRes(
                    context)

            else -> null
        }
        if (dnd != null) {
            cards += Card(R.drawable.ic_zen_mode.fromDrawableRes(context), dnd, {
                View(context)
            }, Card.NO_HEADER, "nosort,top", "feedDndIndicator".hashCode())
        }
        return cards;
    }

}