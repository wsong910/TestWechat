package com.song.testwechatdemo;

import android.app.Application;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.song.testwechatdemo.frameworks.DataRepository;
import com.song.testwechatdemo.model.DbHelper;
import com.song.testwechatdemo.model.MemoryCacheManager;
import com.song.testwechatdemo.model.SharedPreferencesManager;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/11/10.
 */

public class App extends Application {
    public static final int NETWORK_TYPE_NONE = -0x1; // 断网情况
    public static final int NETWORK_TYPE_WIFI = 0x1; // WiFi模式
    public static final int NETWOKR_TYPE_MOBILE = 0x2; // gprs模式
    public static final int MAIN = 10;
    public static DbHelper dbHelper;
    public static int netState;
    public static App instance;
    public static MemoryCacheManager memoryCache;
    public static SharedPreferencesManager sp;
    public static CompositeSubscription compositeSubscription;
    public static File imageCacheDir;
    public static int screenWidth, screenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(Observable.just(0)
                .map(integer -> {
                    DisplayMetrics dm = App.this.getResources().getDisplayMetrics();
                    screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
                    screenHeight = dm.heightPixels; // 屏幕宽（像素，如：480px）
                    sp = new SharedPreferencesManager(this);
                    dbHelper = new DbHelper(App.this.getApplicationContext());//缓存数据库
                    memoryCache = new MemoryCacheManager.Builder().build(this);//缓存cache
                    imageCacheDir = Glide.getPhotoCacheDir(this);
                    DataRepository.init();
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    //友盟统计
                }, Throwable::printStackTrace));
    }
}
