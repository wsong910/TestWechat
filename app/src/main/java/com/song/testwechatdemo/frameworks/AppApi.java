package com.song.testwechatdemo.frameworks;

import android.util.Log;

import com.google.gson.Gson;
import com.song.testwechatdemo.BuildConfig;
import com.song.testwechatdemo.model.bean.Message;
import com.song.testwechatdemo.model.bean.UserInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/11/10.
 */

public interface AppApi {
//    CustomHttpLoggingIntercept loggingInterceptor = new CustomHttpLoggingIntercept(message -> Log.e("AppApi", message))
//            .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

    class Factory {

        public static AppApi create(Gson gson) {
            synchronized (AppApi.class) {
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        .addInterceptor(new HttpLoggingInterceptor(message -> Log.e("AppApi", message))
                                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(false)
                        .build();
                return new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(httpClient)
                        .build()
                        .create(AppApi.class);
            }
        }
    }

    String BASE_URL = "http://thoughtworks-ios.herokuapp.com";
    String USER_INFO = "/user/jsmith";
    String MESSAGES = "/user/jsmith/tweets";

    @Streaming
    @GET(USER_INFO)
    @Headers("Content-Type: application/json; charset=utf-8")
    Observable<UserInfo> userInfo();

    @Streaming
    @GET(MESSAGES)
    Observable<ArrayList<Message>> getMessages();

    /**
     * 上传图片 url
     */
    String UPLOAD_IMAGE = "/apiv1/storage/upload";
    /**
     * /**
     * 团购 url
     */
    String GROUPON_LIST = "/apiv1/groupon/list";

}
