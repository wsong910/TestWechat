package com.song.testwechatdemo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v4.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/10.
 */

public class MemoryCacheManager {
    private final MemoryCache memoryCache;
    private final SharedPreferences ps;
    private Type type = new TypeToken<LinkedHashMap<String, Boolean>>() {
    }.getType();
    private SharedPreferences.Editor editor;

    private MemoryCacheManager(Context context, int maxSize) {
        memoryCache = new MemoryCache(maxSize);
        ps = context.getSharedPreferences("memoryKV", Context.MODE_PRIVATE);
        Observable<Boolean> observable = Observable.just(0).map(integer -> {
            String str = ps.getString("kvMap", null);
            if (str != null) {
                Gson gson = new Gson();
                LinkedHashMap<String, Boolean> kvMap = gson.fromJson(str, type);
                for (String key :
                        kvMap.keySet()) {
                    memoryCache.put(key, kvMap.get(key));
                }
            }
            return true;
        });
        if (Looper.myLooper() == Looper.getMainLooper()) {
            observable.subscribeOn(Schedulers.io()).subscribe(a -> {
            }, Throwable::printStackTrace);
        } else {
            observable.subscribe(a -> {
            }, Throwable::printStackTrace);
        }
    }

    public void putApiCached(String key, boolean flag) {
        memoryCache.put(key, flag);
        putSP();
    }

    public boolean isApiCached(String key) {
        Object object = memoryCache.get(key);
        return object != null && (Boolean) object;
    }

    private void putSP() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(memoryCache.snapshot(), type);
        if (editor == null) {
            editor = ps.edit();
        }
        editor.putString("kvMap", jsonStr);
        editor.apply();
    }

    private static class MemoryCache extends LruCache<String, Object> {

        public MemoryCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Object value) {
            if (value instanceof Integer) {
                return 8;
            }
            if (value instanceof String) {
                return ((String) value).length() * 2;
            }
            if (value instanceof Boolean) {
                return 2;
            }
            return super.sizeOf(key, value);
        }
    }


    public static class Builder {

        private static final int MAX_SIZE = 1024 * 1024 * 5;

        private int maxSize = MAX_SIZE;

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public MemoryCacheManager build(Context context) {
            return new MemoryCacheManager(context, maxSize);
        }
    }


}
