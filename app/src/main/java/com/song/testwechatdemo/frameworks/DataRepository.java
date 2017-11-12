package com.song.testwechatdemo.frameworks;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.song.testwechatdemo.App;
import com.song.testwechatdemo.model.DbCache;
import com.song.testwechatdemo.model.MemoryCacheManager;
import com.song.testwechatdemo.model.bean.Message;
import com.song.testwechatdemo.model.bean.UserInfo;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2017/11/10.
 */


public class DataRepository {
    private static final String TEXT_PLAIN = "text/plain";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static DataRepository INSTANCE;
    private NetworkProxy networkProxy;
    private DbCache dbCache;
    private MemoryCacheManager memoryCacheManager;
    private final static String NON_CATCH = "0";

    public static void init() {
        DataRepository.getInstance();
    }

    public static DataRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository();
        }
        return INSTANCE;
    }

    private DataRepository() {
        if (networkProxy == null) {
            networkProxy = NetworkProxy.build(App.instance);//网络代理接口代理
        }
        dbCache = DbCache.getInstance();
        memoryCacheManager = App.memoryCache;
    }

    public Gson outPacakgeGetGson() {
        return NetworkProxy.gson();
    }


    public Observable<UserInfo> userInfo(boolean getOnlineData) {
        final String key = getCatchKey(null, null, AppApi.USER_INFO);
        Observable<UserInfo> local = dbCache.get(key, memoryCacheManager.isApiCached(key))
                .flatMap(new Converter<UserInfo>() {
                }::convert);
        Observable<UserInfo> remote = NetworkProxy.network().userInfo()
                .map(responseData -> {
                    if (responseData == null) {
                        dbCache.delete(key);
                        memoryCacheManager.putApiCached(key, false);
                    } else {
                        dbCache.put(key, responseData);
                        memoryCacheManager.putApiCached(key, true);
                    }
                    return responseData;
                });
        if (getOnlineData) {
            return Observable.concatDelayError(remote, local).takeFirst(data -> data != null);
        } else {
            return Observable.concatDelayError(local, remote).takeFirst(data -> data != null);
        }
    }

    public Observable<ArrayList<Message>> trendList( boolean getOnlineData) {
        final String key = getCatchKey(null, null, AppApi.MESSAGES);
        Observable<ArrayList<Message>> remote = NetworkProxy.network().getMessages()
                .map(responseData -> {
                    if (responseData == null || responseData.isEmpty()) {
                        dbCache.delete(key);
                        memoryCacheManager.putApiCached(key, false);
                        System.out.println("threadname="+Thread.currentThread().getName());
                    } else {
                        dbCache.put(key, responseData);
                        memoryCacheManager.putApiCached(key, true);
                        System.out.println("threadname="+Thread.currentThread().getName());
                    }
                    return responseData;
                });
        Observable<ArrayList<Message>> local = dbCache.get(key, memoryCacheManager.isApiCached(key))
                .flatMap(new Converter<ArrayList<Message>>() {
                }::convert);
        if (getOnlineData) {
            return Observable.concatDelayError(remote, local).takeFirst(data -> data != null);
        } else {
            return Observable.concatDelayError(local, remote).takeFirst(data -> data != null);
        }
    }

    private Map<String, RequestBody> createMap(Map<String, String> map, String type) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key :
                map.keySet()) {
            putRequestBodyMap(requestBodyMap, key, map.get(key), type);
        }
        return requestBodyMap;
    }

    private void putRequestBodyMap(Map map, String key, String value, String type) {
        putRequestBodyMap(map, key, createPartFromString(value, type));
    }


    @NonNull
    private RequestBody createPartFromString(String descriptionString, String type) {
        if (descriptionString == null) {
            descriptionString = "";
        }
        return FormBody.create(
                MediaType.parse(type), descriptionString);
    }

    private void putRequestBodyMap(Map map, String key, RequestBody body) {
        if (!TextUtils.isEmpty(key) && body != null) {
            map.put(key, body);
        }
    }

    /**
     * 获取缓存唯一key值
     *
     * @param map
     * @param uniqueTag
     * @param apiUrl
     * @return
     */
    private String getCatchKey(Map<String, String> map, String uniqueTag, String apiUrl) {
        return Observable.just(new StringBuilder())
                .map(stringBuilder -> {
                    if (map != null) {
                        Map<String, String> tempMap = new HashMap();
                        tempMap.putAll(map);
                        stringBuilder.append(Base64
                                .encodeToString(new ArrayList<>(tempMap.values()).toString().getBytes()
                                        , Base64.CRLF));
                    }
                    if (uniqueTag != null) {
                        stringBuilder.append(uniqueTag);
                    }
                    stringBuilder.append("_").append(apiUrl);
                    return stringBuilder.toString();
                })
                .subscribeOn(Schedulers.io())
                .toBlocking().first();
    }

    public static class Converter<T> {
        final Gson mGson;

        public Converter() {
            mGson = NetworkProxy.gson();
        }

        public Observable<T> convert(String s) {
            return Observable.unsafeCreate(subscriber -> {
                if (TextUtils.isEmpty(s)) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                    return;
                }
                System.out.println("1threadname="+Thread.currentThread().getName());
                final JsonReader jsonReader = mGson.newJsonReader(new StringReader(s));
                TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(getTypeOfT()));
                try {
                    T read = (T) adapter.read(jsonReader);
                    subscriber.onNext(read);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            });
        }

        public final Type getTypeOfT() throws TypeNotPresentException, MalformedParameterizedTypeException {
            return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }
}