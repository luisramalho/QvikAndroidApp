package com.qvik.qvikandroidapp.notifications;

import android.databinding.BindingAdapter;
import android.widget.ListView;

import com.qvik.qvikandroidapp.data.Notification;

import java.util.List;

/**
 *  Contains {@link BindingAdapter}s for the {@link Notification} list.
 */
public class NotificationsListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<Notification> notifications) {
        NotificationsAdapter adapter = (NotificationsAdapter) listView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(notifications);
        }
    }
}
