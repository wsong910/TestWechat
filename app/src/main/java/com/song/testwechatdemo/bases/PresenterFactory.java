package com.song.testwechatdemo.bases;


/**
 * Created by Administrator on 2017/11/10.
 */


public interface PresenterFactory<T extends BasePresenter> {
    T create();
}
