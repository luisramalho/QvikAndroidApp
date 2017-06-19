package com.qvik.qvikandroidapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.base.Objects;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

/**
 * Immutable model class for a Qvikie.
 */
@IgnoreExtraProperties
public final class Qvikie {

    @NonNull
    private String id;

    @Nullable
    private String name;

    @Nullable
    private String title;

    @Nullable
    private String description;

    @Nullable
    private String phoneNumber;

    @Nullable
    private String email;

    public Qvikie() {
        // Firebase requires this empty constructor
    }

    /**
     * Use this constructor to create a new Qvikie.
     *
     * @param name        title of the qvikie
     * @param title       title of the qvikie
     * @param description description of the qvikie
     * @param phoneNumber phone number of the qvikie
     * @param email       email of the qvikie
     */
    public Qvikie(@Nullable String name, @Nullable String title, @Nullable String description,
                  @Nullable String phoneNumber, @Nullable String email) {
        this(UUID.randomUUID().toString(), name, title, description, phoneNumber, email);
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
                  @Nullable String description, @Nullable String phoneNumber,
                  @Nullable String email) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(title)) {
            return title;
        } else {
            return description;
        }
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public boolean isEngineer() {
        return title != null && title.equals("ENGINEER");
    }

    public boolean isDesigner() {
        return title != null && title.equals("DESIGNER");
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(name) && Strings.isNullOrEmpty(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Qvikie qvikie = (Qvikie) o;
        return Objects.equal(id, qvikie.id) &&
                Objects.equal(name, qvikie.name) &&
                Objects.equal(title, qvikie.title) &&
                Objects.equal(description, qvikie.description) &&
                Objects.equal(phoneNumber, qvikie.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, title, description, phoneNumber);
    }

    @Override
    public String toString() {
        return "Qvikie " + name + " (" + title + ")";
    }
}
