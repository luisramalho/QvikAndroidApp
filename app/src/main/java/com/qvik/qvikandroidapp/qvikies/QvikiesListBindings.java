package com.qvik.qvikandroidapp.qvikies;

import android.databinding.BindingAdapter;
import android.widget.ListView;

import com.qvik.qvikandroidapp.data.Qvikie;

import java.util.List;

/**
 *  Contains {@link BindingAdapter}s for the {@link Qvikie} list.
 */
public class QvikiesListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<Qvikie> qvikies) {
        QvikiesAdapter adapter = (QvikiesAdapter) listView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(qvikies);
        }
    }
}
