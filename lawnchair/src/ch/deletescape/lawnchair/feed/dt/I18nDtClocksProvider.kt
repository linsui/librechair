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

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.world_clock.view.zid_name
import kotlinx.android.synthetic.main.world_clock.view.zid_offset
import kotlinx.android.synthetic.main.world_clock_analog.view.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.TimeUnit

class I18nDtClocksProvider(c: Context) : FeedProvider(c) {

    override fun onFeedShown() {

    }

    override fun onFeedHidden() {

    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    @SuppressLint("SetTextI18n")
    override fun getCards(): List<Card> {
        val analog = context.feedPrefs.displayAnalogClock
        return context.feedPrefs.clockTimeZones.map {
            val card = Card(null, null, { parent, _ ->
                val view = (parent as ViewGroup).inflate(
                        if (!analog) R.layout.world_clock else R.layout.world_clock_analog)
                view.zid_name.text =
                        ZoneId.of(it).getDisplayName(TextStyle.FULL_STANDALONE, context.locale)
                val offset = TimeUnit.SECONDS.toHours(
                        (TimeZone.getDefault().rawOffset / 1000 - ZoneId.of(it).rules.getOffset(
                                Instant.now()).totalSeconds.toLong()))
                view.zid_offset.text = if (offset > 0) "-$offset h" else "$offset h"
                TickManager.subscribe {
                    if (ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                    ZoneId.of(it)).toLocalDate() < LocalDate.now()) {
                        if (!analog) {
                            view.zid_time.text = context.getString(R.string.title_card_dt_yesterday)
                                    .format(formatTime(
                                            ZonedDateTime.now(
                                                    ZoneId.systemDefault()).withZoneSameInstant(
                                                    ZoneId.of(it)), context))
                        } else {
                            view.zid_direction.text = context.getString(R.string.title_dt_yesterday)
                        }
                    } else if (ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                    ZoneId.of(it)).toLocalDate() > LocalDate.now()) {
                        if (!analog) {
                            view.zid_time.text = context.getString(R.string.title_card_dt_tomorrow)
                                    .format(formatTime(
                                            ZonedDateTime.now(
                                                    ZoneId.systemDefault()).withZoneSameInstant(
                                                    ZoneId.of(it)), context))
                        } else {
                            view.zid_direction.text = context.getString(R.string.title_dt_tomorrow)
                        }
                    } else {
                        if (!analog) {
                            view.zid_time.text = formatTime(
                                    ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                            ZoneId.of(it)), context)
                        } else {
                            view.zid_direction.text = ""
                        }
                    }
                    if (analog) {
                        view.zid_time_analog.updateTime(
                                ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                        ZoneId.of(it)).toLocalTime());
                    }
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

    override fun isVolatile() = true
}
