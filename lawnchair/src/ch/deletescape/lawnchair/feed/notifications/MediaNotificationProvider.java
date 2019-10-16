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

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.notification.NotificationInfo;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.FeedProvider;

public class MediaNotificationProvider extends FeedProvider {
    private final List<Consumer<OMCMediaListener.MediaNotificationController>> onMediaNotifChange = new Vector<>();
    private OMCMediaListener mediaListener = null;

    public MediaNotificationProvider(Context c) {
        super(c);
        mediaListener = new OMCMediaListener(c,
                () -> onMediaNotifChange.forEach(it -> it.accept(mediaListener.getTracking())),
                new Handler(
                        LauncherModel.getWorkerLooper()));
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
        Vector<Card> cards = new Vector<>();
        AtomicReference<OMCMediaListener.MediaNotificationController> mnc = new AtomicReference<>();
        mnc.set(mediaListener.getTracking());
        Log.d(getClass().getName(), "getCards: mnc: " + mnc);
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
                pause.setOnClickListener(cause -> mediaListener.toggle(false));
                onMediaNotifChange.add(mn -> {
                    if (mn == null) {
                        this.getFeed().refresh(0, 0, true, true);
                    } else {
                        mnc.set(mn);
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
            }, Card.Companion.getRAISE() | Card.Companion.getNO_HEADER(), "nosort,top",
                    mnc.get().getSbn().getId()));
        }
        return cards;
    }

    @Override
    public boolean isVolatile() {
        return true;
    }
}
