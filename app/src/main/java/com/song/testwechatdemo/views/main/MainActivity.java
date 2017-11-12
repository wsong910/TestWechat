package com.song.testwechatdemo.views.main;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.song.testwechatdemo.App;
import com.song.testwechatdemo.R;
import com.song.testwechatdemo.bases.PresenterFactory;
import com.song.testwechatdemo.bases.PresenterLoader;
import com.song.testwechatdemo.frameworks.AppApi;
import com.song.testwechatdemo.model.bean.Message;
import com.song.testwechatdemo.model.bean.UserInfo;
import com.song.testwechatdemo.views.CustomToolBar;
import com.song.testwechatdemo.views.adapters.MyAdapter;
import com.song.testwechatdemo.views.widget.SwipPullRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/10.
 */
public class MainActivity extends AppCompatActivity implements MainContract.View
        , LoaderManager.LoaderCallbacks<MainContract.Presenter>, SwipPullRefreshRecyclerView.OnFreshListener
        , SwipPullRefreshRecyclerView.LoadMoreSubject {

    @BindView(R.id.title_bar)
    CustomToolBar titleBar;
    @BindView(R.id.wechats)
    RecyclerView wechats;
    @BindView(R.id.swipe_refresh_layout)
    SwipPullRefreshRecyclerView swipeRefreshLayout;
    private MainContract.Presenter mainPresenter;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(App.MAIN, null, this);
        ButterKnife.bind(this);
        wechats.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this);
        wechats.setItemAnimator(new DefaultItemAnimator());
        wechats.setHasFixedSize(true);
        wechats.setAdapter(myAdapter);
        swipeRefreshLayout.setOnFreshingListener(this);
        swipeRefreshLayout.setLoadMoreSubject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mainPresenter.getReceiver(), mainPresenter.getFilter());
        mainPresenter.onResume(myAdapter.isEmpty());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mainPresenter.getReceiver());
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mainPresenter = presenter;
        mainPresenter.attachView(this);
        mainPresenter.onCreate();
    }

    @Override
    public Loader<MainContract.Presenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(this, () -> new MainPresenter(MainActivity.this));
    }

    @Override
    public void onLoadFinished(Loader<MainContract.Presenter> loader, MainContract.Presenter data) {
        setPresenter(data);
    }

    @Override
    public void onLoaderReset(Loader<MainContract.Presenter> loader) {
        mainPresenter = null;
    }

    @Override
    public void onStartLoading() {
        if (!swipeRefreshLayout.isOnFreshing()) {
            swipeRefreshLayout.startFreshing();
        }
    }

    @Override
    public void onStopLoading() {
        if (swipeRefreshLayout.isOnFreshing()) {
            swipeRefreshLayout.stopFreshing();
        }
        if (swipeRefreshLayout.isLoading()) {
            swipeRefreshLayout.stopLoading();
        }
    }

    @Override
    public void onSuccess(Object data, String apiName) {
        if (apiName.equals(AppApi.USER_INFO)) {
            myAdapter.setUserInfo((UserInfo) data);
        } else if (apiName.equals(AppApi.MESSAGES)) {
            myAdapter.getDatas().clear();
            myAdapter.setDatas((List<Message>) data, true);
        }
    }

    @Override
    public void onError(String errMsg, int errKind, String apiName) {
        //TODO
    }

    @Override
    public void onFreshing() {
        //TODO
    }

    @Override
    public void onLoadMore() {
        //TODO
    }
}
