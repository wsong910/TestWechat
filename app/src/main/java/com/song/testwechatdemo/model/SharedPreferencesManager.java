package com.song.testwechatdemo.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Administrator on 2017/11/10.
 */
public class SharedPreferencesManager {
    private String filename = "TEST";
    private SharedPreferences ps;
    private Editor editor;

    public SharedPreferences getPs() {
        return ps;
    }

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesManager(Context context) {
        ps = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        editor = ps.edit();
    }

    public void clearData() {
        editor.clear();
        editor.commit();
    }

}