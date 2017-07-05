package com.qvik.qvikandroidapp.addeditqvikie;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.databinding.AddEditQvikieFragBinding;

/**
 * Main UI for the add qvikie screen.
 */
public class AddEditQvikieFragment extends LifecycleFragment {

    public static final String ARGUMENT_EDIT_QVIKIE_ID = "EDIT_QVIKIE_ID";

    AddEditQvikieViewModel viewModel;

    AddEditQvikieFragBinding viewDataBinding;

    public static AddEditQvikieFragment newInstance() {
        return new AddEditQvikieFragment();
    }

    public AddEditQvikieFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupActionBar();

        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.add_edit_qvikie_frag,
                container, false);
        if (viewDataBinding == null) {
            viewDataBinding = AddEditQvikieFragBinding.bind(root);
        }

        viewModel = AddEditQvikieActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewModel(viewModel);

        setHasOptionsMenu(true);

        setRetainInstance(false);

        return viewDataBinding.getRoot();
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) getActivity()
                .findViewById(R.id.fab_edit_qvikie_done);
        fab.setImageResource(R.drawable.ic_done_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.saveQvikie();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity())
                .getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments() != null) {
            actionBar.setTitle(R.string.edit_qvikie);
        } else {
            actionBar.setTitle(R.string.add_qvikie);
        }
    }

    /**
     * Add or edit an existant qvikie
     */
    private void loadData() {
        if (getArguments() != null) {
            viewModel.start(getArguments().getString(ARGUMENT_EDIT_QVIKIE_ID));
        } else {
            viewModel.start(null);
        }
    }
}
