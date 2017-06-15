package com.qvik.qvikandroidapp.qvikies;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.qvik.qvikandroidapp.BR;
import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the data to be used in the qvikie list screen.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
public class QvikiesViewModel extends BaseObservable {

    public final ObservableList<Qvikie> qvikies = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noQvikiesLabel = new ObservableField<>();

    final ObservableField<String> snackbarText = new ObservableField<>();

    private QvikiesFilterType mCurrentFiltering = QvikiesFilterType.ALL_QVIKIES;

    private final QvikiesRepository mQvikiesRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private QvikiesNavigator mNavigator;

    public QvikiesViewModel(QvikiesRepository qvikiesRepository, Context context) {
        mQvikiesRepository = qvikiesRepository;
        mContext = context;

        // Set initial state
        setFiltering(QvikiesFilterType.ALL_QVIKIES);
    }

    void setNavigator(QvikiesNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start() {
        loadQvikies(false);
    }

    @Bindable
    public boolean isEmpty() {
        return qvikies.isEmpty();
    }

    public void loadQvikies(boolean forceUpdate) {
        loadQvikies(forceUpdate, true);
    }

    /**
     * Sets the current qvikie filtering type.
     *
     * @param requestType Can be {@link QvikiesFilterType#ALL_QVIKIES},
     *                    {@link QvikiesFilterType#ENGINEERS}, or
     *                    {@link QvikiesFilterType#DESIGNERS}
     */
    public void setFiltering(QvikiesFilterType requestType) {
        mCurrentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (requestType) {
            case ALL_QVIKIES:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noQvikiesLabel.set(mContext.getResources().getString(R.string.no_qvikies_all));
                break;
            case ENGINEERS:
                currentFilteringLabel.set(mContext.getString(R.string.label_engineers));
                noQvikiesLabel.set(mContext.getResources().getString(R.string.no_qvikies_engineers));
                break;
            case DESIGNERS:
                currentFilteringLabel.set(mContext.getString(R.string.label_designers));
                noQvikiesLabel.set(mContext.getResources().getString(R.string.no_qvikies_designers));
                break;
        }
    }


    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewQvikie() {
        if (mNavigator != null) {
            mNavigator.addNewQvikie();
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link QvikiesDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadQvikies(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {

            mQvikiesRepository.refreshQvikies();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mQvikiesRepository.getQvikies(new QvikiesDataSource.LoadQvikiesCallback() {
            @Override
            public void onQvikiesLoaded(List<Qvikie> qvikies) {
                List<Qvikie> qvikiesToShow = new ArrayList<Qvikie>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the tasks based on the requestType
                for (Qvikie qvikie : qvikies) {
                    switch (mCurrentFiltering) {
                        case ALL_QVIKIES:
                            qvikiesToShow.add(qvikie);
                            break;
                        case ENGINEERS:
                            if (qvikie.isEngineer()) {
                                qvikiesToShow.add(qvikie);
                            }
                            break;
                        case DESIGNERS:
                            if (qvikie.isDesigner()) {
                                qvikiesToShow.add(qvikie);
                            }
                            break;
                        default:
                            qvikiesToShow.add(qvikie);
                            break;
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                qvikies.clear();
                qvikies.addAll(qvikiesToShow);
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }
}
