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
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MixerSortingAlgorithm extends AbstractFeedSortingAlgorithm {

    private List<Card> top = new ArrayList<>();
    private List<Card> bottom = new ArrayList<>();
    private List<List<Card>> shouldMix = new ArrayList<>();

    @SafeVarargs
    @NotNull
    @Override
    public final List<Card> sort(@NotNull List<? extends Card>... ts) {
        for (List<? extends Card> cards : ts) {
            List<Card> mixQueue = new ArrayList<>();
            for (Card c : cards) {
                if (c.getAlgoFlags() != null && c.getAlgoFlags().contains("nosort")) {
                    if (Arrays.asList(c.getAlgoFlags().split(",")).contains("bottom")) {
                        bottom.add(c);
                    } else {
                        top.add(c);
                    }
                } else {
                    mixQueue.add(c);
                }
            }
            shouldMix.add(mixQueue);
        }
        List<Card> cards = new ArrayList<>();
        cards.addAll(top);
        while (true) {
            int empty = 0;
            for (List<Card> iter : shouldMix) {
                if (!iter.isEmpty()) {
                    cards.add(iter.get(0));
                    iter.remove(0);
                } else {
                    ++empty;
                }
            }

            if (empty >= shouldMix.size()) {
                break;
            }
        }
        cards.addAll(bottom);
        return cards;
    }
}
