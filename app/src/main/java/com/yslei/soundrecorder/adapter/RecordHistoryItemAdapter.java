package com.yslei.soundrecorder.adapter;

import android.content.Context;

import com.yslei.soundrecorder.R;
import com.yslei.soundrecorder.databinding.ListRecordHistoryItemBinding;
import com.yslei.soundrecorder.domain.RecordingHistoryBean;
import com.yslei.soundrecorder.view.NormalRecycleViewAdapter;

import java.util.List;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午8:25
 * @readme to do
 */
public class RecordHistoryItemAdapter extends NormalRecycleViewAdapter<RecordingHistoryBean, ListRecordHistoryItemBinding> {

    public RecordHistoryItemAdapter(Context context, List<RecordingHistoryBean> data) {
        super(context);
    }

    @Override
    public int setViewId() {
        return R.layout.list_record_history_item;
    }

    @Override
    public void bindViewHolderData(ListRecordHistoryItemBinding binding, int position) {
        if (getDataAtIndex(position) != null) {
            binding.setItem(getDataAtIndex(position));
        }
    }
}
