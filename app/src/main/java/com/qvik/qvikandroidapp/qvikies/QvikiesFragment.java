package com.qvik.qvikandroidapp.qvikies;

import android.database.Observable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qvik.qvikandroidapp.Injection;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.databinding.QvikieItemBinding;
import com.qvik.qvikandroidapp.databinding.QvikiesFragBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Display a grid of {@link Qvikie}s. User can choose to view all, engineers or designers qvikies.
 */
public class QvikiesFragment extends Fragment {

    private QvikiesViewModel mQvikiesViewModel;

    private QvikiesFragBinding mQvikiesFragBinding;

    private QvikiesAdapter mListAdapter;

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
        mQvikiesFragBinding = QvikiesFragBinding.inflate(inflater, container, false);

        mQvikiesFragBinding.setView(this);

        mQvikiesFragBinding.setViewmodel(mQvikiesViewModel);

        setHasOptionsMenu(true);

        return mQvikiesFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mQvikiesViewModel.start();
    }

    @Override
    public void onDestroy() {
        mListAdapter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mQvikiesViewModel.loadQvikies(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qvikies_fragment_menu, menu);
    }

    public void setViewModel(QvikiesViewModel viewModel) {
        mQvikiesViewModel = viewModel;
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_qvikies, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.engineers:
                        mQvikiesViewModel.setFiltering(QvikiesFilterType.ENGINEERS);
                        break;
                    case R.id.designers:
                        mQvikiesViewModel.setFiltering(QvikiesFilterType.DESIGNERS);
                        break;
                    default:
                        mQvikiesViewModel.setFiltering(QvikiesFilterType.ALL_QVIKIES);
                        break;
                }
                mQvikiesViewModel.loadQvikies(false);
                return true;
            }
        });

        popup.show();
    }

    private void setupListAdapter() {
        ListView listView =  mQvikiesFragBinding.qvikiesList;

        mListAdapter = new QvikiesAdapter(
                new ArrayList<Qvikie>(0),
                (QvikiesActivity) getActivity(),
                Injection.provideQvikiesRepository(getContext().getApplicationContext()),
                mQvikiesViewModel);
        listView.setAdapter(mListAdapter);
    }

    public static class QvikiesAdapter extends BaseAdapter {

        @Nullable
        private QvikieItemNavigator mQvikieItemNavigator;

        private final QvikiesViewModel mQvikiesViewModel;

        private List<Qvikie> mQvikies;

        private QvikiesRepository mQvikiesRepository;

        public QvikiesAdapter(List<Qvikie> qvikies, QvikiesActivity qvikieItemNavigator,
                              QvikiesRepository qvikiesRepository,
                              QvikiesViewModel qvikiesViewModel) {
            mQvikieItemNavigator = qvikieItemNavigator;
            mQvikiesViewModel = qvikiesViewModel;
            mQvikiesRepository = qvikiesRepository;
            setList(qvikies);
        }

        public void onDestroy() {
            mQvikieItemNavigator = null;
        }

        public void replaceData(List<Qvikie> qvikies) {
            setList(qvikies);
        }

        @Override
        public int getCount() {
            return mQvikies != null ? mQvikies.size() : 0;
        }

        @Override
        public Qvikie getItem(int position) {
            return mQvikies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Qvikie qvikie = getItem(position);
            QvikieItemBinding binding;
            if (convertView == null) {
                // Inflate
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                // Create the binding
                binding = QvikieItemBinding.inflate(inflater, parent, false);
            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(convertView);
            }

            final QvikieItemViewModel viewmodel = new QvikieItemViewModel(
                    parent.getContext().getApplicationContext(),
                    mQvikiesRepository
            );

            viewmodel.setNavigator(mQvikieItemNavigator);

            binding.setViewmodel(viewmodel);

            viewmodel.setQvikie(qvikie);

            return binding.getRoot();
        }


        private void setList(List<Qvikie> qvikies) {
            mQvikies = qvikies;
            notifyDataSetChanged();
        }
    }
}
