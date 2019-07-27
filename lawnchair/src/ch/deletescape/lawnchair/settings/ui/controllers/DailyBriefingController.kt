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

package ch.deletescape.lawnchair.settings.ui.controllers

import android.content.Context
import android.support.annotation.Keep
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.settings.ui.PreferenceController
import ch.deletescape.lawnchair.smartspace.DailyBriefingProvider
import ch.deletescape.lawnchair.util.extensions.d

@Keep
class DailyBriefingController(context: Context) : PreferenceController(context) {

    override val isVisible
        get() = context.lawnchairPrefs.eventProviders.also {
            d("getIsVisible: event providers: ${it.getAll()}")
        }.toList().any { it.contentEquals(DailyBriefingProvider::class.java.name) }.also {
            d("getIsVisible: visibility: $it")
        }
}
