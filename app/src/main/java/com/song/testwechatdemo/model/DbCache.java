package com.song.testwechatdemo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;


import com.song.testwechatdemo.App;
import com.song.testwechatdemo.frameworks.DataRepository;

import okio.Buffer;
import rx.Observable;
import rx.Subscriber;
/**
 * Created by Administrator on 2017/11/10.
 */

public class DbCache {

    private static DbCache INSTANCE;
    private final DbHelper dbHelper;
    /**
     * 本地缓存过期时间
     */
    private final static long REFRESH_TIME = 1000 * 60 * 60 * 2;//2h

    static void CreateTable(SQLiteDatabase db) {
        final String sql = "create table " + Entity.TABLE_NAME + " (" +
                Entity._ID + " int," +
                Entity.COLUMN_NAME_KEY + " CHAR(32) PRIMARY KEY," +
                Entity.COLUMN_NAME_VALUE + " TEXT," +
                Entity.COLUMN_NAME_LAST_UPDATE + " LONG" +
                ")";
        db.execSQL(sql);
    }

    public static DbCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbCache();
        }
        return INSTANCE;
    }

    private DbCache() {
        dbHelper = App.dbHelper;
    }

    private void insert(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(Entity.COLUMN_NAME_KEY, GenerateKey(key));
        values.put(Entity.COLUMN_NAME_VALUE, value);
        values.put(Entity.COLUMN_NAME_LAST_UPDATE, System.currentTimeMillis());
        String table = Entity.TABLE_NAME;
        dbHelper.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public <T> void put(String key, T obj) {
        String s = null;
        if (obj != null) {
            s = DataRepository.getInstance().outPacakgeGetGson().toJson(obj);
        }
        insert(key, s);
    }

    public void delete(String key) {
        if (key != null) {
            String table = Entity.TABLE_NAME;
            String clause = new StringBuilder().append(Entity.COLUMN_NAME_KEY).append("=?").toString();
            String[] args = new String[]{key};
            dbHelper.getWritableDatabase().delete(table, clause, args);
        }
    }

    private Observable<Entity> get(String key) {
        String realKey = GenerateKey(key);
        String sql = String.format("select * from %s where %s = ?", Entity.TABLE_NAME, Entity.COLUMN_NAME_KEY);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{realKey});
        if (cursor.moveToFirst()) {
            Entity entity = new Entity();
            entity.key = cursor.getString(cursor.getColumnIndex(Entity.COLUMN_NAME_KEY));
            entity.lastUpdate = cursor.getLong(cursor.getColumnIndex(Entity.COLUMN_NAME_LAST_UPDATE));
            entity.value = cursor.getString(cursor.getColumnIndex(Entity.COLUMN_NAME_VALUE));
            cursor.close();
            final long curTime = System.currentTimeMillis();
            final long dis = curTime - entity.lastUpdate;
            Log.e("DbCache", "get: " + dis);
            if (App.netState != App.NETWORK_TYPE_NONE
                    && dis > REFRESH_TIME) {//更新时间,如果有网的情况下超时，则返回null
                return Observable.just(null);
            }
            return Observable.just(entity);
        } else {
            cursor.close();
            return Observable.just(null);
        }
    }

    public Observable<String> get(String key, boolean cacheUsable) {
        return get(key)
                .defaultIfEmpty(null)
                .lift(subscriber -> new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Entity entity) {
                        if (entity != null && cacheUsable) {
                            subscriber.onNext(entity.value);
                        } else {
                            subscriber.onNext(null);
                        }
                        subscriber.onCompleted();
                    }
                });
    }

    private String GenerateKey(String url) {
        Buffer buffer = new Buffer();
        buffer.write(url.getBytes());
        return buffer.md5().hex();
    }

    @SuppressWarnings("unused")
    private <T> Entity mapper(Cursor cursor) {
        Entity entity = new Entity();
        entity.key = cursor.getString(cursor.getColumnIndex(Entity.COLUMN_NAME_KEY));
        entity.lastUpdate = cursor.getLong(cursor.getColumnIndex(Entity.COLUMN_NAME_LAST_UPDATE));
        entity.value = cursor.getString(cursor.getColumnIndex(Entity.COLUMN_NAME_VALUE));
        return entity;
    }


    private static class Entity implements BaseColumns {
        static final String TABLE_NAME = "cache";
        static final String COLUMN_NAME_KEY = "key";
        static final String COLUMN_NAME_VALUE = "value";
        static final String COLUMN_NAME_LAST_UPDATE = "last_update";


        String key;

        String value;

        Long lastUpdate;


    }

}
