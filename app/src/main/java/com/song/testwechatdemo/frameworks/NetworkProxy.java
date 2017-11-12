package com.song.testwechatdemo.frameworks;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by Administrator on 2017/11/10.
 */

final class NetworkProxy {
    private NetWorkProxyBuilder netWorkProxyBuilder;

    private NetworkProxy() {
    }

    /**
     * 初始化
     *
     * @param context
     * @return
     */
    public static NetworkProxy build(Context context) {
        if (NestedManagerClass.instance.netWorkProxyBuilder == null) {
            NestedManagerClass.instance.netWorkProxyBuilder =
                    new NetWorkProxyBuilder(context);
        }
        return NestedManagerClass.instance;
    }

    private static NetworkProxy getInstance() {
        if (NestedManagerClass.instance == null) {
            throw new NullPointerException("还未初始化");
        }
        return NestedManagerClass.instance;
    }

    public static AppApi network() {
        return getInstance().netWorkProxyBuilder.api;
    }

    public static Gson gson() {
        return getInstance().netWorkProxyBuilder.gson;
    }

    private static class NestedManagerClass {
        private static NetworkProxy instance
                = new NetworkProxy();
    }

    private static class NetWorkProxyBuilder {
        Context context;
        Gson gson;
        AppApi api;

        private NetWorkProxyBuilder(Context context) {
            this.context = context;
            gson = new GsonBuilder()
//                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            api = AppApi.Factory.create(gson);
        }
    }
}
