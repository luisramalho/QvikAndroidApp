package com.qvik.qvikandroidapp.qvikiedetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.qvik.qvikandroidapp.LifecycleAppCompatActivity;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.ViewModelFactory;
import com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieActivity;
import com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieFragment;
import com.qvik.qvikandroidapp.util.ActivityUtils;

import static com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieActivity.ADD_EDIT_RESULT_OK;
import static com.qvik.qvikandroidapp.qvikiedetail.QvikieDetailFragment.REQUEST_EDIT_QVIKIE;

public class QvikieDetailActivity extends LifecycleAppCompatActivity implements QvikieDetailNavigator {

    public static final String EXTRA_QVIKIE_ID = "QVIKIE_ID";

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 3;

    private QvikieDetailViewModel qvikieViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qvikie_detail_act);

        setupToolbar();

        QvikieDetailFragment qvikieDetailFragment = findOrCreateViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                qvikieDetailFragment, R.id.contentFrame);

        qvikieViewModel = obtainViewModel(this);

        subscribeToNavigationChanges(qvikieViewModel);
    }


    @NonNull
    private QvikieDetailFragment findOrCreateViewFragment() {
        String qvikieId = getIntent().getStringExtra(EXTRA_QVIKIE_ID);

        QvikieDetailFragment qvikieDetailFragment = (QvikieDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (qvikieDetailFragment == null) {
            qvikieDetailFragment = QvikieDetailFragment.newInstance(qvikieId);
        }

        return qvikieDetailFragment;
    }

    @NonNull
    public static QvikieDetailViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(QvikieDetailViewModel.class);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void subscribeToNavigationChanges(QvikieDetailViewModel viewModel) {
        // The activity observes the navigation commands in the ViewModel
        viewModel.getEditQvikieCommand().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                QvikieDetailActivity.this.onStartEditQvikie();
            }
        });
        viewModel.getDeleteQvikieCommand().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                QvikieDetailActivity.this.onQvikieDeleted();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_QVIKIE) {
            // If the qvikie was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onQvikieDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the qvikie was deleted successfully, go back to the list.
        finish();
    }

    @Override
    public void onStartEditQvikie() {
        String qvikieId = getIntent().getStringExtra(EXTRA_QVIKIE_ID);
        Intent intent = new Intent(this, AddEditQvikieActivity.class);
        intent.putExtra(AddEditQvikieFragment.ARGUMENT_EDIT_QVIKIE_ID, qvikieId);
        startActivityForResult(intent, REQUEST_EDIT_QVIKIE);
    }
}
