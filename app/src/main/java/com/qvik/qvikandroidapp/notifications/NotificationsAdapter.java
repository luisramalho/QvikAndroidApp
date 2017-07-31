package com.qvik.qvikandroidapp.notifications;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.databinding.NotificationItemBinding;

import java.util.List;

public class NotificationsAdapter extends BaseAdapter {

    private final NotificationsViewModel notificationsViewModel;

    private List<Notification> notifications;

    public NotificationsAdapter(List<Notification> notifications,
                                NotificationsViewModel notificationsViewModel) {
        this.notificationsViewModel = notificationsViewModel;
        setList(notifications);

    }

    public void replaceData(List<Notification> notifications) {
        setList(notifications);
    }

    @Override
    public int getCount() {
        return notifications != null ? notifications.size() : 0;
    }

    @Override
    public Notification getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View view, final ViewGroup viewGroup) {
        NotificationItemBinding binding;
        if (view == null) {
            // Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            // Create the binding
            binding = NotificationItemBinding.inflate(inflater, viewGroup, false);
        } else {
            // Recycling view
            binding = DataBindingUtil.getBinding(view);
        }

        NotificationItemUserActionsListener userActionsListener =
                new NotificationItemUserActionsListener() {

            @Override
            public void onNotificationClicked(Notification notification) {
                notificationsViewModel.getOpenNotificationEvent().setValue(notification.getId());
            }
        };

        binding.setNotification(notifications.get(position));

        binding.setListener(userActionsListener);

        binding.executePendingBindings();
        return binding.getRoot();
    }


    private void setList(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
}
