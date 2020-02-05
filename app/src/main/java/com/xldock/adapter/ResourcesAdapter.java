package com.xldock.adapter;

import android.content.Context;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.xldock.R;
import com.xldock.databinding.RowContactsBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Honey Shah on 18-11-2017.
 */

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {

    private List<List<Object>> mList = new ArrayList<>();
    private Context mContext;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onClick(String number);
    }

    public ResourcesAdapter(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RowContactsBinding mBinder = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.row_contacts, parent, false);

        return new ViewHolder(mBinder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final List<Object> list = mList.get(holder.getAdapterPosition());
        if (list != null) {
            if (list.size() > 0) {
                if (list.get(0) != null && !TextUtils.isEmpty((String)list.get(0))) {
                    holder.binder.tvName.setText((String) list.get(0));
                } else {
                    holder.binder.tvName.setText("No data");
                }
            } else {
                holder.binder.tvName.setText("No data");
            }
            if (list.size() > 1) {
                if (list.get(1) != null && !TextUtils.isEmpty((String)list.get(1))) {
                    holder.binder.tvNo.setText((String) list.get(1));
                } else {
                    holder.binder.tvNo.setText("No data");
                }
            }
            else {
                holder.binder.tvNo.setText("No data");
            }
        }

        holder.binder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick((String) list.get(1));
            }
        });
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

        RowContactsBinding binder;

        ViewHolder(final RowContactsBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }
    }
}
