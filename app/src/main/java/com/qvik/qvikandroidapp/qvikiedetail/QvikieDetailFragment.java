package com.qvik.qvikandroidapp.qvikiedetail;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.databinding.QvikiedetailFragBinding;

public class QvikieDetailFragment extends LifecycleFragment {

    public static final String ARGUMENT_QVIKIE_ID = "QVIKIE_ID";

    private QvikieDetailViewModel viewModel;

    public static QvikieDetailFragment newInstance(String qvikieId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_QVIKIE_ID, qvikieId);
        QvikieDetailFragment fragment = new QvikieDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.start(getArguments().getString(ARGUMENT_QVIKIE_ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qvikiedetail_frag, container, false);

        QvikiedetailFragBinding viewDataBinding = QvikiedetailFragBinding.bind(view);

        viewModel = QvikieDetailActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewmodel(viewModel);

        return view;
    }
}
