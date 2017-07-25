package com.qvik.qvikandroidapp.addeditqvikie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.qvik.qvikandroidapp.LifecycleAppCompatActivity;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.ViewModelFactory;
import com.qvik.qvikandroidapp.util.ActivityUtils;

import static com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieFragment.ARGUMENT_EDIT_QVIKIE_ID;

public class AddEditQvikieActivity extends LifecycleAppCompatActivity
        implements AddEditQvikieNavigation {

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_qvikie_act);

        setupToolbar();

        AddEditQvikieFragment addEditQvikieFragment = obtainViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                addEditQvikieFragment, R.id.contentFrame);

        subscribeToNavigationChanges();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

    @NonNull
    private AddEditQvikieFragment obtainViewFragment() {
        // View Fragment
        AddEditQvikieFragment addEditQvikieFragment = (AddEditQvikieFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (addEditQvikieFragment == null) {
            addEditQvikieFragment = AddEditQvikieFragment.newInstance();

            // Send the qvikie ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putString(ARGUMENT_EDIT_QVIKIE_ID,
                    getIntent().getStringExtra(ARGUMENT_EDIT_QVIKIE_ID));
            addEditQvikieFragment.setArguments(bundle);
        }
        return addEditQvikieFragment;
    }

    public static AddEditQvikieViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(AddEditQvikieViewModel.class);
    }

    private void subscribeToNavigationChanges() {
        AddEditQvikieViewModel viewModel = obtainViewModel(this);

        // The activity observes the navigation events in the ViewModel
        viewModel.getQvikieUpdatedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                AddEditQvikieActivity.this.onQvikieSaved();
            }
        });
    }

    @Override
    public void onQvikieSaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
