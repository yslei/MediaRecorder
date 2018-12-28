package com.yslei.soundrecorder.viewmodle;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

/**
 * @author by leiyongsheng_cd@keruyun.com
 * @day 2018/9/6 上午10:52
 * @readme to do
 */
public class ViewModelUtils {

    public static AndroidViewModel obtainViewModel(FragmentActivity activity, Class<? extends AndroidViewModel> viewModelClass) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AndroidViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(viewModelClass);

        return viewModel;
    }


}
