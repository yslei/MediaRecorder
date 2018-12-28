package com.yslei.soundrecorder.services;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.yslei.soundrecorder.utils.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午2:00
 * @readme to do
 */
public class RecorderClient {

    private final String TAG = "RecorderClient";
    public static final char FILE_NAME_SEPARATE = '_';
    public static final String FILE_PREFIX_TAG = "YSLEI";
    public static final String FILE_SUFFIX_TAG = ".MP4";

    private MediaRecorder mRecorder;
    private long mStartingTimeMillis;
    private long mElapsedMillis;
    private Builder mBuilder;
    private String mFilePath;

    public RecorderClient() {
        this(new Builder());
    }

    public RecorderClient(Builder builder) {
        mRecorder = new MediaRecorder();
        mBuilder = builder;
    }

    private void initRecord(String fileName) {
        mRecorder.setAudioSource(mBuilder.getSourceType());
        mRecorder.setOutputFormat(mBuilder.getFormat());
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(mBuilder.getEncoder());
        mRecorder.setAudioChannels(mBuilder.getChannels());
        mRecorder.setAudioSamplingRate(mBuilder.getSamplingRate());
        mRecorder.setAudioEncodingBitRate(mBuilder.getEncodingBitRate());
        mFilePath = fileName;
    }

    private String crateRandomFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(FILE_PREFIX_TAG);
        builder.append(FILE_NAME_SEPARATE);
        builder.append(System.currentTimeMillis());
        builder.append(FILE_SUFFIX_TAG);
        return builder.toString();
    }

    private void startRecord(String fileName) {
        if (mRecorder != null) {
            try {
                mRecorder.reset();
                initRecord(fileName);
                mRecorder.prepare();
                mRecorder.start();
                mStartingTimeMillis = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRecord() {
        String path = mBuilder.getOutPutPath();
        if (mBuilder.isFileNameRandom() && FileUtil.isFileDirExist(new File(path))) {
            path = path + File.separator + crateRandomFileName();
            startRecord(path);
        } else {
            startRecord(path);
        }
    }

    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            reNameRecord(mElapsedMillis);
        }
    }

    private void reNameRecord(long elapsed) {
        Log.d(TAG, "rename file before " + mFilePath);
        if (!TextUtils.isEmpty(mFilePath)) {
            File file = new File(mFilePath);
            if (file != null && file.exists() && !file.isDirectory()) {
                String path = mFilePath.substring(0, mFilePath.lastIndexOf('.'));
                mFilePath = path + FILE_NAME_SEPARATE + elapsed + FILE_SUFFIX_TAG;
                Log.d(TAG, "rename to " + mFilePath);
                file.renameTo(new File(mFilePath));
            }
        }
    }

    public void releaseRecord() {
        if (mRecorder != null) {
            mRecorder.release();
        }
    }

    public int getMaxAmplitude() {
        if (mRecorder != null) {
            return mRecorder.getMaxAmplitude();
        }
        return -1;
    }

    public long getElapsedMillis() {
        return mElapsedMillis;
    }

    public String getFinalFilePath() {
        return mFilePath;
    }

    public String getFinalFileName() {
        if (TextUtils.isEmpty(mFilePath)) {
            return "";
        } else return mFilePath.substring(mFilePath.lastIndexOf(File.separator) + 1,mFilePath.lastIndexOf("."));
    }

    public static final class Builder {
        private int mSourceType;
        private int mFormat;
        private String mOutPutPath;
        private int mEncoder;
        private int mChannels;
        private int mSamplingRate;
        private int mEncodingBitRate;
        // random to create file name base on time
        private boolean mFileNameRandom;

        public Builder() {
            mSourceType = MediaRecorder.AudioSource.MIC;
            mFormat = MediaRecorder.OutputFormat.MPEG_4;
            mOutPutPath = FileUtil.getRecordCacheDirPath(null);
            mEncoder = MediaRecorder.AudioEncoder.AAC;
            mChannels = AudioFormat.CHANNEL_IN_DEFAULT;
            mSamplingRate = 44100;
            mEncodingBitRate = 192000;
            mFileNameRandom = true;
        }

        public RecorderClient build() {
            return new RecorderClient(this);
        }

        public Builder setFileNameRandom(boolean fileNameRandom) {
            mFileNameRandom = fileNameRandom;
            return this;
        }

        public boolean isFileNameRandom() {
            return mFileNameRandom;
        }

        public int getSourceType() {
            return mSourceType;
        }

        public Builder setSourceType(int sourceType) {
            mSourceType = sourceType;
            return this;
        }

        public int getFormat() {
            return mFormat;
        }

        public Builder setFormat(int format) {
            mFormat = format;
            return this;
        }

        public String getOutPutPath() {
            return mOutPutPath;
        }

        // not allow access currently
        private Builder setOutPutPath(String outPutPath) {
            mOutPutPath = outPutPath;
            return this;
        }

        public int getEncoder() {
            return mEncoder;
        }

        public Builder setEncoder(int encoder) {
            mEncoder = encoder;
            return this;
        }

        public int getChannels() {
            return mChannels;
        }

        public Builder setChannels(int channels) {
            mChannels = channels;
            return this;
        }

        public int getSamplingRate() {
            return mSamplingRate;
        }

        public Builder setSamplingRate(int samplingRate) {
            mSamplingRate = samplingRate;
            return this;
        }

        public int getEncodingBitRate() {
            return mEncodingBitRate;
        }

        public Builder setEncodingBitRate(int encodingBitRate) {
            mEncodingBitRate = encodingBitRate;
            return this;
        }
    }
}
