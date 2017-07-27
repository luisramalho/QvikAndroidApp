package com.qvik.qvikandroidapp.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {

    private ActivityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     *
     */
    public static void replaceFragmentInActivity(@NonNull FragmentManager fragmentManager,
                                                 @NonNull Fragment fragment, int frameId) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(fragmentManager, "fragmentManager == null");
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(fragment, "fragment == null");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     *
     */
    public static void replaceFragmentInActivity(@NonNull FragmentManager fragmentManager,
                                                 @NonNull Fragment fragment, String tag) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(fragmentManager, "fragmentManager == null");
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(fragment, "fragment == null");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(fragment, tag);
        transaction.commit();
    }
}
