package com.qvik.qvikandroidapp.addeditqvikie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.SingleLiveEvent;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

/**
 * ViewModel for the Add/Edit screen.
 * <p>
 * This ViewModel only exposes {@link ObservableField}s, so it doesn't need to extend
 * {@link android.databinding.BaseObservable} and updates are notified automatically.
 */
public class AddEditQvikieViewModel extends AndroidViewModel
        implements QvikiesDataSource.GetQvikieCallback {

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> description = new ObservableField<>();

    public final ObservableField<String> email = new ObservableField<>();

    public final ObservableField<String> phoneNumber = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private final SingleLiveEvent<Void> qvikieUpdatedEvent = new SingleLiveEvent<>();

    private final QvikiesRepository qvikiesRepository;

    @Nullable
    private String qvikieId;

    private boolean isNewQvikie;

    private boolean isDataLoaded = false;

    public AddEditQvikieViewModel(Application context,
                                  QvikiesRepository qvikiesRepository) {
        super(context);
        this.qvikiesRepository = qvikiesRepository;
    }

    public void start(String qvikieId) {
        if (dataLoading.get()) {
            // It's already running, ignore.
            return;
        }

        this.qvikieId = qvikieId;
        if (qvikieId == null) {
            // It's a new qvikie, do not populate.
            isNewQvikie = true;
            return;
        }

        if (isDataLoaded) {
            // Already have data, do not populate.
            return;
        }

        isNewQvikie = false;
        dataLoading.set(true);

        qvikiesRepository.getQvikie(qvikieId, this);
    }

    @Override
    public void onQvikieLoaded(Qvikie qvikie) {
        name.set(qvikie.getName());
        title.set(qvikie.getTitle());
        description.set(qvikie.getDescription());
        email.set(qvikie.getEmail());
        phoneNumber.set(qvikie.getPhoneNumber());
        dataLoading.set(false);
        isDataLoaded = true;
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    void saveQvikie(String name, String title, String description,
                    String phoneNumber, String email) {
        Qvikie qvikie = new Qvikie(name, title, description, phoneNumber, email);
        if (qvikie.isEmpty()) {
            return;
        }
        if (isNewQvikie() || qvikieId == null) {
            createQvikie(qvikie);
        } else {
            qvikie = new Qvikie(qvikieId, name, title, description, phoneNumber, email);
            updateQvikie(qvikie);
        }
    }

    private void createQvikie(Qvikie newQvikie) {
        qvikiesRepository.saveQvikie(newQvikie);
        qvikieUpdatedEvent.call();
    }

    private void updateQvikie(Qvikie qvikie) {
        if (isNewQvikie()) {
            throw new RuntimeException("updateQvikie() was called but qvikie is new.");
        }
        qvikiesRepository.saveQvikie(qvikie);
        qvikieUpdatedEvent.call();
    }

    SingleLiveEvent<Void> getQvikieUpdatedEvent() {
        return qvikieUpdatedEvent;
    }

    public boolean isNewQvikie() {
        return isNewQvikie;
    }
}
