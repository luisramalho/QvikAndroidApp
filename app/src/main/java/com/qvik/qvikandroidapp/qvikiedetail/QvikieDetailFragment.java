package com.qvik.qvikandroidapp.qvikiedetail;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.databinding.QvikieDetailFragBinding;

public class QvikieDetailFragment extends LifecycleFragment {

    public static final String ARGUMENT_QVIKIE_ID = "QVIKIE_ID";

    public static final int REQUEST_EDIT_QVIKIE = 1;

    private QvikieDetailViewModel viewModel;

    public static QvikieDetailFragment newInstance(String qvikieId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_QVIKIE_ID, qvikieId);
        QvikieDetailFragment fragment = new QvikieDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qvikie_detail_frag, container, false);

        QvikieDetailFragBinding viewDataBinding = QvikieDetailFragBinding.bind(view);

        viewModel = QvikieDetailActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewModel(viewModel);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.start(getArguments().getString(ARGUMENT_QVIKIE_ID));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                viewModel.deleteQvikie();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qvikie_detail_frag_menu, menu);
    }

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_qvikie);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.editQvikie();
            }
        });
    }
}
