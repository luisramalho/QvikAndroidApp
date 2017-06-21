package com.qvik.qvikandroidapp.qvikiedetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.qvik.qvikandroidapp.Injection;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.ViewModelHolder;
import com.qvik.qvikandroidapp.util.ActivityUtils;

public class QvikieDetailActivity extends AppCompatActivity implements QvikieDetailNavigator {

    public static final String QVIKIEDETAIL_VIEWMODEL_TAG = "QVIKIEDETAIL_VIEWMODEL_TAG";

    public static final String EXTRA_QVIKIE_ID = "QVIKIE_ID";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    private QvikieDetailViewModel qvikieViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qvikiedetail_act);

        setupToolbar();

        QvikieDetailFragment qvikieDetailFragment = findOrCreateViewFragment();

        qvikieViewModel = findOrCreateViewModel();
        qvikieViewModel.setNavigator(this);

        // Link view and ViewModel
        qvikieDetailFragment.setViewModel(qvikieViewModel);
    }

    @Override
    protected void onDestroy() {
        qvikieViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private QvikieDetailFragment findOrCreateViewFragment() {
        String qvikieId = getIntent().getStringExtra(EXTRA_QVIKIE_ID);

        QvikieDetailFragment qvikieDetailFragment = (QvikieDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (qvikieDetailFragment == null) {
            qvikieDetailFragment = QvikieDetailFragment.newInstance(qvikieId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    qvikieDetailFragment, R.id.contentFrame);
        }

        return qvikieDetailFragment;
    }

    @NonNull
    private QvikieDetailViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present.
        // It's retained using the Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<QvikieDetailViewModel> retainedViewModel =
                (ViewModelHolder<QvikieDetailViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(QVIKIEDETAIL_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            QvikieDetailViewModel viewModel = new QvikieDetailViewModel(
                    getApplicationContext(),
                    Injection.provideQvikiesRepository(getApplicationContext())
            );

            // bind the ViewModel to this Activitiy's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    QVIKIEDETAIL_VIEWMODEL_TAG
            );
            return viewModel;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onQvikieDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the task was deleted successfully, go back to the list.
        finish();
    }
}
