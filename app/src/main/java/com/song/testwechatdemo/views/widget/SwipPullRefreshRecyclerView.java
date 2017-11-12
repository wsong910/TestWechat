package com.song.testwechatdemo.views.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.song.testwechatdemo.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/11/10.
 */
public class SwipPullRefreshRecyclerView extends ViewGroup {
    private int mLastChildIndex;
    private static FreshStatus mFreshStatus = FreshStatus.NORMAL;
    private static LoadMoreStatus mLoadMoreStatus = LoadMoreStatus.NORMAL;
    private float mLastYIntercept;
    private float mLastMoveY;
    private Scroller mScroller;
    private final Drawable mCircleDrawable;

    private Rect mCircleSrcRect;
    /**
     * coordinate x
     */
    private int mCircleX = 50;
    /**
     * loading icon Height
     */
    private int mCircleH;
    /**
     * cur degree
     */
    private int mCircleDegree = 0;
    /**
     * loading point
     */
    private final int mShowHeight;
    /**
     * control anim,loading and hiding
     */
    private volatile boolean startAnim, hideAnim, showAnim;
    /**
     * loading anim degree
     */
    private final static int DEGREE_CIRCLE = 360;
    public CompositeSubscription destroyComposite = new CompositeSubscription();
    private int mScrollY;
    private OnFreshListener onFreshingListener;


    private enum FreshStatus {
        NORMAL, FRESHING;
    }

    private enum LoadMoreStatus {
        NORMAL, LOADING;
    }


    public SwipPullRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public SwipPullRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipPullRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(getContext(), new AccelerateInterpolator(1.5f));
        mCircleDrawable = getResources().getDrawable(R.drawable.ic_rainbow);
        int mCircleW = getResources().getDimensionPixelSize(R.dimen.fresh_width);
        mCircleH = getResources().getDimensionPixelSize(R.dimen.fresh_height);
        mCircleSrcRect = new Rect();
        mCircleSrcRect.left = mCircleX;
        mCircleSrcRect.top = 0;
        mCircleSrcRect.right = mCircleW + mCircleX;
        mCircleSrcRect.bottom = mCircleH;
        mCircleDrawable.setBounds(mCircleSrcRect);
        mShowHeight = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (destroyComposite.hasSubscriptions()) {
            return;
        }
        Subscription subscription = Observable.interval(0, 16, TimeUnit.MILLISECONDS, Schedulers.io())
                .map(aLong -> {
                    if (startAnim) {
                        mCircleDegree += 30;
                        if (mCircleDegree >= 359) {
                            mCircleDegree = 0;
                        }
                        postInvalidate();
                    }
                    if (hideAnim) {
                        if (mCircleSrcRect.top <= 0) {
                            mCircleSrcRect.top = 0;
                            mCircleSrcRect.bottom = mCircleH;
                            hideAnim = false;
                            startAnim = false;
                            mFreshStatus = FreshStatus.NORMAL;
                        } else {
                            mCircleSrcRect.offset(0, -5);
                        }
                        mCircleDrawable.setBounds(mCircleSrcRect);
                    } else if (showAnim) {
                        if (mCircleSrcRect.top >= (mShowHeight << 1)) {
                            mCircleSrcRect.top = mShowHeight << 1;
                            mCircleSrcRect.bottom = mCircleH + (mShowHeight << 1);
                            showAnim = false;
                            startAnim = true;
                            mFreshStatus = FreshStatus.FRESHING;
                        } else {
                            mCircleSrcRect.offset(0, +5);
                        }
                        mCircleDrawable.setBounds(mCircleSrcRect);
                    }
                    return true;
                })
                .subscribe(r -> {
                }, Throwable::printStackTrace);
        destroyComposite.add(subscription);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyComposite.clear();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.rotate(mCircleDegree % DEGREE_CIRCLE, mCircleSrcRect.centerX(), mCircleSrcRect.centerY());
        mCircleDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLastChildIndex = getChildCount() - 1;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int mLayoutContentHeight = 0;
        for (int j = 0; j < getChildCount(); j++) {
            final View child = getChildAt(j);
            child.layout(0, mLayoutContentHeight, child.getMeasuredWidth(), mLayoutContentHeight + child.getMeasuredHeight());
            if (j < getChildCount()) {
                mLayoutContentHeight += child.getMeasuredHeight();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = y;
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (y > mLastYIntercept) {
                    final View child = getChildAt(0);
                    intercept = getReFreshIntercept(child);
                } else if (y < mLastYIntercept) {
                    final View child = getChildAt(mLastChildIndex);
                    intercept = getLoadMoreIntercept(child);
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        mLastYIntercept = y;
        return intercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                final float dy = mLastMoveY - y;
                scrollBy(0, (int) dy / 3);
                startAnim = false;
                mCircleDegree += ((int) dy << 1);
                break;
            case MotionEvent.ACTION_UP:
                smoothScrollBy(mScrollY);
                break;
        }
        mLastMoveY = y;
        return super.onTouchEvent(event);
    }

    private boolean getReFreshIntercept(View child) {
        if (child instanceof RecyclerView) {
            boolean intercept = false;
            RecyclerView recyclerView = (RecyclerView) child;
            if (recyclerView.computeVerticalScrollOffset() <= 0) {
                intercept = true;
            }
            return intercept;
        }
        return false;
    }

    private boolean getLoadMoreIntercept(View child) {
        if (child instanceof RecyclerView) {
            boolean intercept = false;
            RecyclerView recyclerView = (RecyclerView) child;
            if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                    >= recyclerView.computeVerticalScrollRange()) {
                intercept = true;
            }
            return intercept;

        }
        return false;
    }

