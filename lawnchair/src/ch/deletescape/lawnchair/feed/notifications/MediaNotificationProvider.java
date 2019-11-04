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

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.notification.NotificationInfo;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.awareness.VolumeManager;
import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.FeedProvider;
import kotlin.Unit;

public class MediaNotificationProvider extends FeedProvider {
    private final List<Consumer<OMCMediaListener.MediaNotificationController>> onMediaNotifChange = new Vector<>();
    private OMCMediaListener mediaListener;

    public MediaNotificationProvider(Context c) {
        super(c);
        mediaListener = new OMCMediaListener(c,
                () -> onMediaNotifChange.forEach(it -> it.accept(mediaListener.getTracking())));
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public List<Card> getCards() {
        Vector<Card> cards = new Vector<>();
        AtomicReference<OMCMediaListener.MediaNotificationController> mnc = new AtomicReference<>();
        mnc.set(mediaListener.getTracking());
        Log.d(getClass().getName(), "getCards: mnc: " + mnc);
        NotificationInfo mediaInfo = mnc.get() != null ? new NotificationInfo(getContext(),
                mnc.get().getSbn()) : null;
        cards.add(new Card(null, null, parent -> {
            View mnv = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.media_notification, parent, false);
            TextView title = mnv.findViewById(R.id.notification_title);
            TextView author = mnv.findViewById(R.id.notification_author);
            ImageButton pause = mnv.findViewById(R.id.play_button);
            ImageButton next = mnv.findViewById(R.id.next_track);
            ImageButton last = mnv.findViewById(R.id.last_track);
            ImageView icon = mnv.findViewById(R.id.media_icon);
            SeekBar seekbar = mnv.findViewById(R.id.volume_seekbar);
            View seekbarContainer = mnv.findViewById(R.id.volume_container);
            seekbarContainer.setAlpha(0);
            seekbar.setProgressTintList(
                    ColorStateList.valueOf(FeedAdapter.Companion.getOverrideColor(getContext(),
                            LawnchairUtilsKt.getColorEngineAccent(getContext()), true)));
            seekbar.setTickMarkTintList(ColorStateList.valueOf(
                    FeedAdapter.Companion.getOverrideColor(getContext(),
                            LawnchairUtilsKt.getColorEngineAccent(getContext()), true)));
            AtomicLong hideDelay = new AtomicLong(System.currentTimeMillis());
            AtomicBoolean trackingTouch = new AtomicBoolean(false);
            seekbar.setOnTouchListener((v, ev) -> {
                Objects.requireNonNull(getControllerView()).setDisallowInterceptCurrentTouchEvent(
                        true);
                return false;
            });
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        ((AudioManager) Objects.requireNonNull(
                                getContext().getSystemService(Context.AUDIO_SERVICE)))
                                .setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    trackingTouch.set(true);
                    if (seekbarContainer.getAlpha() == 0) {
                        seekbarContainer.animate().setDuration(200).alpha(1f);
                    }
                    hideDelay.addAndGet(1000);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            Thread.sleep(1800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (seekbarContainer) {
                            if (hideDelay.get() <= System.currentTimeMillis()) {
                                seekbarContainer.post(() -> {
                                    seekbarContainer.setAlpha(1f);
                                    seekbarContainer.setClickable(false);
                                    seekbarContainer.animate().setDuration(200).alpha(
                                            0f).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            trackingTouch.set(false);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                });
                            }
                        }
                    });
                }
            });
            VolumeManager.subscribe(seekbar::setProgress);
            VolumeManager.subscribe(value -> seekbarContainer.post(() -> {
                if (!trackingTouch.get()) {
                    hideDelay.set(System.currentTimeMillis() + 1800);
                    seekbarContainer.setClickable(true);
                    seekbarContainer.setAlpha(0);
                    seekbarContainer.animate().setDuration(200).alpha(1f);
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            Thread.sleep(1800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (seekbarContainer) {
                            if (hideDelay.get() <= System.currentTimeMillis()) {
                                seekbarContainer.post(() -> {
                                    seekbarContainer.setAlpha(1f);
                                    seekbarContainer.setClickable(false);
                                    seekbarContainer.animate().setDuration(200).alpha(
                                            0f);
                                });
                            }
                        }
                    });
                }
            }), false);

            TextView duration = mnv.findViewById(R.id.notification_duration);
            title.setText(
                    mnc.get() != null ? mnc.get().getInfo().getTitle() : getContext().getString(
                            R.string.title_nothings_playing));
            author.setText(
                    mnc.get() != null ? mnc.get().getInfo().getAlbum() != null ? mnc.get().getInfo().getAlbum() : mnc.get().getInfo().getArtist() : "");
            pause.setImageDrawable(
                    mnc.get() != null && mnc.get().isPlaying() ? getContext().getDrawable(
                            R.drawable.ic_pause_black_24dp) : getContext().getDrawable(
                            R.drawable.ic_play_arrow_black_24dp));
            pause.setImageTintList(ColorStateList.valueOf(
                    FeedAdapter.Companion.getOverrideColor(getContext())));
            next.setImageTintList(ColorStateList.valueOf(
                    FeedAdapter.Companion.getOverrideColor(getContext())));
            last.setImageTintList(ColorStateList.valueOf(
                    FeedAdapter.Companion.getOverrideColor(getContext())));
            icon.setVisibility(
                    mnc.get() != null && mnc.get().getInfo().getBitmap() != null ? View.VISIBLE : View.GONE);
            icon.setImageBitmap(mnc.get() != null ? mnc.get().getInfo().getBitmap() : null);
            if (mnc.get() != null && mnc.get().getInfo().getDuration() != -1) {
                long min = mnc.get().getInfo().getDuration() / 1000 / 60;
                int sec = (int) (mnc.get().getInfo().getDuration() / 1000) % 60;
                duration.setText(String.format(Locale.CHINA, "%02d:%02d", min, sec));
            } else {
                duration.setText(null);
            }
            pause.setOnClickListener(cause -> mediaListener.toggle(false));
            next.setOnClickListener(cause -> mediaListener.next());
            last.setOnClickListener(cause -> mediaListener.previous());
            onMediaNotifChange.add(mn -> {
                if (mn == null) {
                    mnc.set(null);
                    mnv.post(() -> {
                        title.setText(
                                mnc.get() != null ? mnc.get().getInfo().getTitle() : getContext().getString(
                                        R.string.title_nothings_playing));
                        author.setText(
                                mnc.get() != null ? mnc.get().getInfo().getAlbum() != null ? mnc.get().getInfo().getAlbum() : mnc.get().getInfo().getArtist() : "");
                        pause.setImageDrawable(
                                mnc.get() != null && mnc.get().isPlaying() ? getContext().getDrawable(
                                        R.drawable.ic_pause_black_24dp) : getContext().getDrawable(
                                        R.drawable.ic_play_arrow_black_24dp));
                        icon.setVisibility(
                                mnc.get() != null && mnc.get().getInfo().getBitmap() != null ? View.VISIBLE : View.GONE);
                        icon.setImageBitmap(
                                mnc.get() != null ? mnc.get().getInfo().getBitmap() : null);
                        if (mnc.get() != null && mnc.get().getInfo().getDuration() != -1) {
                            long min = mnc.get().getInfo().getDuration() / 1000 / 60;
                            int sec = (int) (mnc.get().getInfo().getDuration() / 1000) % 60;
                            duration.setText(String.format(Locale.CHINA, "%02d%02d", min, sec));
                        } else {
                            duration.setText(null);
                        }
                    });
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
                        icon.setVisibility(
                                mnc.get() != null && mnc.get().getInfo().getBitmap() != null ? View.VISIBLE : View.GONE);
                        icon.setImageBitmap(
                                mnc.get() != null ? mnc.get().getInfo().getBitmap() : null);
                        if (mnc.get() != null && mnc.get().getInfo().getDuration() != -1) {
                            long min = mnc.get().getInfo().getDuration() / 1000 / 60;
                            int sec = (int) (mnc.get().getInfo().getDuration() / 1000) % 60;
                            duration.setText(String.format(Locale.CHINA, "%02d:%02d", min, sec));
                        } else {
                            duration.setText(null);
                        }
                    });
                }
            });
            return mnv;
        }, Card.RAISE | Card.NO_HEADER, "nosort,top",
                mnc.get() != null ? mnc.get().getSbn().getId() : 13824221));
        cards.get(0).setGlobalClickListener(v -> {
            if (mnc.get() != null) {
                try {
                    mnc.get().getSbn().getNotification().contentIntent.send();
                } catch (PendingIntent.CanceledException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return Unit.INSTANCE;
        });
        return cards;
    }

    @Override
    public boolean isVolatile() {
        return true;
    }
}
