package com.qvik.qvikandroidapp.qvikies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.qvik.qvikandroidapp.Injection;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.ViewModelHolder;
import com.qvik.qvikandroidapp.util.ActivityUtils;
import com.qvik.qvikandroidapp.util.EspressoIdlingResource;

public class QvikiesActivity extends AppCompatActivity implements QvikieItemNavigator, QvikiesNavigator {

    public static final String QVIKIES_VIEWMODEL_TAG = "QVIKIES_VIEWMODEL_TAG";

    private DrawerLayout mDrawerLayout;

    private QvikiesViewModel mViewModel;

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

        QvikiesFragment qvikiesFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Link View and ViewModel
        qvikiesFragment.setViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private QvikiesViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<QvikiesViewModel> retainedViewModel =
                (ViewModelHolder<QvikiesViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(QVIKIES_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            QvikiesViewModel viewModel = new QvikiesViewModel(
                    Injection.provideQvikiesRepository(getApplicationContext()),
                    getApplicationContext());
            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    QVIKIES_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private QvikiesFragment findOrCreateViewFragment() {
        QvikiesFragment qvikiesFragment =
                (QvikiesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (qvikiesFragment == null) {
            // Create the fragment
            qvikiesFragment = QvikiesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), qvikiesFragment, R.id.contentFrame);
        }
        return qvikiesFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
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
//                                Intent intent =
//                                        new Intent(QvikiesActivity.this, StatisticsActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void addNewQvikie() {
        //Intent intent = new Intent(this, AddEditQvikieActivity.class);
        //startActivityForResult(intent, AddEditQvikieActivity.REQUEST_CODE);
    }

    @Override
    public void openQvikieDetails(String qvikieId) {
        //Intent intent = new Intent(this, QvikieDetailActivity.class);
        //intent.putExtra(QvikieDetailActivity.EXTRA_QVIKIE_ID, qvikieId);
        //startActivityForResult(intent, AddEditQvikieActivity.REQUEST_CODE);
    }
}
