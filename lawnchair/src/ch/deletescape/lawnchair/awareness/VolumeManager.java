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

package ch.deletescape.lawnchair.awareness;

import android.annotation.AnyThread;
import android.annotation.MainThread;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import ch.deletescape.lawnchair.feed.util.FeedUtil;

public class VolumeManager {
    private static int volume = 0;
    private static final List<Consumer<Integer>> listeners = new Vector<>();
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static final ContentObserver observer = new ContentObserver(
            new Handler(FeedUtil.apply(new HandlerThread("volume-manager"),
                    Thread::start).getLooper())) {
        int previousVolume;

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            assert audio != null;
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            int delta = previousVolume - currentVolume;

            if (delta > 0) {
                previousVolume = currentVolume;
                volume = previousVolume;
                synchronized (this) {
                    listeners.forEach(it -> it.accept(volume));
                }
            } else if (delta < 0) {
                previousVolume = currentVolume;
                volume = previousVolume;
                synchronized (this) {
                    listeners.forEach(it -> it.accept(volume));
                }
            }
        }
    };

    @MainThread
    public static void attachToContext(@NotNull @Nonnull Context context) {
        VolumeManager.context = context;
        volume = Objects.requireNonNull(
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(
                AudioManager.STREAM_MUSIC);
        context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true,
                observer);
    }

    @AnyThread
    public static void subscribe(@NotNull Consumer<Integer> consumer, boolean notifyCurrentValue) {
        if (notifyCurrentValue) {
            consumer.accept(volume);
        }
        synchronized (observer) {
            listeners.add(consumer);
        }
    }

    @AnyThread
    public static void subscribe(@NotNull Consumer<Integer> consumer) {
        subscribe(consumer, true);
    }
}
