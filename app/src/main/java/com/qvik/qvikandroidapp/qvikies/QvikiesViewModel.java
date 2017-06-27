package com.qvik.qvikandroidapp.qvikies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.SingleLiveEvent;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the data to be used in the qvikie list screen.
 */
public class QvikiesViewModel extends AndroidViewModel {

    // These observable fields will update Views automatically
    public final ObservableList<Qvikie> items = new ObservableArrayList<>();

    public final ObservableBoolean empty = new ObservableBoolean(false);

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noQvikiesLabel = new ObservableField<>();

    private QvikiesFilterType currentFiltering = QvikiesFilterType.ALL_QVIKIES;

    private final QvikiesRepository qvikiesRepository;

    private final ObservableBoolean isDataLoadingError = new ObservableBoolean(false);

    private SingleLiveEvent<String> openQvikieEvent = new SingleLiveEvent<>();

    private Context context; // To avoid leaks, this must be an Application Context.

    public QvikiesViewModel(Application context, QvikiesRepository qvikiesRepository) {
        super(context);
        this.context = context.getApplicationContext();
        this.qvikiesRepository = qvikiesRepository;

        // Set initial state
        setFiltering(QvikiesFilterType.ALL_QVIKIES);
    }

    public void start() {
        loadQvikies(false);
    }

    public void loadQvikies(boolean forceUpdate) {
        loadQvikies(forceUpdate, true);
    }

    SingleLiveEvent<String> getOpenQvikieEvent() {
        return openQvikieEvent;
    }

    /**
     * Sets the current qvikie filtering type.
     *
     * @param requestType Can be {@link QvikiesFilterType#ALL_QVIKIES},
     *                    {@link QvikiesFilterType#ENGINEERS}, or
     *                    {@link QvikiesFilterType#DESIGNERS}
     */
    public void setFiltering(QvikiesFilterType requestType) {
        currentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (requestType) {
            case ALL_QVIKIES:
                currentFilteringLabel.set(context.getString(R.string.label_all));
                noQvikiesLabel.set(context.getResources().getString(R.string.no_qvikies_all));
                break;
            case ENGINEERS:
                currentFilteringLabel.set(context.getString(R.string.label_engineers));
                noQvikiesLabel.set(context.getResources().getString(R.string.no_qvikies_engineers));
                break;
            case DESIGNERS:
                currentFilteringLabel.set(context.getString(R.string.label_designers));
                noQvikiesLabel.set(context.getResources().getString(R.string.no_qvikies_designers));
                break;
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
            qvikiesRepository.refreshQvikies();
        }

        qvikiesRepository.getQvikies(new QvikiesDataSource.LoadQvikiesCallback() {
            @Override
            public void onQvikiesLoaded(List<Qvikie> qvikies) {
                List<Qvikie> qvikiesToShow = new ArrayList<>();

                // We filter the qvikies based on the requestType
                for (Qvikie qvikie : qvikies) {
                    switch (currentFiltering) {
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
                isDataLoadingError.set(false);

                items.clear();
                items.addAll(qvikiesToShow);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                isDataLoadingError.set(true);
            }
        });
    }
}
