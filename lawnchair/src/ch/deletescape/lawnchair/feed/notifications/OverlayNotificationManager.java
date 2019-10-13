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

import android.annotation.AnyThread;
import android.annotation.MainThread;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.impl.OverlayService;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public final class OverlayNotificationManager
        implements Function1<List<? extends StatusBarNotification>, Unit> {
    private static final List<Consumer<List<StatusBarNotification>>> listeners = new Vector<>();
    private static final Handler mainThread = new Handler(Looper.getMainLooper());
    private static final OverlayNotificationManager sCallback = new OverlayNotificationManager();
    private static List<StatusBarNotification> current;

    static {
        OverlayService.CompanionService.InterfaceHolder.INSTANCE.setNotifChangedListener(sCallback);
    }

    @AnyThread
    public static void addListener(
            @MainThread @NotNull Consumer<List<StatusBarNotification>> listener) {
        if (current != null) {
            List<StatusBarNotification> current = OverlayNotificationManager.current;
            mainThread.post(() -> listener.accept(current));
            listener.accept(current);
        }
        listeners.add(listener);
    }

    @AnyThread
    public static void removeListener(@NotNull Consumer<List<StatusBarNotification>> listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        List<StatusBarNotification> current = OverlayNotificationManager.current;
        mainThread.post(() -> {
            if (current != null) {
                listeners.forEach(it -> it.accept(current));
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized Unit invoke(List<? extends StatusBarNotification> notifications) {
        current = (List<StatusBarNotification>) notifications;
        notifyListeners();
        return Unit.INSTANCE;
    }
}
