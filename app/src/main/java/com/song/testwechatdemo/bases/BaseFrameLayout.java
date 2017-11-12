package com.song.testwechatdemo.bases;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/11/10.
 */

public abstract class BaseFrameLayout extends RelativeLayout {
    private View mView;
    protected Context mContext;

    public BaseFrameLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(initLayoutRes(), this);
    }

    @LayoutRes
    protected abstract int initLayoutRes();

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        onRestoreState(savedState);
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        onSaveState(savedState);
        return savedState;
    }

    protected void onRestoreState(SavedState savedState) {

    }

    protected void onSaveState(SavedState savedState) {

    }

    protected void closeImm() {
        if (getFocusedChild() != null) {
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getFocusedChild().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected static class SavedState extends BaseSavedState {
        public static final int FRESH_TOP = 1;
        public static final int FRESH_BOTTOM = 2;
        public int refreshKind;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            refreshKind = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(refreshKind);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
