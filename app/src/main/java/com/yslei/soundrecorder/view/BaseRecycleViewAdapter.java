package com.yslei.soundrecorder.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * create by author：yslei
 * create time : 2017/4/12 14:22
 * author email : leiyongsheng_cd@keruyun.com
 */
public abstract class BaseRecycleViewAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<RecycleViewHolder> {

    private RecycleViewItemClickListener mRecycleViewItemClickListener;
    private RecycleViewItemLongClickListener mRecycleViewItemLongClickListener;
    private Context mContext;

    public BaseRecycleViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        T binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                setViewId(),
                parent,
                false);
        RecycleViewHolder holder = new RecycleViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    public abstract int setViewId();

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecycleViewHolder holder, final int position) {
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecycleViewItemClickListener != null) {
                    if (isInfinite()) {
                        mRecycleViewItemClickListener.onRecycleViewItemClick(position % getDataCount());
                    } else {
                        mRecycleViewItemClickListener.onRecycleViewItemClick(position);
                    }
                }
            }
        });
        holder.getBinding().getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mRecycleViewItemLongClickListener != null) {
                    if (isInfinite()) {
                        mRecycleViewItemLongClickListener.onRecycleViewLongItemClick(position % getDataCount());
                    } else {
                        mRecycleViewItemLongClickListener.onRecycleViewLongItemClick(position);
                    }
                }
                return false;
            }
        });
        if (isInfinite()) {
            bindViewHolderData((T) holder.getBinding(), position % getDataCount());
        } else {
            bindViewHolderData((T) holder.getBinding(), position);
        }
    }

    public abstract void bindViewHolderData(T binding, int position);

    public boolean isInfinite() {
        return false;
    }

    public int getDataCount() {
        return setItemCount();
    }

    @Override
    public int getItemCount() {
        return setItemCount();
    }

    public abstract int setItemCount();

    public Context getContext() {
        return mContext;
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener recycleViewItemClickListener) {
        mRecycleViewItemClickListener = recycleViewItemClickListener;
    }

    public void setRecycleViewItemLongClickListener(RecycleViewItemLongClickListener recycleViewItemLongClickListener) {
        mRecycleViewItemLongClickListener = recycleViewItemLongClickListener;
    }

    public interface RecycleViewItemClickListener {
        void onRecycleViewItemClick(int position);
    }

    public interface RecycleViewItemLongClickListener {
        void onRecycleViewLongItemClick(int position);
    }
}
