package com.qvik.qvikandroidapp.notifications;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.databinding.NotificationsFragBinding;

public class NotificationsFragment extends Fragment {

    private NotificationsFragBinding notificationsFragBinding;

    private NotificationsViewModel notificationsViewModel;

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
    public void onResume() {
        super.onResume();

        notificationsViewModel.start();
    }
}
