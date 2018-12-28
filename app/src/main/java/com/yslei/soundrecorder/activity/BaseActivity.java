package com.yslei.soundrecorder.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yslei.soundrecorder.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/6/25
 * @readme to do
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final int DEBOUNCE_TIME = 100;
    protected String TAG = getClass().getSimpleName();
    private ProgressDialog mProgressDialog;
    private long mLastTime = 0;

    public Context getBaseContext() {
        return this.getApplicationContext();
    }

    public void showToast(int id) {
        if (id > 0) {
            showToast(getString(id));
        }
    }

    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    public void showProgressDialog() {
        showProgressDialog(R.string.str_loading, true);
    }

    public void showProgressDialog(int resId) {
        showProgressDialog(resId, true);
    }

    public void showProgressDialog(String msg) {
        showProgressDialog(msg, true);
    }

    public void showUpdateProgressDialog(String msg) {
        showUpdateProgressDialog(msg, true);
    }

    public void showProgressDialog(String msg, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(cancelable);
            if (!TextUtils.isEmpty(msg)) {
                mProgressDialog.setMessage(msg);
            }
            mProgressDialog.show();
        } else if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void showProgressDialog(int resId, boolean cancelable) {
        showProgressDialog(getString(resId), cancelable);
    }

    public void showUpdateProgressDialog(String msg, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show();
        }
        mProgressDialog.setMessage(msg);
    }

    public void dismissDialog() {
        if (null == mProgressDialog || isFinishing()) {
            return;
        }
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 不带参数的跳转
     */
    public void jumpToActivity(Class<? extends Activity> clazz, boolean finish) {
        jumpToActivity(null, clazz, finish);
    }

    /**
     * 跳转Activity
     *
     * @param intent 意图
     * @param clazz  跳转Class
     * @param finish 是否结束
     */
    public void jumpToActivity(Intent intent,
                               Class<? extends Activity> clazz,
                               boolean finish) {
        if (System.currentTimeMillis() - mLastTime > DEBOUNCE_TIME) {
            mLastTime = System.currentTimeMillis();
            if (intent == null)
                intent = new Intent(this, clazz);
            else
                intent.setClass(this, clazz);
            startActivity(intent);
            if (finish)
                this.finish();
        }
    }

    public void jumpToActivityForResult(Class<? extends Activity> clazz,
                                        int reqCode) {
        jumpToActivityForResult(null, clazz, reqCode);
    }

    public void jumpToActivityForResult(Intent intent,
                                        Class<? extends Activity> clazz,
                                        int reqCode) {
        if (System.currentTimeMillis() - mLastTime > DEBOUNCE_TIME) {
            mLastTime = System.currentTimeMillis();
            if (intent == null)
                intent = new Intent(this, clazz);
            else
                intent.setClass(this, clazz);
            startActivityForResult(intent, reqCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    protected boolean hasPermission(String... perms) {
        return EasyPermissions.hasPermissions(this, perms);
    }

    protected void grantedPermissionFromSetting() {
    }

    protected void requestPermissions(@StringRes int stringId, int requestId, String... perms) {
        EasyPermissions.requestPermissions(this, getString(stringId), requestId, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size() + "/perms:" + perms);
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}
