package com.song.testwechatdemo.views.adapters;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class DiffCallBack<T> extends DiffUtil.Callback {
    private List<T> mOldDatas, mNewDatas;

    public DiffCallBack(List<T> mOldDatas, List<T> mNewDatas) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
    }
}
