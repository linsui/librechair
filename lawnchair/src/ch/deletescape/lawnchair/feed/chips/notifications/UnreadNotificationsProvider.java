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

package ch.deletescape.lawnchair.feed.chips.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.service.notification.StatusBarNotification;

import androidx.core.graphics.ColorUtils;

import com.android.launcher3.notification.NotificationInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.colors.ColorEngine;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.impl.OverlayService;
import kotlin.Unit;

public class UnreadNotificationsProvider extends ChipProvider {
    private List<StatusBarNotification> notifications = new LinkedList<>();

    public UnreadNotificationsProvider(Context c) {
        OverlayService.CompanionService.InterfaceHolder.INSTANCE.setNotifChangedListener(notifs -> {
            notifications.clear();
            notifications.addAll(notifs);
            return Unit.INSTANCE;
        });
    }

    @Override
    public List<Item> getItems(Context context) {
        return notifications.stream().map(it -> {
            NotificationInfo info = new NotificationInfo(context, it);
            Item item = new Item();
            item.icon = info.getIconForBackground(context,
                    ColorUtils.setAlphaComponent(ColorEngine.getInstance(context).getResolverCache(
                            ColorEngine.Resolvers.FEED_CHIP).getValue().resolveColor(), 255));
            item.viewClickListener = v -> {
                if (info.intent != null) {
                    try {
                        info.intent.send();
                        if (info.autoCancel) {
                            info.intent.cancel();
                            notifications.remove(it);
                            refresh();
                        }
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            };
            item.title = info.title != null ? info.title.toString() : info.text != null ? info.text.toString() : "";
            return item;
        }).collect(Collectors.toList());
    }
}
