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

package ch.deletescape.lawnchair.feed.tabs

import com.android.launcher3.R

object NameRegistry {
    val nameMap = mapOf(TabController::class.java.name to R.string.title_tab_controller_none,
                        CategorizedTabbingController::class.java.name to R.string.title_sorting_provider_categorized,
                        ProviderTabbingController::class.java.name to R.string.title_tab_controller_by_provider,
            CustomTabbingController::class.java.name to R.string.title_tabbing_controller_custom,
            G2CategorizedTabbingController::class.qualifiedName!! to context.getString(
                    R.string.title_tab_controller_categorized_g2))
}