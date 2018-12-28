package com.yslei.soundrecorder.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yslei.soundrecorder.R;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/12/27 下午8:45
 * @readme to do
 */
public class MediaBaseActivity extends BaseActivity {

    private static final int RC_MEDIA_RECORD_DELETE_PERMISSION = 00002;

    private static final String[] MEDIA_RECORD_DELETE_PERMISSION = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestVoiceDetectPerms();
    }

    @AfterPermissionGranted(RC_MEDIA_RECORD_DELETE_PERMISSION)
    public void requestVoiceDetectPerms() {
        if (hasMediaRecordDeletePermissions()) {

        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_for_media_record_delete),
                    RC_MEDIA_RECORD_DELETE_PERMISSION,
                    MEDIA_RECORD_DELETE_PERMISSION);
        }
    }

    protected boolean hasMediaRecordDeletePermissions() {
        return EasyPermissions.hasPermissions(this, MEDIA_RECORD_DELETE_PERMISSION);
    }
}
