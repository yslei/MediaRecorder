package com.yslei.soundrecorder.viewmodle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yslei.soundrecorder.domain.RecordingItem;
import com.yslei.soundrecorder.server.IRecordCallback;
import com.yslei.soundrecorder.services.MediaPlayerClient;
import com.yslei.soundrecorder.services.RecorderClient;
import com.yslei.soundrecorder.utils.FileUtil;
import com.yslei.soundrecorder.utils.SingleLiveEvent;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午4:01
 * @readme to do
 */
public class RecordViewModel extends AndroidViewModel {

    private final SingleLiveEvent<Void> mLBPlayClick = new SingleLiveEvent<>();
    private final MutableLiveData<MediaPlayer> mLBCompleteCallback = new MutableLiveData<>();

    private ObservableBoolean mIsOBRecordOrNot;
    private ObservableBoolean mIsOBShowPlayButton;
    private ObservableBoolean mIsOBPlaying;
    private ObservableBoolean mIsOBRecording;
    private ObservableField<String> mPlayLength;
    private ObservableField<String> mFileName;
    private ObservableField<String> mFilePath;

    private RecorderClient mRecorderClient;
    private MediaPlayerClient mPlayerClient;


    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRecorderClient = new RecorderClient.Builder()
                .build();
        mPlayerClient = MediaPlayerClient.get();

        mIsOBRecordOrNot = new ObservableBoolean();
        mIsOBShowPlayButton = new ObservableBoolean();
        mIsOBPlaying = new ObservableBoolean();
        mIsOBRecording = new ObservableBoolean();
        mPlayLength = new ObservableField<>();
        mFileName = new ObservableField<>();
        mFilePath = new ObservableField<>();
        mIsOBRecordOrNot.set(true);
        mIsOBShowPlayButton.set(false);
        mIsOBPlaying.set(false);
        mIsOBRecording.set(false);
        mFilePath.set("--");
        mFileName.set("--");
    }

    public void resetShowPlayButton() {
        if (canPlay()) {
            mIsOBShowPlayButton.set(true);
        } else {
            mIsOBShowPlayButton.set(false);
        }
    }

    public void resetFileInfoDisplay(){
        if (canPlay()) {
            mFilePath.set(mRecorderClient.getFinalFilePath());
            mFileName.set(mRecorderClient.getFinalFileName());
            mIsOBShowPlayButton.set(true);
        } else {
            mFilePath.set("--");
            mFileName.set("--");
        }
    }

    public void startRecord() {
        mRecorderClient.startRecord();
        mIsOBRecording.set(true);
    }

    public void stopRecord() {
        mRecorderClient.stopRecord();
        resetFileInfoDisplay();
        resetShowPlayButton();
        mIsOBRecording.set(false);
    }

    public void startToPlay() {
        if (canPlay()) {
            if (needRestartPlay()) {
                setPlayLength();
                mPlayerClient.startPlaying(mRecorderClient.getFinalFilePath(), new MediaPlayerClient.OnPlayerCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mLBCompleteCallback.setValue(mp);
                        stopPlay();
                    }
                });
            } else {
                mPlayerClient.resumePlaying();
            }
            mIsOBPlaying.set(true);
        }
    }

    public void seekToPlay(int progress) {
        mPlayerClient.seekTo(progress);
    }

    public void pausePlay() {
        mPlayerClient.pausePlaying();
        mIsOBPlaying.set(false);
    }

    public void stopPlay() {
        mPlayerClient.stopPlaying();
        mIsOBPlaying.set(false);
    }

    public boolean canPlay() {
        if (!TextUtils.isEmpty(mRecorderClient.getFinalFilePath())
                && FileUtil.isFileExist(mRecorderClient.getFinalFilePath())) {
            return true;
        }
        return false;
    }

    public int getPlayCurrentPosition() {
        if (!needRestartPlay()) {
            return mPlayerClient.getMediaPlayer().getCurrentPosition();
        }
        return -1;
    }

    public int getPlayCurrentDuration() {
        if (!needRestartPlay()) {
            return mPlayerClient.getMediaPlayer().getDuration();
        }
        return -1;
    }

    public int getMaxAmplitude(){
        return mRecorderClient.getMaxAmplitude();
    }

    public void setPlayLength() {
        long itemDuration = RecordingItem.getRecordLengthByFilePath(mRecorderClient.getFinalFilePath());
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes));
        mPlayLength.set(String.format("%02d:%02d", minutes, seconds));
    }

    public boolean needRestartPlay() {
        return mPlayerClient.getMediaPlayer() == null;
    }

    public void releaseRecord() {
        mRecorderClient.releaseRecord();
    }

    public MutableLiveData<MediaPlayer> getLBCompleteCallback() {
        return mLBCompleteCallback;
    }

    public ObservableField<String> getPlayLength() {
        return mPlayLength;
    }

    public ObservableBoolean getIsOBRecordOrNot() {
        return mIsOBRecordOrNot;
    }

    public ObservableBoolean getIsOBShowPlayButton() {
        return mIsOBShowPlayButton;
    }

    public ObservableBoolean getIsOBPlaying() {
        return mIsOBPlaying;
    }

    public ObservableBoolean getIsOBRecording() {
        return mIsOBRecording;
    }

    public ObservableField<String> getFileName() {
        return mFileName;
    }

    public ObservableField<String> getFilePath() {
        return mFilePath;
    }
}
