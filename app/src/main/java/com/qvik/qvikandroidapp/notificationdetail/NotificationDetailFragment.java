package com.qvik.qvikandroidapp.notificationdetail;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.databinding.NotificationDetailFragBinding;

public class NotificationDetailFragment extends LifecycleFragment {

    public static final String ARGUMENT_NOTIFICATION_ID = "NOTIFICATION_ID";

    NotificationDetailViewModel viewModel;

    public static NotificationDetailFragment newInstance(String notificationId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_NOTIFICATION_ID, notificationId);
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_detail_frag, container, false);

        NotificationDetailFragBinding viewDataBinding =
                NotificationDetailFragBinding.bind(view);

        viewModel = NotificationDetailActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewModel(viewModel);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.start(getArguments().getString(ARGUMENT_NOTIFICATION_ID));
    }
}
