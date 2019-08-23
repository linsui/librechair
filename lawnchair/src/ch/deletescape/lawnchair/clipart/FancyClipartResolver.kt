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

package ch.deletescape.lawnchair.clipart

import android.content.Context
import ch.deletescape.lawnchair.fromStringRes
import com.android.launcher3.R

class FancyClipartResolver(val context: Context) : ClipartResolver {
    companion object {
        @JvmStatic
        val items = mapOf("ic_mic_color" to R.string.clipart_microphone,
                          "ic_bug_notification" to R.string.clipart_bug,
                          "ic_edit_no_shadow" to R.string.action_edit,
                          "ic_information" to R.string.clipart_information,
                          "ic_google" to R.string.clipart_goolag)
    }

    override fun getAllClipart(): List<ClipartResolver.ClipartData> {
        return items.map {
            object : ClipartResolver.ClipartData {
                override fun getToken(): String = it.key
                override fun getUserFacingName() = it.value.fromStringRes(context)
                override fun resolveClipart() = context.getDrawable(
                        context.resources.getIdentifier(it.key, "drawable", context.packageName))
            }
        }
    }
}