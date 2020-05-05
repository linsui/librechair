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

package ch.deletescape.lawnchair.feed.tabs.indicator

import android.content.Context
import android.graphics.drawable.Drawable
import ch.deletescape.lawnchair.fromDrawableRes
import com.android.launcher3.R
import kotlin.reflect.KClass

open class TabIndicatorProvider(val context: Context) {
    open val drawable: Drawable
        get() = R.drawable.tab_indicator_feed.fromDrawableRes(context)

    companion object
}

fun TabIndicatorProvider.Companion.inflate(klazz: KClass<out TabIndicatorProvider>,
                                           context: Context): TabIndicatorProvider =
            klazz.constructors.first().call(
                    context)

val TabIndicatorProvider.Companion.allProviders
    get() = listOf(TabIndicatorProvider::class to R.string.theme_default,
            DotIndicatorProvider::class to R.string.title_tab_indicator_dot,
            LegacyTabIndicatorProvider::class to R.string.title_tab_indicator_legacy)