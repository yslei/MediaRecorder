package com.yslei.soundrecorder.domain;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午2:00
 * @readme to do
 */
public class RecordingHistoryBean {
    private boolean isEditMode;
    private boolean isSelect;
    private RecordingItem recordingItem;

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public RecordingItem getRecordingItem() {
        return recordingItem;
    }

    public void setRecordingItem(RecordingItem recordingItem) {
        this.recordingItem = recordingItem;
    }
}
