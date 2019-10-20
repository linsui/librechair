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

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.FeedProviderContainer;
import ch.deletescape.lawnchair.feed.MainFeedController;
import ch.deletescape.lawnchair.reflection.ReflectionUtils;

public final class NotificationManager {
    private volatile static NotificationManager sInstance;
    private final WeakReference<Application> context;
    private final Map<FeedProviderContainer, NotificationChannel> channels;
    private final android.app.NotificationManager manager;
    private final Handler mainThreadHandler;

    @AnyThread
    private NotificationManager(@NonNull Context context) {
        if (ReflectionUtils.getCallingClass() != NotificationManager.class) {
            throw new SecurityException(
                    "This class shouldn't be instantiated outside of getInstance (was instantiated in " + ReflectionUtils.getCallingClass() + ")");
        }
        this.context = new WeakReference<>((Application) context.getApplicationContext());
        this.channels = new LinkedHashMap<>();
        this.mainThreadHandler = new Handler(context.getMainLooper());
        this.manager = (android.app.NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    @AnyThread
    @NonNull
    public static synchronized NotificationManager getInstance(@NonNull Context context) {
        return sInstance == null ? (sInstance = new NotificationManager(context)) : sInstance;
    }

    @SuppressWarnings("ConstantConditions")
    @AnyThread
    public synchronized void postNotification(@NonNull FeedProvider provider,
                                              @NonNull Integer icon,
                                              @NonNull String title,
                                              @NonNull String message,
                                              @Nullable PendingIntent intent,
                                              @Nullable List<Pair<String, PendingIntent>> actions) {
        mainThreadHandler.post(() -> {
            if (provider == null || icon == null || title == null || message == null) {
                throw new RuntimeException("no arguments may be null");
            }
            if (!channels.containsKey(provider.getContainer())) {
                if (manager.getNotificationChannel(
                        new Gson().toJson(provider.getContainer())) != null) {
                    channels.put(provider.getContainer(),
                            manager.getNotificationChannel(
                                    new Gson().toJson(provider.getContainer())));
                } else {
                    NotificationChannel channel = new NotificationChannel(
                            new Gson().toJson(provider.getContainer()),
                            MainFeedController.Companion.getDisplayName(provider.getContainer(),
                                    context.get()),
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                    channels.put(provider.getContainer(), channel);
                }
            }
            NotificationChannel channel = channels.get(provider.getContainer());
            Notification.Builder builder = new Notification.Builder(context.get(),
                    channel.getId()).setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(intent);
            if (actions != null) {
                actions.forEach(it -> builder.addAction(new Notification.Action.Builder(null, it.first,
                        it.second).build()));
            }
            manager.notify(UUID.randomUUID().hashCode(), builder.build());
        });
    }
}
