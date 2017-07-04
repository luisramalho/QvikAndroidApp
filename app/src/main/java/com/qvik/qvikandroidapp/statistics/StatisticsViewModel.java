package com.qvik.qvikandroidapp.statistics;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.qvik.qvikandroidapp.R;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

import java.util.List;

/**
 * Exposes the data to be used in the statistics screen.
 * <p>
 * This ViewModel uses both {@link ObservableField}s ({@link ObservableBoolean}s in this case) and
 * {@link Bindable} getters. The values in {@link ObservableField}s are used directly in the layout,
 * whereas the {@link Bindable} getters allow us to add some logic to it. This is
 * preferable to having logic in the XML layout.
 */
public class StatisticsViewModel extends AndroidViewModel {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableBoolean error = new ObservableBoolean(false);

    public final ObservableField<String> qvikies = new ObservableField<>();

    public final ObservableField<String> engineers = new ObservableField<>();

    public final ObservableField<String> designers = new ObservableField<>();

    public final ObservableField<String> uncategorized = new ObservableField<>();

    /**
     * Controls whether the stats are shown or a "No data" message.
     */
    public final ObservableBoolean empty = new ObservableBoolean();

    private int numberOfQvikies = 0;

    private int numberOfEngineers = 0;

    private int numberOfDesigners = 0;

    private int numberOfUncategorized = 0;

    private Context context;

    private final QvikiesRepository qvikiesRepository;

    public StatisticsViewModel(Application context, QvikiesRepository
            qvikiesRepository) {
        super(context);
        this.context = context;
        this.qvikiesRepository = qvikiesRepository;
    }

    public void start() {
        loadStatistics();
    }

    void loadStatistics() {
        dataLoading.set(true);

        qvikiesRepository.getQvikies(new QvikiesDataSource.LoadQvikiesCallback() {
            @Override
            public void onQvikiesLoaded(List<Qvikie> qvikies) {
                error.set(false);
                resetStats();
                computeStats(qvikies);
            }

            @Override
            public void onDataNotAvailable() {
                error.set(true);
                resetStats();
                updateDataBindingObservables();
            }
        });
    }

    /**
     * Called when new data is ready.
     */
    private void computeStats(List<Qvikie> qvikies) {
        for (Qvikie qvikie : qvikies) {
            numberOfQvikies += 1;
            if (qvikie.isEngineer()) {
                numberOfEngineers += 1;
            } else  if (qvikie.isDesigner()) {
                numberOfDesigners += 1;
            } else {
                numberOfUncategorized += 1;
            }
        }

        updateDataBindingObservables();
    }

    private void updateDataBindingObservables() {
        qvikies.set(context.getString(R.string.statistics_qvikies, numberOfQvikies));
        engineers.set(context.getString(R.string.statistics_engineers, numberOfEngineers));
        designers.set(context.getString(R.string.statistics_designers, numberOfDesigners));
        uncategorized.set(context.getString(R.string.statistics_uncategorized, numberOfUncategorized));
        empty.set(numberOfQvikies == 0);

        dataLoading.set(false);
    }

    private void resetStats() {
        numberOfQvikies = 0;
        numberOfEngineers = 0;
        numberOfDesigners = 0;
        numberOfUncategorized = 0;
    }
}
