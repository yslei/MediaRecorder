package com.yslei.soundrecorder.services;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午5:35
 * @readme to do
 */
public class MediaPlayerClient {

    private final String TAG = "MediaPlayerClient";
    private MediaPlayer mMediaPlayer = null;

    private static class SingletonHolder {
        private static final MediaPlayerClient INSTANCE = new MediaPlayerClient();
    }

    public static MediaPlayerClient get() {
        return SingletonHolder.INSTANCE;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public int getDuration(){
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public int getCurrentPosition(){
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public void startPlaying(String recordPath, final OnPlayerCompletionListener completionListener) {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(recordPath);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (completionListener != null) {
                    completionListener.onCompletion(mp);
                }
                stopPlaying();
            }
        });
    }

    public void prepareMediaPlayerFromPoint(String recordPath, int progress) {
        //set mediaPlayer to start from middle of the audio file
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(recordPath);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void seekTo(int progress){
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    public void pausePlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void resumePlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void stopPlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public interface OnPlayerCompletionListener{
        void onCompletion(MediaPlayer mp);
    }
}
