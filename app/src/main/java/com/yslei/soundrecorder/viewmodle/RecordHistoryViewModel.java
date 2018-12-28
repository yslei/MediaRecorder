package com.yslei.soundrecorder.viewmodle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import com.yslei.soundrecorder.domain.RecordingHistoryBean;
import com.yslei.soundrecorder.domain.RecordingItem;
import com.yslei.soundrecorder.repository.RecordHistoryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午4:01
 * @readme to do
 */
public class RecordHistoryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<RecordingHistoryBean>> mRecordList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSwitchMode = new MutableLiveData<>();

    private ObservableBoolean mEditMode;
    private ObservableBoolean mIsEmpty;

    private RecordHistoryRepository mHistoryRepository;
    private List<RecordingHistoryBean> mWrapedDatas;

    private boolean mCurrentEditMode;

    public RecordHistoryViewModel(@NonNull Application application) {
        super(application);
        mHistoryRepository = RecordHistoryRepository.getInstance(application);
        mEditMode = new ObservableBoolean();
        mIsEmpty = new ObservableBoolean();
        mCurrentEditMode = false;
        mEditMode.set(mCurrentEditMode);
        mIsEmpty.set(false);
    }

    private void resetEmptyStatus(){
        if (mWrapedDatas == null || mWrapedDatas.size() == 0) {
            mIsEmpty.set(true);
        } else {
            mIsEmpty.set(false);
        }
    }

    public void loadRecordFiles() {
        List<RecordingItem> items = mHistoryRepository.loadRecordFiles();
        if (items != null && items.size() > 0) {
            mIsEmpty.set(false);
            mWrapedDatas = new ArrayList<>();
            for (RecordingItem item : items) {
                RecordingHistoryBean bean = new RecordingHistoryBean();
                bean.setEditMode(mCurrentEditMode);
                bean.setRecordingItem(item);
                mWrapedDatas.add(bean);
            }
        }
        resetEmptyStatus();
        mRecordList.setValue(mWrapedDatas);
    }

    public void selectItem(int position) {
        if (position >= 0 && mWrapedDatas != null && mWrapedDatas.size() > position) {
            mWrapedDatas.get(position).setSelect(!mWrapedDatas.get(position).isSelect());
            mRecordList.setValue(mWrapedDatas);
        }
    }

    public void deleteSelectedFiles() {
        if (mWrapedDatas != null && mWrapedDatas.size() > 0) {
            List<RecordingHistoryBean> deleteBeans = new ArrayList<>();
            List<RecordingItem> items = new ArrayList<>();
            for (RecordingHistoryBean bean : mWrapedDatas) {
                if (bean.isSelect() && bean.getRecordingItem() != null) {
                    deleteBeans.add(bean);
                    items.add(bean.getRecordingItem());
                }
            }
            if (deleteBeans.size() > 0) {
                for (RecordingHistoryBean bean : deleteBeans) {
                    mWrapedDatas.remove(bean);
                }
                mHistoryRepository.deleteRecordFiles(items);
                mRecordList.setValue(mWrapedDatas);
                resetEmptyStatus();
            }
        }
    }

    public void selectAllFiles() {
        if (mWrapedDatas != null && mWrapedDatas.size() > 0) {
            for (RecordingHistoryBean bean : mWrapedDatas) {
                bean.setSelect(!bean.isSelect());
            }
            mRecordList.setValue(mWrapedDatas);
        }
    }

    public void clearAllFiles() {
        if (mWrapedDatas != null && mWrapedDatas.size() > 0) {
            mWrapedDatas.clear();
            mHistoryRepository.clearRecordFiles();
            mRecordList.setValue(mWrapedDatas);
            resetEmptyStatus();
        }
    }

    public void switchEditMode() {
        if (mCurrentEditMode) {
            mSwitchMode.setValue(false);
        } else {
            mSwitchMode.setValue(true);
        }
        mCurrentEditMode = !mCurrentEditMode;
        if (mWrapedDatas != null && mWrapedDatas.size() > 0) {
            for (RecordingHistoryBean bean : mWrapedDatas) {
                bean.setEditMode(mCurrentEditMode);
            }
            mRecordList.setValue(mWrapedDatas);
        }
        mEditMode.set(mCurrentEditMode);
    }

    public ObservableBoolean getEditMode() {
        return mEditMode;
    }

    public ObservableBoolean getIsEmpty() {
        return mIsEmpty;
    }

    public MutableLiveData<List<RecordingHistoryBean>> getRecordList() {
        return mRecordList;
    }

    public MutableLiveData<Boolean> getSwitchMode() {
        return mSwitchMode;
    }
}
