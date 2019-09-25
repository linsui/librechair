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

package ch.deletescape.lawnchair.feed.chips.sorting;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.deletescape.lawnchair.feed.SortingAlgorithm;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;

public class MixerSortHelper implements SortingAlgorithm<ChipProvider.Item> {
    @NotNull
    @Override
    public List<ChipProvider.Item> sort(@NotNull List<? extends ChipProvider.Item>... ts) {
        if (ts.length == 0 || Arrays.stream(ts).allMatch(it -> it.isEmpty())) {
            return Collections.EMPTY_LIST;
        }
        List<ChipProvider.Item> results = new ArrayList<>();

        int itemPos = 0;
        int max = Arrays.stream(ts).mapToInt(List::size).max().getAsInt();
        while (max > itemPos) {
            int listPos = 0;
            while (ts.length > listPos) {
                if (ts[listPos].size() > itemPos) {
                    results.add(ts[listPos].get(itemPos));
                }
                ++listPos;
            }
            ++itemPos;
        }
        return results;
    }
}
