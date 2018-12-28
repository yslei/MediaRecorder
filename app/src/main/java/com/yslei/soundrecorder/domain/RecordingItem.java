package com.yslei.soundrecorder.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.yslei.soundrecorder.services.RecorderClient;

import java.io.File;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/26 下午2:00
 * @readme to do
 */
public class RecordingItem implements Parcelable {
    private String mName;
    private String mFilePath;
    private int mId;
    private int mLength;
    private long mTime;

    public static long getRecordLengthByFilePath(String filePath) {
        int length = 0;
        if (!TextUtils.isEmpty(filePath)) {
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf('.'));
            String[] strArr = fileName.split(RecorderClient.FILE_NAME_SEPARATE + "");
            if (strArr != null && strArr.length == 3 && !TextUtils.isEmpty(strArr[2])) {
                try {
                    length = Integer.parseInt(strArr[2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return length;
    }

    public RecordingItem() {
    }

    public RecordingItem(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        mId = in.readInt();
        mLength = in.readInt();
        mTime = in.readLong();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getLength() {
        if (mLength <= 0) {
            mLength = (int)getRecordLengthByFilePath(mFilePath);
        }
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        if (!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mName)) {
            mName = mFilePath.substring(mFilePath.lastIndexOf(File.separator) + 1);
        }
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public static final Parcelable.Creator<RecordingItem> CREATOR = new Parcelable.Creator<RecordingItem>() {
        public RecordingItem createFromParcel(Parcel in) {
            return new RecordingItem(in);
        }

        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeLong(mTime);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
