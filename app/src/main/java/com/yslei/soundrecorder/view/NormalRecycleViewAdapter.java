package com.yslei.soundrecorder.view;

import android.content.Context;
import android.databinding.ViewDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/7/17 下午2:48
 * @readme to do
 */
public abstract class NormalRecycleViewAdapter<T, R extends ViewDataBinding> extends BaseRecycleViewAdapter<R> {

    private List<T> mData;

    public NormalRecycleViewAdapter(Context context) {
        this(context, null);
    }

    public NormalRecycleViewAdapter(Context context, List<T> data) {
        super(context);
        mData = data;
    }

    public void addDatas(List<T> data) {
        if (data != null && data.size() > 0) {
            if (mData == null) {
                mData = data;
                notifyDataSetChanged();
            } else {
                int positionStart = getDataCount() - 1;
                mData.addAll(data);
                notifyItemRangeInserted(positionStart, data.size());
            }
        }
    }

    public void setDatas(List<T> data) {
        if (data != null && data.size() > 0) {
            mData = data;
        } else {
            mData = null;
        }
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if (data == null) return;
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public int getDataCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public T getDataAtIndex(int index) {
        if (mData != null && mData.size() > index) {
            return mData.get(index);
        }
        return null;
    }

    @Override
    public int setItemCount() {
        return getDataCount();
    }
}
