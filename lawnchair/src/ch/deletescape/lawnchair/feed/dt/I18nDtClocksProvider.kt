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

package ch.deletescape.lawnchair.feed.dt

import android.content.Context
import android.view.ViewGroup
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.formatTime
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.locale
import ch.deletescape.lawnchair.persistence.feedPrefs
import com.android.launcher3.R
import kotlinx.android.synthetic.main.world_clock.view.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

class I18nDtClocksProvider(c: Context) : FeedProvider(c) {

    override fun onFeedShown() {

    }

    override fun onFeedHidden() {

    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun getCards(): List<Card> {
        return context.feedPrefs.clockTimeZones.map {
            val card = Card(null, null, { parent, _ ->
                val view = (parent as ViewGroup).inflate(R.layout.world_clock)
                view.zid_name.text = ZoneId.of(it).getDisplayName(TextStyle.FULL_STANDALONE, context.locale)
                TickManager.subscribe {
                    view.zid_time.text = formatTime(
                            ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                    ZoneId.of(it)), context)
                    Unit
                }
                view
            }, Card.RAISE or Card.NO_HEADER, "",
                    ("dtc" + it + UUID.randomUUID().toString()).hashCode())
            card.canHide = true
            card.onRemoveListener = {
                context.feedPrefs.clockTimeZones.remove(it)
                Unit
            }
            card
        }
    }
}
