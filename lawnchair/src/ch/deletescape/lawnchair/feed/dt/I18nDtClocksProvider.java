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

package ch.deletescape.lawnchair.feed.dt;

import android.content.Context;
import android.widget.TextView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.awareness.TickManager;
import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;
import kotlin.Unit;

public class I18nDtClocksProvider extends FeedProvider {
    public I18nDtClocksProvider(Context c) {
        super(c);
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public List<Card> getCards() {
        return FeedPersistenceKt.getFeedPrefs(getContext()).getClockTimeZones().stream().map(it -> {
            Card card = new Card(null, null, parent -> {
                TextView tv = new TextView(parent.getContext());
                TickManager.INSTANCE.subscribe(() -> {
                    tv.setText(LawnchairUtilsKt.formatTime(
                            ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(
                                    ZoneId.of(it)), getContext()));
                    return Unit.INSTANCE;
                });
                return tv;
            }, Card.Companion.getRAISE() | Card.Companion.getNO_HEADER(), "",
                    ("dtc" + it + UUID.randomUUID().toString()).hashCode());
            card.setCanHide(true);
            card.setOnRemoveListener(() -> {
                FeedPersistenceKt.getFeedPrefs(getContext()).getClockTimeZones().remove(it);
                return Unit.INSTANCE;
            });
            return card;
        }).collect(Collectors.toList());
    }
}
