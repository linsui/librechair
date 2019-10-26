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

package ch.deletescape.lawnchair.feed.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.View;

import com.android.launcher3.notification.NotificationInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedProvider;
import kotlin.Unit;

public class NotificationFeedProvider extends FeedProvider {
    private final Object lockRef = new Object();
    private List<StatusBarNotification> notifs = new Vector<>();

    public NotificationFeedProvider(Context c) {
        super(c);
        OverlayNotificationManager.addListener(sbns -> {
            synchronized (lockRef) {
                notifs.clear();
                notifs.addAll(sbns);
            }
        });
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
    @SuppressWarnings("unchecked")
    public List<Card> getCards() {
        if (notifs == null || notifs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        synchronized (lockRef) {
            List<Card> cards = notifs.stream().filter(
                    Objects::nonNull).map(it -> {
                NotificationInfo info = new NotificationInfo(getContext(), it);
                String title;
                if (info.title != null) {
                    title = info.title.toString();
                } else if (info.text != null) {
                    title = info.text.toString();
                } else {
                    title = "?";
                }
                Card card = new Card(
                        info.getIconForBackground(getContext(), getFeed().getBackgroundColor()),
                        title, parent -> new View(getContext()),
                        Card.TEXT_ONLY | Card.RAISE, "",
                        info.notificationKey.hashCode());
                card.globalClickListener = v -> {
                    if (info.intent != null) {
                        try {
                            info.intent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                    return Unit.INSTANCE;
                };
                return card;
            }).collect(Collectors.toCollection(Vector::new));
            return cards;
        }
    }

    @Override
    public boolean isVolatile() {
        return true;
    }
}
