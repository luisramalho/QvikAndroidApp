package com.qvik.qvikandroidapp.qvikiedetail;

import android.content.Context;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.QvikieViewModel;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.qvikies.QvikiesFragment;

/**
 * Listens to user actions from the list item in ({@link QvikiesFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class QvikieDetailViewModel extends QvikieViewModel {

    @Nullable
    private QvikieDetailNavigator qvikieDetailNavigator;

    public QvikieDetailViewModel(Context context, QvikiesRepository qvikiesRepository) {
        super(context, qvikiesRepository);
    }

    public void setNavigator(QvikieDetailNavigator qvikieDetailNavigator) {
        this.qvikieDetailNavigator = qvikieDetailNavigator;
    }

    /**
     * Clears references to avoid memory leaks
     */
    public void onActivityDestroyed() {
        qvikieDetailNavigator = null;
    }

    /**
     * Called by the Data Binding Library
     */
    public void deleteQvikie() {
        super.deleteQvikie();
        if (qvikieDetailNavigator != null) {
            qvikieDetailNavigator.onQvikieDeleted();
        }
    }
}
