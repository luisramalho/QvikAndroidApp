package com.qvik.qvikandroidapp.qvikies;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.databinding.QvikiesFragBinding;

import java.util.ArrayList;

/**
 * Display a grid of {@link Qvikie}s. User can choose to view all, engineers or designers qvikies.
 */
public class QvikiesFragment extends LifecycleFragment {

    private QvikiesViewModel qvikiesViewModel;

    private QvikiesFragBinding qvikiesFragBinding;

    private QvikiesAdapter listAdapter;

    public QvikiesFragment() {
        // Empty public constructor required
    }

    public static QvikiesFragment newInstance() {
        return new QvikiesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        qvikiesFragBinding = QvikiesFragBinding.inflate(inflater, container, false);

        qvikiesViewModel = QvikiesActivity.obtainViewModel(getActivity());

        qvikiesFragBinding.setViewmodel(qvikiesViewModel);

        setHasOptionsMenu(true);

        return qvikiesFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        qvikiesViewModel.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                qvikiesViewModel.loadQvikies(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qvikies_fragment_menu, menu);
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_qvikies, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.engineers:
                        qvikiesViewModel.setFiltering(QvikiesFilterType.ENGINEERS);
                        break;
                    case R.id.designers:
                        qvikiesViewModel.setFiltering(QvikiesFilterType.DESIGNERS);
                        break;
                    default:
                        qvikiesViewModel.setFiltering(QvikiesFilterType.ALL_QVIKIES);
                        break;
                }
                qvikiesViewModel.loadQvikies(false);
                return true;
            }
        });

        popup.show();
    }

    private void setupListAdapter() {
        ListView listView =  qvikiesFragBinding.qvikiesList;

        listAdapter = new QvikiesAdapter(new ArrayList<Qvikie>(0), qvikiesViewModel);
        listView.setAdapter(listAdapter);
    }
}
