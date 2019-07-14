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

import android.content.Context
import com.android.launcher3.R

fun getFeedSortingAlgorithms(): List<String> {
    return listOf(OrderedSortingAlgorithm::class.java.name,
                  MixerSortingAlgorithm::class.java.name);
}

fun getFeedSortingAlgorithmName(algorithm: String, context: Context): String {
    return when (algorithm) {
        OrderedSortingAlgorithm::class.java.name -> context.getString(R.string.title_sorting_algorithm_ordered)
        MixerSortingAlgorithm::class.java.name -> context.getString(
                    R.string.title_sorting_algorithm_mixer)

        else -> error("No such sorting algorithm ${algorithm}")
    }
}