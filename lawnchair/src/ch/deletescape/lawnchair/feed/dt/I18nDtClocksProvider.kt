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
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.Button
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.persistence.feedPrefs
import com.android.launcher3.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.world_clock.view.*
import kotlinx.android.synthetic.main.world_clock.view.zid_name
import kotlinx.android.synthetic.main.world_clock.view.zid_offset
import kotlinx.android.synthetic.main.world_clock_analog.view.*
import kotlinx.android.synthetic.main.world_clock_analog.view.zid_direction
import okhttp3.internal.toImmutableList
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.TimeUnit

class I18nDtClocksProvider(c: Context) : FeedProvider(c) {
    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun getCards(): List<Card> {
        val analog = context.feedPrefs.displayAnalogClock
        return context.feedPrefs.clockTimeZones.map {
            val card = Card(null, null, { parent, _ ->
                val view = (parent as ViewGroup).inflate(
                        if (!analog) R.layout.world_clock else R.layout.world_clock_analog)
                view.zid_name.text =
                        ZoneId.of(it).getDisplayName(TextStyle.FULL_STANDALONE, context.locale)
                val offset = -TimeUnit.SECONDS.toHours(
                        (TimeZone.getDefault().rawOffset / 1000 - ZoneId.of(it).rules.getOffset(
                                Instant.now()).totalSeconds.toLong()))
                view.zid_offset.text = when {
                    offset > 0 -> context.resources.getQuantityText(R.plurals.title_dt_time_later,
                            offset.toInt()).toString().format(offset)
                    offset == 0L -> R.string.reusable_str_now.fromStringRes(context)
                    else -> context.resources.getQuantityText(R.plurals.title_dt_time_sooner,
                            offset.toInt()).toString().format(offset)
                }
                TickManager.subscribe {
                    when {
                        ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                ZoneId.of(
                                        it)).toLocalDate() < LocalDate.now() -> view.zid_direction.text =
                                context.getString(R.string.title_dt_yesterday)
                        ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                ZoneId.of(
                                        it)).toLocalDate() > LocalDate.now() -> view.zid_direction.text =
                                context.getString(R.string.title_dt_tomorrow)
                        else -> view.zid_direction.text = ""
                    }
                    if (analog) {
                        view.zid_time_analog.updateTime(
                                ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                        ZoneId.of(it)).toLocalTime())
                        view.zid_time_analog.setTint(view.zid_direction.textColors.defaultColor)
                    }
                    if (!analog) {
                        view.zid_time.text = formatTime(
                                ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                        ZoneId.of(it)), context)
                    }
                    Unit
                }
                view
            }, Card.RAISE or Card.NO_HEADER, "",
                    ("dtc" + it + UUID.randomUUID().toString()).hashCode())
            card.canHide = true
            card.onRemoveListener = { v ->
                val backup = context.feedPrefs.clockTimeZones.toImmutableList()
                context.feedPrefs.clockTimeZones.remove(it)
                Snackbar.make(v, R.string.item_removed.fromStringRes(context), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo.fromStringRes(context)) {
                            context.feedPrefs.clockTimeZones.clear()
                            context.feedPrefs.clockTimeZones.addAll(backup)
                            feed?.refresh(0)
                        }
                        .setBackgroundTint(context.colorEngine.getResolverCache(
                                ColorEngine.Resolvers.FEED_CARD).value.resolveColor())
                        .setTextColor(context.colorEngine.getResolverCache(
                                ColorEngine.Resolvers.FEED_CARD).value.computeForegroundColor())
                        .setActionTextColor(FeedAdapter.getOverrideColor(v.context))
                        .also { sb ->
                            sb.view.findViewById<Button>(
                                    com.google.android.material.R.id.snackbar_action)
                                    .backgroundTintList = ColorStateList.valueOf(
                                    FeedAdapter.getOverrideColor(v.context).setAlpha(0))
                        }.show()
                Unit
            }
            card
        }
    }

    override fun isVolatile() = true
}
