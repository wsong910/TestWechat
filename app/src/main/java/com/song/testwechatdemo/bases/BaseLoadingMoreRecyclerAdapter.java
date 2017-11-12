package com.song.testwechatdemo.bases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.song.testwechatdemo.R;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseLoadingMoreRecyclerAdapter<T> extends DefaultAdapter<T> {

    protected Context mContext;
    protected int[] mItemLayoutIds;
    protected final int[] types;
    protected static final int NORMAL = 0;
    protected static final int LOADING_MORE = -11;
    protected final int headCount = 1;
    protected final int footCount = 1;

    /**
     * Created by Administrator on 2017/11/10.
     */

    public BaseLoadingMoreRecyclerAdapter(Context context, int[] types, int[] itemLayoutIds) {
        mContext = context;
        if (types != null) {
            this.types = Arrays.copyOf(types, types.length);
        } else {
            throw new IllegalArgumentException("types is NULL");
        }
        if (itemLayoutIds != null) {
            mItemLayoutIds = Arrays.copyOf(itemLayoutIds, itemLayoutIds.length);
        } else {
            throw new IllegalArgumentException("itemLayoutIds is NULL");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerHolder holder = null;
        if (viewType == LOADING_MORE) {
            holder = new MoreHolder(LayoutInflater.from
                    (mContext).inflate(R.layout.pull_footer, parent, false));
        } else {
            View view = null;
            for (int i = 0; i < types.length; i++) {
                if (types[i] == viewType) {
                    view = LayoutInflater.from(mContext).
                            inflate(mItemLayoutIds[i], parent, false);
                    holder = createCustomViewHolder(view, viewType);
                    if (holder == null) {
                        holder = new BaseRecyclerHolder(view, viewType);
                    }
                    break;
                }
            }
        }
        initHolderView(viewType, holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        BaseRecyclerHolder baseHolder = (BaseRecyclerHolder) holder;
        if (viewType == LOADING_MORE) {
            ((BaseRecyclerHolder) holder).configViewByData(position);
        } else {
            int startIndex = getInsertStartCount() + headCount;
            int endIndex = getItemCount() - headCount - getInsertEndCount() - getInsertStartCount();
            if (position >= startIndex && position < endIndex) {
                convert(viewType, baseHolder, (T) getDatas().get(position - startIndex), position);
            } else {
                convert(viewType, baseHolder, null, position);
            }
        }
    }

    /**
     * onCreateViewHolder配置view的初始化
     *
     * @param viewType
     * @param holder
     */
    protected void initHolderView(int viewType, BaseRecyclerHolder holder) {
    }

    /**
     * @param viewType
     * @param holder   自定义的ViewHolder对象，可以getView获取控件
     * @param item     对应的数据
     * @param position
     */
    protected abstract void convert(int viewType, BaseRecyclerHolder holder, T item, int position);

    /**
     * 当在纯数据mDatas前存在其它数据时，需要重写定义此方法
     *
     * @return
     */
    public int getInsertStartCount() {
        return 0;
    }

    /**
     * 当在纯数据mDatas后存在其它数据时，需要重写定义此方法
     *
     * @return
     */
    public int getInsertEndCount() {
        return 0;
    }

    /**
     * 当有多个type时，此方法需要重写
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (getItemsCount() == 0) {
            return headCount + footCount;
        }
        return getItemsCount() + headCount + footCount;//loading
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return LOADING_MORE;
        }
        return super.getItemViewType(position);
    }

    /**
     * 纯数据item数量
     *
     * @return
     */
    public int getItemsCount() {
        return isEmpty() ? 0 : getDatas().size();
    }

    @NonNull
    protected BaseRecyclerHolder createNoDataViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    protected BaseRecyclerHolder createCustomViewHolder(View itemView, int viewType) {
        return null;
    }

    public static class BaseRecyclerHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertView;
        int itemType = RecyclerView.INVALID_TYPE;

        public BaseRecyclerHolder(View itemView) {
            this(itemView, RecyclerView.INVALID_TYPE);
        }

        public BaseRecyclerHolder(View itemView, int itemType) {
            super(itemView);
            mConvertView = itemView;
            mViews = new SparseArray<>();
            mConvertView.setTag(this);
            this.itemType = itemType;
        }

        /**
         * 通过viewId获取控件
         */
        public <T> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
//            return (T) mConvertView.findViewById(viewId);
        }

        public View getConvertView() {
            return mConvertView;
        }

        public <T> void configViewByData(int position, T... item) {

        }
    }

    static class MoreHolder extends BaseRecyclerHolder {

        public MoreHolder(View itemView) {
            super(itemView);
        }

        @Override
        public <T> void configViewByData(int position, T... item) {
        }

    }
}

