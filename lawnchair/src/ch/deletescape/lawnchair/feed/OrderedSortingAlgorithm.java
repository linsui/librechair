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

package ch.deletescape.lawnchair.feed;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class OrderedSortingAlgorithm extends AbstractFeedSortingAlgorithm {

    @NotNull
    @Override
    public List<Card> sort(@NotNull List<? extends Card>... cards) {
        List<Card> cards1 = new ArrayList<>();
        for (List<? extends Card> card2 : cards) {
            cards1.addAll(card2);
        }
        return cards1;
    }
}
