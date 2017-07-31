package com.qvik.qvikandroidapp.notifications;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.databinding.NotificationsFragBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsFragBinding notificationsFragBinding;

    private NotificationsViewModel notificationsViewModel;

    private NotificationsAdapter listAdapter;

    public NotificationsFragment() {
        // Empty public constructor required
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        notificationsFragBinding = DataBindingUtil.inflate(
                inflater, R.layout.notifications_frag, container, false);

        notificationsViewModel = NotificationsActivity.obtainViewModel(getActivity());

        notificationsFragBinding.setViewModel(notificationsViewModel);

        return notificationsFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        notificationsViewModel.start();
    }

    private void setupListAdapter() {
        ListView listView =  notificationsFragBinding.notificationsList;

        listAdapter = new NotificationsAdapter(new ArrayList<Notification>(0),
                notificationsViewModel);
        listView.setAdapter(listAdapter);
    }
}
