package com.yslei.soundrecorder.repository;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yslei.soundrecorder.domain.RecordingItem;
import com.yslei.soundrecorder.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/27 下午12:54
 * @readme to do
 */
public class RecordHistoryRepository {

    private volatile static RecordHistoryRepository INSTANCE;

    private Application mContext;
    private List<RecordingItem> mRecordingItems;

    public static RecordHistoryRepository getInstance(@NonNull Application context) {
        if (INSTANCE == null) {
            synchronized (RecordHistoryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecordHistoryRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public RecordHistoryRepository(Application context) {
        this.mContext = context;
    }

    public List<RecordingItem> loadRecordFiles() {
        return loadLocalRecordFiles();
    }

    private List<RecordingItem> loadLocalRecordFiles() {
        String fileDir = FileUtil.getRecordCacheDirPath(mContext);
        String[] filePaths = FileUtil.getRecordFiles(fileDir);
        if (filePaths != null && filePaths.length > 0) {
            mRecordingItems = new ArrayList<>();
            for (int i = 0; i < filePaths.length; i++) {
                RecordingItem item = new RecordingItem();
                String fileName = filePaths[i].substring(0, filePaths[i].lastIndexOf('.'));
                item.setName(fileName);
                item.setFilePath(fileDir + File.separator + filePaths[i]);
                item.setLength(item.getLength());
                mRecordingItems.add(item);
            }
            return mRecordingItems;
        }
        return null;
    }

    public void deleteRecordFiles(List<RecordingItem> items) {
        if (mRecordingItems != null && mRecordingItems.size() > 0 && items != null && items.size() > 0) {
            List<String> deleteFiles = new ArrayList<>();
            List<RecordingItem> deleteBeans = new ArrayList<>();
            for (RecordingItem recordingItem : mRecordingItems) {
                for (RecordingItem bean : items) {
                    if (!TextUtils.isEmpty(bean.getName()) && recordingItem.getName().equals(bean.getName())) {
                        deleteBeans.add(bean);
                        deleteFiles.add(bean.getFilePath());
                    }
                }
            }
            if (deleteFiles.size() > 0) {
                for (RecordingItem bean : deleteBeans) {
                    mRecordingItems.remove(bean);
                }
                FileUtil.deleteFiles(deleteFiles);
            }
        }
    }

    public void clearRecordFiles() {
        if (mRecordingItems != null && mRecordingItems.size() > 0) {
            List<String> deleteFiles = new ArrayList<>();
            for (RecordingItem bean : mRecordingItems) {
                deleteFiles.add(bean.getFilePath());
            }
            if (deleteFiles.size() > 0) {
                mRecordingItems.clear();
                FileUtil.deleteFiles(deleteFiles);
            }
        }
    }
}
