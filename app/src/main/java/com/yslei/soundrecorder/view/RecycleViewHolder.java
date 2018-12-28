package com.yslei.soundrecorder.view;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * create by author：yslei
 * create time : 2017/4/12 14:38
 * author email : leiyongsheng_cd@keruyun.com
 */
public class RecycleViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public RecycleViewHolder(View itemView) {
        super(itemView);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public void setBinding(ViewDataBinding binding) {
        this.binding = binding;
    }
}
