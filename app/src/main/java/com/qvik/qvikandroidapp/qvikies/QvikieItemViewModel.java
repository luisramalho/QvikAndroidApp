package com.qvik.qvikandroidapp.qvikies;

import android.content.Context;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.QvikieViewModel;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

import java.lang.ref.WeakReference;

/**
 * Listens to user actions from the list item in ({@link QvikiesFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class QvikieItemViewModel extends QvikieViewModel {

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a list adapter.
    @Nullable
    private WeakReference<QvikieItemNavigator> mNavigator;

    public QvikieItemViewModel(Context context, QvikiesRepository qvikiesRepository) {
        super(context, qvikiesRepository);
    }

    public void setNavigator(QvikieItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    public void qvikieClicked() {
        String qvikieId = getQvikieId();
        if (qvikieId == null) {
            // Click happened before qvikie was loaded, no-op.
            return;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            mNavigator.get().openQvikieDetails(qvikieId);
        }
    }
}
