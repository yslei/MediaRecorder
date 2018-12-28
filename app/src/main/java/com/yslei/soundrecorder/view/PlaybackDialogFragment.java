package com.yslei.soundrecorder.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import com.yslei.soundrecorder.R;
import com.yslei.soundrecorder.databinding.FragmentMediaPlaybackBinding;
import com.yslei.soundrecorder.domain.RecordingItem;
import com.yslei.soundrecorder.services.MediaPlayerClient;

import java.util.concurrent.TimeUnit;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午5:35
 * @readme to do
 */
public class PlaybackDialogFragment extends DialogFragment {

    private static final String ARG_ITEM = "recording_item";

    private FragmentMediaPlaybackBinding mBinding;
    private RecordingItem item;
    private Handler mHandler = new Handler();

    private ObservableField<String> mPlayedTime;
    private ObservableBoolean mPlayStatus;

    //stores whether or not the mediaplayer is currently playing audio
    private boolean isPlaying = false;

    //stores minutes and seconds of the length of the file.
    long minutes = 0;
    long seconds = 0;
    private static long mFileLength = 0;

    public static PlaybackDialogFragment newInstance(RecordingItem item) {
        PlaybackDialogFragment f = new PlaybackDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM, item);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(ARG_ITEM);

        long itemDuration = item.getLength();
        mFileLength = itemDuration;
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_playback, container, false);
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPlayedTime = new ObservableField<>();
        mPlayStatus = new ObservableBoolean();
        mPlayedTime.set(String.format("%02d:%02d", 0, 0));
        mPlayStatus.set(true);

        mBinding.setPlayTotalTime(String.format("%02d:%02d", minutes, seconds));
        mBinding.setPayStatus(mPlayStatus);
        mBinding.setFileName(item.getName());
        mBinding.setCurrentPayTime(mPlayedTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
        mBinding.seekbar.getProgressDrawable().setColorFilter(filter);
        mBinding.seekbar.getThumb().setColorFilter(filter);

        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MediaPlayerClient.get().getMediaPlayer() != null && fromUser) {
                    MediaPlayerClient.get().seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(MediaPlayerClient.get().getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(MediaPlayerClient.get().getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    mPlayedTime.set(String.format("%02d:%02d", minutes, seconds));

                    updateSeekBar();

                } else if (MediaPlayerClient.get().getMediaPlayer() == null && fromUser) {
                    prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (MediaPlayerClient.get().getMediaPlayer() != null) {
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MediaPlayerClient.get().getMediaPlayer() != null) {
                    mHandler.removeCallbacks(mRunnable);
                    MediaPlayerClient.get().seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(MediaPlayerClient.get().getMediaPlayer().getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(MediaPlayerClient.get().getMediaPlayer().getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    mPlayedTime.set(String.format("%02d:%02d", minutes, seconds));
                    updateSeekBar();
                }
            }
        });
        mBinding.fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        //set transparent background
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (MediaPlayerClient.get().getMediaPlayer() != null) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MediaPlayerClient.get().getMediaPlayer() != null) {
            stopPlaying();
        }
    }

    // Play start/stop
    private void onPlay(boolean isPlaying) {
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if (MediaPlayerClient.get().getMediaPlayer() == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            pausePlaying();
        }
    }

    private void startPlaying() {
        mPlayStatus.set(false);
        MediaPlayerClient.get().startPlaying(item.getFilePath(), new MediaPlayerClient.OnPlayerCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        mBinding.seekbar.setMax(MediaPlayerClient.get().getDuration());

        updateSeekBar();

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void prepareMediaPlayerFromPoint(int progress) {
        //set mediaPlayer to start from middle of the audio file
        MediaPlayerClient.get().startPlaying(item.getFilePath(), new MediaPlayerClient.OnPlayerCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        mBinding.seekbar.setMax(MediaPlayerClient.get().getDuration());

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void pausePlaying() {
        mPlayStatus.set(true);
        mHandler.removeCallbacks(mRunnable);
        MediaPlayerClient.get().pausePlaying();
    }

    private void resumePlaying() {
        mPlayStatus.set(false);
        mHandler.removeCallbacks(mRunnable);
        MediaPlayerClient.get().resumePlaying();
        updateSeekBar();
    }

    private void stopPlaying() {
        mPlayStatus.set(true);
        mHandler.removeCallbacks(mRunnable);
        MediaPlayerClient.get().stopPlaying();

        isPlaying = !isPlaying;

        mPlayedTime.set(String.format("%02d:%02d", minutes, seconds));
        mBinding.seekbar.setProgress(mBinding.seekbar.getMax());

        //allow the screen to turn off again once audio is finished playing
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //updating mSeekBar
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (MediaPlayerClient.get().getMediaPlayer() != null) {

                int mCurrentPosition = MediaPlayerClient.get().getCurrentPosition();
                mBinding.seekbar.setProgress(mCurrentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                mPlayedTime.set(String.format("%02d:%02d", minutes, seconds));

                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
}
