package ch.deletescape.lawnchair.feed.notifications;

import android.service.notification.StatusBarNotification;

interface INotificationsChangedListener {
    oneway void notificationsChanged(in List<StatusBarNotification> notifs);
}
