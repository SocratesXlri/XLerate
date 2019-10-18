package com.xldock.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xldock.R;
import com.xldock.databinding.RowContactsBinding;
import com.xldock.databinding.RowNotificationBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Honey Shah on 18-11-2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<List<Object>> mList = new ArrayList<>();
    private Context mContext;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RowNotificationBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.row_notification, parent, false);

        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final List<Object> list = mList.get(holder.getAdapterPosition());
        if (list != null) {
            holder.binder.tvCommitteeName.setText(mContext.getString(R.string.committee,
                    ( String) list.get(0)));
            holder.binder.tvDetails.setText((String) list.get(1));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addAll(List<List<Object>> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        mList.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        RowNotificationBinding binder;

        ViewHolder(final RowNotificationBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }
    }
}