    private void smoothScrollBy(int dy) {
        if (mScroller.getFinalY() == 0) {
            mScroller.startScroll(0, dy, 0, -dy);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {//scroll auto
            int curry = mScroller.getCurrY();
            scrollTo(mScroller.getCurrX(), curry);
            postInvalidate();
        }
        //when anim start,deal noting
        if (startAnim) {
            return;
        }
        //deal loading icon
        mScrollY = getScrollY();
        if (mScrollY < 0) {
            if (-mScrollY > mShowHeight && mFreshStatus != FreshStatus.FRESHING) {//freshing on this point
                mFreshStatus = FreshStatus.FRESHING;
                mCircleSrcRect.offsetTo(mCircleX, (mShowHeight << 1) + mScrollY);
                if (onFreshingListener != null) {
                    onFreshingListener.onFreshing();
                }
            } else {
                if (mFreshStatus == FreshStatus.FRESHING) {//keep on this point
                    mCircleSrcRect.offsetTo(mCircleX, (mShowHeight << 1) + mScrollY);
                } else {//scroll back
                    mCircleSrcRect.offsetTo(mCircleX, -mScrollY);
                }
            }
        } else if (mScrollY > 0) {
            if (mScrollY >= mShowHeight && mFreshStatus != FreshStatus.NORMAL) {
                mFreshStatus = FreshStatus.NORMAL;
                System.out.println("Status.NORMAL");
                hideAnim = true;
                startAnim = false;
            }
        } else {
            if (mFreshStatus != FreshStatus.FRESHING) {
                mCircleSrcRect.offsetTo(mCircleX, 0);
            } else {
                mCircleSrcRect.offsetTo(mCircleX, mShowHeight << 1);
                startAnim = true;
            }
        }
        mCircleDrawable.setBounds(mCircleSrcRect);
        super.computeScroll();
    }

    public void startFreshing() {
        hideAnim = false;
        showAnim = true;
    }

    public void stopFreshing() {
        showAnim = false;
        hideAnim = true;
    }


    public interface OnFreshListener {
        void onFreshing();
    }

    public void setOnFreshingListener(OnFreshListener onFreshingListener) {
        this.onFreshingListener = onFreshingListener;
    }

    public boolean isOnFreshing() {
        return mFreshStatus == FreshStatus.FRESHING;
    }

    private static class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private static final int VISIBLE_THRESHOLD = 6;
        private final LinearLayoutManager layoutManager;
        private final LoadMoreSubject loadMoreSubject;


        private EndlessScrollListener(LinearLayoutManager layoutManager, LoadMoreSubject loadMoreSubject) {
            this.layoutManager = layoutManager;
            this.loadMoreSubject = loadMoreSubject;
        }


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy < 0 || mLoadMoreStatus == LoadMoreStatus.LOADING) {
                return;
            }
            if (layoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1) {
                mLoadMoreStatus = LoadMoreStatus.LOADING;
                loadMoreSubject.onLoadMore();
            }
        }
    }


    public interface LoadMoreSubject {
        void onLoadMore();
    }

    public void setLoadMoreSubject(LoadMoreSubject loadMoreSubject) {
        RecyclerView recyclerView = (RecyclerView) getChildAt(0);
        recyclerView.addOnScrollListener(new EndlessScrollListener(
                (LinearLayoutManager) recyclerView.getLayoutManager(),
                loadMoreSubject));
    }

    public void stopLoading() {
        mLoadMoreStatus = LoadMoreStatus.NORMAL;
    }

    public boolean isLoading() {
        return mLoadMoreStatus == LoadMoreStatus.LOADING;
    }
}
