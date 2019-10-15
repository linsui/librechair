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
import android.content.res.ColorStateList;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.notification.NotificationInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.smartspace.MediaListener;
import kotlin.Unit;

public class NotificationFeedProvider extends FeedProvider {
    private List<StatusBarNotification> notifs = new Vector<>();
    private List<Runnable> onMediaNotifChange = new Vector<>();
    private MediaListener mediaListener;

    public NotificationFeedProvider(Context c) {
        super(c);
        OverlayNotificationManager.addListener(sbns -> notifs = sbns);
        mediaListener = new MediaListener(getContext(), () -> onMediaNotifChange.forEach(
                Runnable::run), new Handler(LauncherModel.getUiWorkerLooper()));
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
                    title, parent -> new View(getContext()), Card.Companion.getTEXT_ONLY(), "",
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
        AtomicReference<MediaListener.MediaNotificationController> mnc = new AtomicReference<>();
        mnc.set(mediaListener.getTracking());
        if (mnc.get() != null) {
            NotificationInfo mediaInfo = new NotificationInfo(getContext(), mnc.get().getSbn());
            cards.add(new Card(null, null, parent -> {
                View mnv = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.media_notification, parent, false);
                TextView title = mnv.findViewById(R.id.notification_title);
                TextView author = mnv.findViewById(R.id.notification_author);
                ImageButton pause = mnv.findViewById(R.id.play_button);
                title.setText(mnc.get().getInfo().getTitle());
                author.setText(
                        mnc.get().getInfo().getAlbum() != null ? mnc.get().getInfo().getAlbum() : mnc.get().getInfo().getArtist());
                pause.setImageDrawable(
                        mnc.get().isPlaying() ? getContext().getDrawable(
                                R.drawable.ic_pause_black_24dp) : getContext().getDrawable(
                                R.drawable.ic_play_arrow_black_24dp));
                pause.setImageTintList(ColorStateList.valueOf(
                        FeedAdapter.Companion.getOverrideColor(getContext())));
                pause.setOnClickListener(cause -> mediaListener.toggle(true));
                onMediaNotifChange.add(() -> {
                    mnc.set(mediaListener.getTracking());
                    if (mnc.get() == null) {
                        this.getFeed().refresh(0, 0, true, true);
                    } else {
                        mnv.post(() -> {
                            pause.setImageTintList(ColorStateList.valueOf(
                                    FeedAdapter.Companion.getOverrideColor(getContext())));
                            title.setText(mnc.get().getInfo().getTitle());
                            author.setText(
                                    mnc.get().getInfo().getAlbum() != null ? mnc.get().getInfo().getAlbum() : mnc.get().getInfo().getArtist());
                            pause.setImageDrawable(
                                    mnc.get().isPlaying() ? getContext().getDrawable(
                                            R.drawable.ic_pause_black_24dp) : getContext().getDrawable(
                                            R.drawable.ic_play_arrow_black_24dp));
                            pause.setImageTintList(ColorStateList.valueOf(
                                    FeedAdapter.Companion.getOverrideColor(getContext())));
                        });
                    }
                });
                return mnv;
            }, Card.Companion.getRAISE() | Card.Companion.getNO_HEADER(), "nosort,top", mnc.get().getSbn().getId()));
        }
        return cards;
    }

    @Override
    public boolean isVolatile() {
        return true;
    }
}
