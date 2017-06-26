package com.qvik.qvikandroidapp.qvikies;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.databinding.QvikieItemBinding;

import java.util.List;

public class QvikiesAdapter extends BaseAdapter {

    private final QvikiesViewModel qvikiesViewModel;

    private List<Qvikie> qvikies;

    public QvikiesAdapter(List<Qvikie> qvikies, QvikiesViewModel qvikiesViewModel) {
        this.qvikiesViewModel = qvikiesViewModel;
        setList(qvikies);

    }

    public void replaceData(List<Qvikie> qvikies) {
        setList(qvikies);
    }

    @Override
    public int getCount() {
        return qvikies != null ? qvikies.size() : 0;
    }

    @Override
    public Qvikie getItem(int position) {
        return qvikies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View view, final ViewGroup viewGroup) {
        QvikieItemBinding binding;
        if (view == null) {
            // Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            // Create the binding
            binding = QvikieItemBinding.inflate(inflater, viewGroup, false);
        } else {
            // Recycling view
            binding = DataBindingUtil.getBinding(view);
        }

        QvikieItemUserActionsListener userActionsListener = new QvikieItemUserActionsListener() {

            @Override
            public void onQvikieClicked(Qvikie qvikie) {
                qvikiesViewModel.getOpenQvikieEvent().setValue(qvikie.getId());
            }
        };

        binding.setQvikie(qvikies.get(position));

        binding.setListener(userActionsListener);

        binding.executePendingBindings();
        return binding.getRoot();
    }


    private void setList(List<Qvikie> qvikies) {
        this.qvikies = qvikies;
        notifyDataSetChanged();
    }
}
