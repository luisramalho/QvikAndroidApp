package com.qvik.qvikandroidapp.notifications;

import com.qvik.qvikandroidapp.data.Notification;

/**
 * Listener used with data binding to process user actions.
 */
public interface NotificationItemUserActionsListener {

    void onNotificationClicked(Notification notification);
}
