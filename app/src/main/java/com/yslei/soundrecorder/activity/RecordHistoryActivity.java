package com.yslei.soundrecorder.activity;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yslei.soundrecorder.R;
import com.yslei.soundrecorder.adapter.RecordHistoryItemAdapter;
import com.yslei.soundrecorder.databinding.ActivityRecordHistoryBinding;
import com.yslei.soundrecorder.domain.RecordingHistoryBean;
import com.yslei.soundrecorder.view.BaseRecycleViewAdapter;
import com.yslei.soundrecorder.view.PlaybackDialogFragment;
import com.yslei.soundrecorder.viewmodle.RecordHistoryViewModel;
import com.yslei.soundrecorder.viewmodle.ViewModelUtils;

import java.util.List;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午1:57
 * @readme to do
 */
public class RecordHistoryActivity extends MediaBaseActivity {

    private ActivityRecordHistoryBinding mBinding;
    private RecordHistoryViewModel mViewModel;
    private RecordHistoryItemAdapter mRecordHistoryItemAdapter;
    private boolean mIsEditMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record_history);
        mRecordHistoryItemAdapter = new RecordHistoryItemAdapter(this, null);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.setAdapter(mRecordHistoryItemAdapter);

        mRecordHistoryItemAdapter.setRecycleViewItemClickListener(new BaseRecycleViewAdapter.RecycleViewItemClickListener() {
            @Override
            public void onRecycleViewItemClick(int position) {
                if (mIsEditMode) {
                    mViewModel.selectItem(position);
                } else {
                    recordPlay(mRecordHistoryItemAdapter.getDataAtIndex(position));
                }
            }
        });
    }

    private void initData() {
        mViewModel = (RecordHistoryViewModel) ViewModelUtils.obtainViewModel(this, RecordHistoryViewModel.class);

        mViewModel.getRecordList().observe(this, new Observer<List<RecordingHistoryBean>>() {
            @Override
            public void onChanged(@Nullable List<RecordingHistoryBean> recordingItems) {
                mRecordHistoryItemAdapter.setDatas(recordingItems);
            }
        });
        mViewModel.getSwitchMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mIsEditMode = aBoolean;
            }
        });


        mBinding.setViewModel(mViewModel);
        mViewModel.loadRecordFiles();
    }

    private void recordPlay(RecordingHistoryBean bean) {
        if (bean != null && bean.getRecordingItem() != null) {
            PlaybackDialogFragment fragmentPlay = PlaybackDialogFragment.newInstance(bean.getRecordingItem());
            fragmentPlay.show(getSupportFragmentManager(), PlaybackDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recored_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.mode_switch:
                mViewModel.switchEditMode();
                if (mIsEditMode) {
                    item.setTitle(getString(R.string.str_display_mode));
                } else {
                    item.setTitle(getString(R.string.str_edit_mode));
                }
                break;
            default:
                break;
        }
        return true;
    }
}
