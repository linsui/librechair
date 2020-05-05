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

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.provider.Settings
import android.view.View
import ch.deletescape.lawnchair.*
import com.android.launcher3.R

class DeviceStateProvider(c: Context) : FeedProvider(c) {

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun getCards(): MutableList<Card> {
        val cards = mutableListOf<Card>()
        val dnd = when (Settings.Global.getInt(context.contentResolver, "zen_mode")) {
            1 -> R.string.title_card_dnd_priority_only.fromStringRes(
                    context)
            2 -> R.string.title_card_dnd_alarms_only.fromStringRes(
                    context)
            3 -> R.string.title_card_dnd_nothing.fromStringRes(
                    context)

            else -> null
        }
        if (dnd != null) {
            cards += Card(R.drawable.ic_zen_mode.fromDrawableRes(context).tint(
                    (if (useWhiteText(backgroundColor,
                                      context)) R.color.qsb_background else R.color.qsb_background_dark).fromColorRes(
                            context)), dnd, { _, _ ->
                              View(context)
                          }, Card.TEXT_ONLY, "nosort,top", "feedDndIndicator".hashCode())
        }
        if (Settings.Global.getInt(context.contentResolver,
                                   Settings.Global.AIRPLANE_MODE_ON,
                                   0) != 0) {
            cards += Card(R.drawable.ic_round_airplanemode_active_24px.fromDrawableRes(
                    context).tint((if (useWhiteText(backgroundColor,
                                                    context)) R.color.qsb_background else R.color.qsb_background_dark).fromColorRes(
                    context)), R.string.title_card_airplane_mode_on.fromStringRes(context),
                          { _, _ ->
                              View(context)
                          }, Card.TEXT_ONLY, "nosort,top", "feedAirplaneModeIndicator".hashCode())
        }
        if (((context.getSystemService(
                        Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true).not()) {
            cards += Card(R.drawable.ic_round_wifi_off_24dp.fromDrawableRes(context)
                                  .tint(
                                          (if (useWhiteText(backgroundColor, context))
                                              R.color.qsb_background else R.color.qsb_background_dark)
                                                  .fromColorRes(context)),
                          R.string.title_card_network_disconnected.fromStringRes(context), { _, _ ->
                              View(context)
                          }, Card.TEXT_ONLY, "nosort,top", "feedNetworkModeIndicator".hashCode())
        }
        if ((context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                        .ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            cards += Card(when ((context.getSystemService(
                    Context.AUDIO_SERVICE) as AudioManager).ringerMode) {
                              AudioManager.RINGER_MODE_SILENT -> R.drawable.ic_outline_volume_off_24px
                              AudioManager.RINGER_MODE_VIBRATE -> R.drawable.ic_round_vibration_24dp
                              else -> R.drawable.ic_round_vibration_24dp
                          }.fromDrawableRes(context).tint(
                    (if (useWhiteText(backgroundColor, context))
                        R.color.qsb_background else R.color.qsb_background_dark)
                            .fromColorRes(context)),
                          when ((context.getSystemService(
                                  Context.AUDIO_SERVICE) as AudioManager).ringerMode) {
                              AudioManager.RINGER_MODE_SILENT -> R.string.title_card_phone_muted
                              AudioManager.RINGER_MODE_VIBRATE -> R.string.title_card_phone_vibrate
                              else -> R.string.title_card_phone_vibrate
                          }.fromStringRes(context), { _, _ ->
                              View(context)
                          }, Card.TEXT_ONLY, "nosort,top", "feedNetworkModeIndicator".hashCode())
        }
        return cards
    }

}