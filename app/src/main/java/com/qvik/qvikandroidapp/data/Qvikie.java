package com.qvik.qvikandroidapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.base.Objects;

import java.util.UUID;

/**
 * Immutable model class for a Qvikie.
 */
public final class Qvikie {

    @NonNull
    private final String mId;

    @Nullable
    private final String mName;

    @Nullable
    private final String mTitle;

    @Nullable
    private final String mDescription;

    @Nullable
    private final String mPhoneNumber;

    /**
     * Use this constructor to create a new Qvikie.
     *
     * @param name        title of the qvikie
     * @param title       title of the qvikie
     * @param description description of the qvikie
     * @param phoneNumber phone number of the qvikie
     */
    public Qvikie(@Nullable String name, @Nullable String title, @Nullable String description,
                  @Nullable String phoneNumber) {
        this(UUID.randomUUID().toString(), name, title, description, phoneNumber);
    }

    /**
     * Use this constructor to specify a Qvikie if the Qvikie already has an id.
     *
     * @param name        name of the qvikie
     * @param title       title of the qvikie
     * @param description description of the qvikie
     * @param id          id of the task
     */
    public Qvikie(@NonNull String id, @Nullable String name, @Nullable String title,
                  @Nullable String description, @Nullable String phoneNumber) {
        mId = id;
        mName = name;
        mTitle = title;
        mDescription = description;
        mPhoneNumber = phoneNumber;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public boolean isEngineer() {
        return mTitle != null && mTitle.equals("ENGINEER");
    }

    public boolean isDesigner() {
        return mTitle != null && mTitle.equals("DESIGNER");
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) && Strings.isNullOrEmpty(mTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Qvikie qvikie = (Qvikie) o;
        return Objects.equal(mId, qvikie.mId) &&
                Objects.equal(mName, qvikie.mName) &&
                Objects.equal(mTitle, qvikie.mTitle) &&
                Objects.equal(mDescription, qvikie.mDescription) &&
                Objects.equal(mPhoneNumber, qvikie.mPhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mName, mTitle, mDescription, mPhoneNumber);
    }

    @Override
    public String toString() {
        return "Qvikie " + mName + " (" + mTitle + ")";
    }
}
