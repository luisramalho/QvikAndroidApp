package com.qvik.qvikandroidapp.notifications;

/**
 * Defines the navigation actions that can be called from a list item in the
 * notification list.
 */
public interface NotificationItemNavigator {

    void openNotificationDetail(String notificationId);
}
