package com.song.testwechatdemo.bases;

import android.content.Context;
import android.content.Loader;


/**
 * Created by Administrator on 2017/11/10.
 */


public class PresenterLoader<T extends BasePresenter> extends Loader<T> {

    private T presenter;
    private PresenterFactory<T> factory;

    public PresenterLoader(Context context, PresenterFactory factory) {
        super(context);
        this.factory = factory;
    }

    @Override
    protected void onStartLoading() {
        if (presenter != null) {
            deliverResult((T) presenter);
            return;
        }
        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        presenter = factory.create();
        deliverResult(presenter);
    }

    @Override
    protected void onReset() {
        presenter.onStop();
        presenter = null;
    }
}
