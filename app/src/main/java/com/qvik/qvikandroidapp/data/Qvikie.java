package com.qvik.qvikandroidapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.base.Objects;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Model class for a Qvikie.
 */
@IgnoreExtraProperties
public class Qvikie extends RealmObject {

    @NonNull @PrimaryKey
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

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(name)) {
            return name;
        } else {
            return title;
        }
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    public boolean isEngineer() {
        return title != null && title.equals("engineer");
    }

    public boolean isDesigner() {
        return title != null && title.equals("designer");
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
