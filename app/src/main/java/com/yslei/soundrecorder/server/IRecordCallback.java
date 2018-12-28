package com.yslei.soundrecorder.server;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午3:38
 * @readme to do
 */
public interface IRecordCallback {

    void startRecord();

    void onProgress(int progress);

    void stopRecord();

    void releaseRecord();

    void playRecord();
}
