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

package ch.deletescape.lawnchair.colors.resolvers

import ch.deletescape.lawnchair.colorEngine
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.fromColorRes
import ch.deletescape.lawnchair.setAlpha
import ch.deletescape.lawnchair.useWhiteText
import com.android.launcher3.R

class FeedCardResolver(config: Config) : ColorEngine.ColorResolver(config) {
    override fun resolveColor(): Int {
        return if (useWhiteText(
                        context.colorEngine.feedBackground.value.resolveColor().setAlpha(255),
                        context)) R.color.qsb_background_dark.fromColorRes(
                context) else R.color.qsb_background.fromColorRes(context)
    }

    override fun getDisplayName(): String {
        return context.getString(R.string.title_color_resolver_feed_card)
    }
}