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
import java.util.List;

import ch.deletescape.lawnchair.feed.SortingAlgorithm;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;

public class MixerSortHelper implements SortingAlgorithm<ChipProvider.Item> {
    @NotNull
    @Override
    public List<ChipProvider.Item> sort(@NotNull List<? extends ChipProvider.Item>... ts) {
        int pos = 0;
        List<ChipProvider.Item> results = new ArrayList<>();

        while (true) {
            boolean b = true;
            for (List<? extends ChipProvider.Item> it : ts) {
                if (it.size() > pos) {
                    b = false;
                    break;
                }
            }
            if (b) break;
            for (List<? extends ChipProvider.Item> chips : ts) {
                if (chips.size() <= pos) {
                    continue;
                } else {
                    results.add(chips.get(pos));
                    ++pos;
                }
            }
        }

        return results;
    }
}
