package com.qvik.qvikandroidapp.qvikiedetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.SingleLiveEvent;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.qvikies.QvikiesFragment;

/**
 * Listens to user actions from the list item in ({@link QvikiesFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class QvikieDetailViewModel extends AndroidViewModel implements
        QvikiesDataSource.GetQvikieCallback {


    public final ObservableField<Qvikie> qvikie = new ObservableField<>();

    private SingleLiveEvent<Void> editQvikieCommand = new SingleLiveEvent<>();

    private SingleLiveEvent<Void> deleteQvikieCommand = new SingleLiveEvent<>();

    private final QvikiesRepository qvikiesRepository;

    private boolean isDataLoading;

    public QvikieDetailViewModel(Application context, QvikiesRepository qvikiesRepository) {
        super(context);
        this.qvikiesRepository = qvikiesRepository;
    }

    /**
     * Called by the Data Binding Library
     */
    public void deleteQvikie() {
        if (qvikie.get() != null) {
            qvikiesRepository.deleteQvikie(qvikie.get());
            deleteQvikieCommand.call();
        }
    }

    public void deleteQvikieById() {
        if (qvikie.get() != null) {
            qvikiesRepository.deleteQvikieById(qvikie.get().getId());
            deleteQvikieCommand.call();
        }
    }

    public SingleLiveEvent<Void> getEditQvikieCommand() {
        return editQvikieCommand;
    }

    public void editQvikie() {
        editQvikieCommand.call();
    }

    public SingleLiveEvent<Void> getDeleteQvikieCommand() {
        return deleteQvikieCommand;
    }

    public void start(String qvikieId) {
        if (qvikieId != null) {
            isDataLoading = true;
            qvikiesRepository.getQvikie(qvikieId, this);
        }
    }

    public void setQvikie(Qvikie qvikie) {
        this.qvikie.set(qvikie);
    }

    public boolean isDataAvailable() {
        return qvikie.get() != null;
    }

    public boolean isDataLoading() {
        return isDataLoading;
    }

    @Override
    public void onQvikieLoaded(Qvikie qvikie) {
        setQvikie(qvikie);
        isDataLoading = false;
    }

    @Override
    public void onDataNotAvailable() {
        qvikie.set(null);
        isDataLoading = false;
    }

    public void onRefresh() {
        if (qvikie.get() != null) {
            start(qvikie.get().getId());
        }
    }

    @Nullable
    protected String getQvikieId() {
        return qvikie.get().getId();
    }
}
