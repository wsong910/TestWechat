package com.song.testwechatdemo.views.main;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.song.testwechatdemo.bases.BasePresenter;
import com.song.testwechatdemo.bases.BaseView;

/**
 * Created by Administrator on 2017/11/10.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void onStartLoading();

        void onStopLoading();

        /**
         * 接口访问成功，逻辑正确时执行此方法
         */
        void onSuccess(Object data, String apiName);

        /**
         * 除业务逻辑错误的任何错误都会执行此方法
         */
        void onError(String errMsg, int errKind, String apiName);
    }

    interface Presenter extends BasePresenter<View> {

        void onCreate();

        void onResume(boolean empty);

        BroadcastReceiver getReceiver();

        IntentFilter getFilter();
    }
}
