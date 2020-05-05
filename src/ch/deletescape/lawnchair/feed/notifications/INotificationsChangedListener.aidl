package ch.deletescape.lawnchair.feed.notifications;

import android.service.notification.StatusBarNotification;

interface INotificationsChangedListener {
    oneway void notificationsChanged(in List<StatusBarNotification> notifs);
    oneway void notificationRemoved(String key);
    oneway void notificationPosted(in StatusBarNotification notif);
}
