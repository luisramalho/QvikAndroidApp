package com.qvik.qvikandroidapp.qvikies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.qvik.qvikandroidapp.LifecycleAppCompatActivity;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.ViewModelFactory;
import com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieActivity;
import com.qvik.qvikandroidapp.auth.AuthActivity;
import com.qvik.qvikandroidapp.qvikiedetail.QvikieDetailActivity;
import com.qvik.qvikandroidapp.statistics.StatisticsActivity;
import com.qvik.qvikandroidapp.util.ActivityUtils;

public class QvikiesActivity extends LifecycleAppCompatActivity implements QvikieItemNavigator, QvikiesNavigator {

    private DrawerLayout drawerLayout;

    private QvikiesViewModel viewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Toast.makeText(QvikiesActivity.this, getString(R.string.title_home), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_dashboard:
                    Toast.makeText(QvikiesActivity.this, getString(R.string.title_dashboard), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(QvikiesActivity.this, getString(R.string.title_notifications), Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qvikies_act);

        setupToolbar();

        setupNavigationDrawer();

        setupViewFragment();

        viewModel = obtainViewModel(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Subscribe to "open qvikie" event
        viewModel.getOpenQvikieEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String qvikieId) {
                if (qvikieId != null) {
                    openQvikieDetails(qvikieId);
                }
            }
        });

        // Subscribe to "new qvikie" event
        viewModel.getNewQvikieEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                addNewQvikie();
            }
        });
    }

    public static QvikiesViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(QvikiesViewModel.class);
    }

    private void setupViewFragment() {
        QvikiesFragment qvikiesFragment =
                (QvikiesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (qvikiesFragment == null) {
            // Create the fragment
            qvikiesFragment = QvikiesFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), qvikiesFragment, R.id.contentFrame);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            case R.id.statistics_navigation_menu_item:
                                Intent intent = new Intent(
                                        QvikiesActivity.this,
                                        StatisticsActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.sign_out_navigation_menu_item:
                                AuthUI.getInstance()
                                        .signOut(QvikiesActivity.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // user is now signed out
                                                startActivity(new Intent(
                                                        QvikiesActivity.this,
                                                        AuthActivity.class));
                                                finish();
                                            }
                                        });
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        viewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void openQvikieDetails(String qvikieId) {
        Intent intent = new Intent(this, QvikieDetailActivity.class);
        intent.putExtra(QvikieDetailActivity.EXTRA_QVIKIE_ID, qvikieId);
        startActivity(intent);
    }

    @Override
    public void addNewQvikie() {
        Intent intent = new Intent(this, AddEditQvikieActivity.class);
        startActivityForResult(intent, AddEditQvikieActivity.REQUEST_CODE);
    }
}
