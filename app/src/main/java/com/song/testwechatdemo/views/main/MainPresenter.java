package com.song.testwechatdemo.views.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.song.testwechatdemo.App;
import com.song.testwechatdemo.frameworks.AppApi;
import com.song.testwechatdemo.frameworks.DataRepository;
import com.song.testwechatdemo.model.bean.Message;
import com.song.testwechatdemo.model.bean.UserInfo;
import com.song.testwechatdemo.views.adapters.MyAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/11/10.
 * interval,PublishSubject<String> mSubject,doOnext
 */

public class MainPresenter implements MainContract.Presenter {
    private final Context mContext;
    private ConnectivityManager connectivityManager;
    private ConnectReceiver connectReceiver;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private MainContract.View mainView;
    private volatile boolean isActive;
    private ArrayList<Message> trendDatas = new ArrayList<>();
    private IntentFilter filter;

    public MainPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void attachView(MainContract.View view) {
        mainView = view;
    }

    @Override
    public void onCreate() {
        initNetReceiver(mContext);
        loadUserInfo(true);
    }

    private void loadUserInfo(boolean onLineFirst) {
        if (isActive) {
            return;
        }
        isActive = true;
        compositeSubscription.add(DataRepository.getInstance().userInfo(onLineFirst)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onStart() {
                        mainView.onStartLoading();
                    }

                    @Override
                    public void onCompleted() {
                        loadData(true, onLineFirst);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof CompositeException) {
                            ArrayList<Throwable> list = new ArrayList(((CompositeException) e).getExceptions());
                            e = list.get(0);
                        }
                        mainView.onError(e.getMessage(), -1, AppApi.USER_INFO);
                        mainView.onStopLoading();
                        e.printStackTrace();
                        isActive = false;
                    }

                    @Override
                    public void onNext(UserInfo messages) {
                        if (messages == null) {//接口调用成功，但返回空数据
                            mainView.onError("返回空数据", -1, AppApi.USER_INFO);
                        } else {
                            mainView.onSuccess(messages, AppApi.USER_INFO);
                        }
                    }
                }));
    }

    /**
     * is initialization
     *
     * @param init
     */
    private void loadData(boolean init, boolean onLineFirst) {
        if (!init) {
            if (isActive) {
                return;
            }
            isActive = true;
        }
        compositeSubscription.add(DataRepository.getInstance().trendList(onLineFirst)
                .map(tokenData -> {
                    ArrayList<Message> messages = new ArrayList<>();
                    if (tokenData != null && !tokenData.isEmpty()) {
                        for (Message message :
                                tokenData) {
                            if (TextUtils.isEmpty(message.getContentStr())) {
                                message.setContentStr("empty str");
                            }
                            //top
                            if (message.getSender() == null) {
                                UserInfo userInfo = new UserInfo();
                                userInfo.setNick("unknowName");
                                message.setSender(userInfo);
                            }
                            Message topMsg = new Message();
                            topMsg.setContentStr(message.getContentStr());
                            topMsg.setSender(message.getSender());
                            topMsg.viewType = MyAdapter.ITEM_TOP;
                            messages.add(topMsg);
                            //img
                            if (message.getImages() != null && !message.getImages().isEmpty()) {
                                Message imgMsg = new Message();
                                imgMsg.setImages(message.getImages());
                                imgMsg.viewType = MyAdapter.ITEM_IMAGE;
                                imgMsg.dataSize = imgMsg.getImages().size();
                                messages.add(imgMsg);
                            }
                            //bottom
                            if (message.getComments() != null && !message.getComments().isEmpty()) {
                                for (Message comment :
                                        message.getComments()) {
                                    if (!TextUtils.isEmpty(comment.getContentStr())) {
                                        message.setContentStr("empty str");
                                    }
                                    Message commentMsg = new Message();
                                    if (comment.getSender() == null) {
                                        UserInfo userInfo = new UserInfo();
                                        userInfo.setNick("unknowName");
                                        comment.setSender(userInfo);
                                    }
                                    commentMsg.setSender(comment.getSender());
                                    commentMsg.setContentStr(":" + comment.getContentStr());
                                    commentMsg.viewType = MyAdapter.ITEM_BOTTOM;
                                    messages.add(commentMsg);
                                }
                            }
//                            if (!TextUtils.isEmpty(message.getContentStr())) {
//                                //top
//                                if (message.getSender() != null) {
//                                    Message topMsg = new Message();
//                                    topMsg.setContentStr(message.getContentStr());
//                                    topMsg.setSender(message.getSender());
//                                    topMsg.viewType = MyAdapter.ITEM_TOP;
//                                    messages.add(topMsg);
//                                }
//                                //img
//                                if (message.getImages() != null && !message.getImages().isEmpty()) {
//                                    Message imgMsg = new Message();
//                                    imgMsg.setImages(message.getImages());
//                                    imgMsg.viewType = MyAdapter.ITEM_IMAGE;
//                                    imgMsg.dataSize = imgMsg.getImages().size();
//                                    messages.add(imgMsg);
//                                }
//                                //bottom
//                                if (message.getComments() != null && !message.getComments().isEmpty()) {
//                                    for (Message comment :
//                                            message.getComments()) {
//                                        if (!TextUtils.isEmpty(comment.getContentStr())) {
//                                            Message commentMsg = new Message();
//                                            commentMsg.setSender(comment.getSender());
//                                            commentMsg.setContentStr(":" + comment.getContentStr());
//                                            commentMsg.viewType = MyAdapter.ITEM_BOTTOM;
//                                            messages.add(commentMsg);
//                                        }
//                                    }
//                                }
//                            }
                        }
                        return messages;
                    } else {
                        return messages;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Message>>() {
                    @Override
                    public void onStart() {
                        mainView.onStartLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mainView.onStopLoading();
                        isActive = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof CompositeException) {
                            ArrayList<Throwable> list = new ArrayList(((CompositeException) e).getExceptions());
                            e = list.get(0);
                        }
                        mainView.onError(e.getMessage(), -1, AppApi.MESSAGES);
                        mainView.onStopLoading();
                        e.printStackTrace();
                        isActive = false;
                    }

                    @Override
                    public void onNext(ArrayList<Message> messages) {
                        if (messages == null) {//接口调用成功，但返回空数据
                            mainView.onError("返回空数据", -1, AppApi.MESSAGES);
                        } else {
                            mainView.onSuccess(messages, AppApi.MESSAGES);
                        }
                    }
                }));
    }

    @Override
    public void onResume(boolean empty) {
        if (empty && !isActive) {
            loadUserInfo(false);
        }
    }

    @Override
    public void onStop() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
            compositeSubscription.clear();
        }
    }

    private void initNetReceiver(Context context) {
        connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        connectReceiver = new ConnectReceiver();
        checkNet();//首次先主动赋值
        if (filter == null) {
            filter = new IntentFilter();
            //监听wifi连接（手机与路由器之间的连接）
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            //监听互联网连通性（也就是是否已经可以上网了），当然只是指wifi网络的范畴
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            //这个是监听网络状态的，包括了wifi和移动网络。
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
    }

    @Override
    public BroadcastReceiver getReceiver() {
        return connectReceiver;
    }

    @Override
    public IntentFilter getFilter() {
        return filter;
    }

    private void checkNet() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            if (name.equals("WIFI")) {
                App.netState = App.NETWORK_TYPE_WIFI;
            } else {
                App.netState = App.NETWOKR_TYPE_MOBILE;
            }
        } else {
            App.netState = App.NETWORK_TYPE_NONE;
        }
    }

    class ConnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkNet();
            }
        }

    }
}
