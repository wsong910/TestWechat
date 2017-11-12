package com.song.testwechatdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.song.testwechatdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/10.
 */

public class CustomToolBar extends RelativeLayout {


    private int mTitleKind = 0;
    @BindView(R.id.close)
    ImageButton close;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.photo)
    ImageButton photo;

    public CustomToolBar(Context context) {
        this(context, null);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_titlebar, this);
        TypedArray a = null;
        if (attrs != null) {
            a = context.obtainStyledAttributes(attrs, R.styleable.titleView);
            mTitleKind = a.getInt(0, 0);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        switch (mTitleKind) {
            case 0:
                photo.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.close, R.id.photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                break;
            case R.id.photo:
                break;
        }
    }

}
