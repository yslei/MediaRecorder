package com.yslei.soundrecorder.activity;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.yslei.soundrecorder.domain.RecordingItem;
import com.yslei.soundrecorder.R;
import com.yslei.soundrecorder.utils.FileUtil;
import com.yslei.soundrecorder.view.PlaybackDialogFragment;
import com.yslei.soundrecorder.viewmodle.RecordViewModel;
import com.yslei.soundrecorder.viewmodle.ViewModelUtils;
import com.yslei.soundrecorder.databinding.ActivityRecordBinding;

import java.util.concurrent.TimeUnit;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午1:57
 * @readme to do
 */
public class RecordActivity extends MediaBaseActivity {

    private ActivityRecordBinding mBinding;
    private RecordViewModel mViewModel;
    private Handler mHandler;

    private boolean mIsRecording;
    private boolean mIsPlaying;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record);

        mBinding.recordAudioFabRecord.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        mBinding.recordAudioFabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPlaying) {
                    if (mIsRecording) {
                        stopRecord();
                    } else {
                        startRecord();
                    }
                }
            }
        });

        mBinding.recordAudioFabPlay.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        mBinding.recordAudioFabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsRecording) {
                    if (mIsPlaying) {
                        stopPlaying();
                    } else {
                        startPlaying();
                    }
                }
            }
        });
    }

    private void initData() {
        mViewModel = (RecordViewModel) ViewModelUtils.obtainViewModel(this, RecordViewModel.class);
        mHandler = new EventHandler();

        mViewModel.getLBCompleteCallback().observe(this, new Observer<MediaPlayer>() {
            @Override
            public void onChanged(@Nullable MediaPlayer mediaPlayer) {
                mHandler.removeMessages(EventHandler.TAG_MSG_UPDATE_PLAY_TIME);
                mIsPlaying = !mIsPlaying;
                mBinding.recordAudioFabRecord.setColorNormal(getResources().getColor(R.color.colorPrimary));
            }
        });

        mBinding.setRecorder(mViewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.resetShowPlayButton();
        mViewModel.resetFileInfoDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsPlaying) {
            stopPlaying();
        }
        if (mIsRecording) {
            stopRecord();
        }
    }

    private void showPlayedTime(int currentPlayPosition) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPlayPosition);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPlayPosition)
                - TimeUnit.MINUTES.toSeconds(minutes);
        mBinding.playAudioTime.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void stopRecord() {
        mViewModel.stopRecord();
        mHandler.removeMessages(EventHandler.TAG_MSG_UPDATE_VOICE_WAVE);

        mIsRecording = !mIsRecording;

        mBinding.recordAudioChronometerTime.stop();
        mBinding.recordAudioFabPlay.setColorNormal(getResources().getColor(R.color.colorPrimary));
    }

    private void startRecord() {
        mViewModel.startRecord();
        updatePlayVoice();

        mIsRecording = !mIsRecording;

        mBinding.recordAudioChronometerTime.setBase(SystemClock.elapsedRealtime());
        mBinding.recordAudioChronometerTime.start();
        mBinding.recordAudioFabPlay.setColorNormal(getResources().getColor(R.color.secondary_text));
    }

    private void startPlaying() {
        mViewModel.startToPlay();
        mHandler.removeMessages(EventHandler.TAG_MSG_UPDATE_PLAY_TIME);
        updatePlayTime();

        mIsPlaying = !mIsPlaying;

        mBinding.recordAudioChronometerTime.setBase(SystemClock.elapsedRealtime());
        showPlayedTime(0);
        mBinding.recordAudioFabRecord.setColorNormal(getResources().getColor(R.color.secondary_text));

        //keep screen on while playing audio
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void pausePlaying() {
        mViewModel.pausePlay();
        mHandler.removeMessages(EventHandler.TAG_MSG_UPDATE_PLAY_TIME);
        mBinding.recordAudioFabRecord.setColorNormal(getResources().getColor(R.color.colorPrimary));
    }

    private void stopPlaying() {
        mHandler.removeMessages(EventHandler.TAG_MSG_UPDATE_PLAY_TIME);
        mBinding.recordAudioFabRecord.setColorNormal(getResources().getColor(R.color.colorPrimary));
        mViewModel.stopPlay();

        mIsPlaying = !mIsPlaying;

        //allow the screen to turn off again once audio is finished playing
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recored, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.check_history:
                jumpToActivity(RecordHistoryActivity.class, false);
                break;
            default:
                break;
        }
        return true;
    }

    private void showPlayTime() {
        if (!mViewModel.needRestartPlay()) {
            int mCurrentPosition = mViewModel.getPlayCurrentPosition();
            showPlayedTime(mCurrentPosition);
            updatePlayTime();
        }
    }

    private void shwoVoiceWave() {

        double ratio = (double) mViewModel.getMaxAmplitude() / 100;
        double db = 0;// 分贝
        //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
        //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
        //同时，也可以配置灵敏度sensibility
        if (ratio > 1)
            db = 20 * Math.log10(ratio);
        //只要有一个线程，不断调用这个方法，就可以使波形变化
        //主要，这个方法必须在ui线程中调用
        mBinding.voicLine.setVolume((int) (db));
        updatePlayVoice();
    }

    private void updatePlayTime() {
        mHandler.sendEmptyMessageDelayed(EventHandler.TAG_MSG_UPDATE_PLAY_TIME, 500);
    }

    private void updatePlayVoice() {
        mHandler.sendEmptyMessageDelayed(EventHandler.TAG_MSG_UPDATE_VOICE_WAVE, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.releaseRecord();
        if (mIsPlaying) {
            stopPlaying();
        }
    }

    public class EventHandler extends Handler {
        public static final int TAG_MSG_UPDATE_PLAY_TIME = 20001;
        public static final int TAG_MSG_UPDATE_VOICE_WAVE = 20002;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TAG_MSG_UPDATE_PLAY_TIME:
                    showPlayTime();
                    break;
                case TAG_MSG_UPDATE_VOICE_WAVE:
                    shwoVoiceWave();
                    break;
            }
        }
    }
}
