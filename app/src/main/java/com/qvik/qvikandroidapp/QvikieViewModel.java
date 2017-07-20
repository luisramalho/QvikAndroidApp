package com.qvik.qvikandroidapp;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

/**
 * Abstract class for View Models that expose a single {@link Qvikie}.
 */
public abstract class QvikieViewModel extends BaseObservable implements QvikiesDataSource.GetQvikieCallback {


    public final ObservableField<String> snackbarText = new ObservableField<>();

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> description = new ObservableField<>();

    public final ObservableField<String> email = new ObservableField<>();

    public final ObservableField<String> phoneNumber = new ObservableField<>();

    private final ObservableField<Qvikie> mQvikieObservable = new ObservableField<>();

    private final QvikiesRepository mQvikiesRepository;

    private final Context mContext;

    private boolean mIsDataLoading;

    public QvikieViewModel(Context context, QvikiesRepository qvikiesRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mQvikiesRepository = qvikiesRepository;

        // Exposed observables depend on the mQvikieObservable observable:
        mQvikieObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Qvikie qvikie = mQvikieObservable.get();
                if (qvikie != null) {
                    name.set(qvikie.getName());
                    title.set(qvikie.getTitle());
                    description.set(qvikie.getDescription());
                    email.set(qvikie.getEmail());
                    phoneNumber.set(qvikie.getPhoneNumber());
                } else {
                    name.set(mContext.getString(R.string.no_name));
                    title.set(mContext.getString(R.string.no_title));
                    description.set(mContext.getString(R.string.no_data_description));
                    email.set("");
                    phoneNumber.set(mContext.getString(R.string.no_data_phone_number));
                }
            }
        });
    }

    public void start(String qvikieId) {
        if (qvikieId != null) {
            mIsDataLoading = true;
            mQvikiesRepository.getQvikie(qvikieId, this);
        }
    }

    public void setQvikie(Qvikie qvikie) {
        mQvikieObservable.set(qvikie);
    }

    @Bindable
    public boolean isDataAvailable() {
        return mQvikieObservable.get() != null;
    }

    @Bindable
    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    // This could be an observable, but we save a call to Qvikie.getTitleForList() if not needed.
    @Bindable
    public String getTitleForList() {
        if (mQvikieObservable.get() == null) {
            return "No data";
        }
        return mQvikieObservable.get().getTitleForList();
    }

    @Override
    public void onQvikieLoaded(Qvikie qvikie) {
        mQvikieObservable.set(qvikie);
        mIsDataLoading = false;
        notifyChange(); // For the @Bindable properties
    }

    @Override
    public void onDataNotAvailable() {
        mQvikieObservable.set(null);
        mIsDataLoading = false;
    }

    public void deleteQvikieById() {
        if (mQvikieObservable.get() != null) {
            mQvikiesRepository.deleteQvikieById(mQvikieObservable.get().getId());
        }
    }

    public void deleteQvikie() {
        if (mQvikieObservable.get() != null) {
            mQvikiesRepository.deleteQvikie(mQvikieObservable.get());
        }
    }

    public void onRefresh() {
        if (mQvikieObservable.get() != null) {
            start(mQvikieObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    @Nullable
    protected String getQvikieId() {
        return mQvikieObservable.get().getId();
    }
}
