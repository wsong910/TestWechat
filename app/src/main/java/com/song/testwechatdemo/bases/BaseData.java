package com.song.testwechatdemo.bases;

import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/10.
 */

public class BaseData {
    public int hashCode(@Nullable Object... objects) {
        return Arrays.hashCode(objects);
    }

    public boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
