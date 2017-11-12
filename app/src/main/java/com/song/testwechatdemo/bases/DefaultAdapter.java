package com.song.testwechatdemo.bases;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public abstract class DefaultAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> mDatas = new ArrayList<T>();

    public boolean isEmpty() {
        return mDatas == null || mDatas.size() == 0;
    }

    /**
     * 设置列表中的数据
     */
    public void setDatas(List<T> datas, boolean notifyAllIfNull) {
        if (datas == null) {
            if (notifyAllIfNull) {
                notifyDataSetChanged();
            }
            return;
        }
        mDatas = null;
        this.mDatas = new ArrayList<T>(datas);
        notifyDataSetChanged();
    }

    public List<T> getDatas() {
        return mDatas;
    }


}
