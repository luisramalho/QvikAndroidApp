package com.qvik.qvikandroidapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Model class for a Notification.
 */
@IgnoreExtraProperties
public class Notification extends RealmObject {

    @NonNull
    @PrimaryKey
    private String id;

    @Nullable
    private String title;

    @Nullable
    private String description;

    public Notification() {
        // Firebase requires this empty constructor
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable @Exclude
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(title)) {
            return title;
        } else {
            return "Notification #" + id;
        }
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
