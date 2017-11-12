package com.song.testwechatdemo.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.song.testwechatdemo.App;
import com.song.testwechatdemo.R;
import com.song.testwechatdemo.bases.BaseLoadingMoreRecyclerAdapter;
import com.song.testwechatdemo.frameworks.GlideApp;
import com.song.testwechatdemo.model.bean.ImageData;
import com.song.testwechatdemo.model.bean.Message;
import com.song.testwechatdemo.model.bean.UserInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/11/10.
 */


public class MyAdapter extends BaseLoadingMoreRecyclerAdapter<Message> {
    private static final int HEADER = 0;
    public static final int ITEM_TOP = 1;
    public static final int ITEM_IMAGE = 2;
    public static final int ITEM_BOTTOM = 3;
    private String headerUrl;
    private UserInfo userInfo;

    public MyAdapter(Context context) {
        super(context, new int[]{HEADER, ITEM_TOP, ITEM_IMAGE, ITEM_BOTTOM}
                , new int[]{R.layout.header, R.layout.item_top, R.layout.item_image_content, R.layout.item_bottom});
    }

    @Override
    protected BaseRecyclerHolder createCustomViewHolder(View itemView, int viewType) {
        if (viewType == HEADER) {
            return new HeaderHolder(itemView);
        } else if (viewType == ITEM_TOP) {
            return new TopHolder(itemView);
        } else if (viewType == ITEM_IMAGE) {
            return new ImageHolder(itemView);
        } else if (viewType == ITEM_BOTTOM) {
            return new BottomHolder(itemView);
        }
        return super.createCustomViewHolder(itemView, viewType);
    }

    @Override
    protected void convert(int viewType, BaseRecyclerHolder holder, Message item, int position) {
        if (viewType == HEADER) {
            if(userInfo==null) {
                holder.configViewByData(position, "");
            } else {
                holder.configViewByData(position, userInfo.getProfileImage());
            }
        } else if (viewType == ITEM_TOP) {
            holder.configViewByData(position, item);
        } else if (viewType == ITEM_IMAGE) {
            holder.configViewByData(position, item);
        } else if (viewType == ITEM_BOTTOM) {
            holder.configViewByData(position, item);
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return getItemsCount() + getInsertStartCount() + getInsertEndCount() + headCount + footCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        if (position == getItemCount() - 1) {
            return LOADING_MORE;
        }
        return getDatas().get(position - 1).viewType;
    }

    static class HeaderHolder extends BaseRecyclerHolder {
        private final RequestOptions options;
        private ImageView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = getView(R.id.headImg);
            options = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }

        @Override
        public <T> void configViewByData(int position, T... item) {
            Glide.with(header.getContext())
                    .load(item[0])
                    .apply(options)
//                    .thumbnail(Glide.with(header.getContext()).load(item))
                    .into(header);
        }
    }

    static class TopHolder extends BaseRecyclerHolder {
        private final RequestOptions options;
        @BindView(R.id.top_line)
        View topLine;
        @BindView(R.id.icon_image)
        ImageView iconImage;
        @BindView(R.id.name_text)
        TextView nameText;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.outermost_layout)
        RelativeLayout outermostLayout;

        public TopHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            options = new RequestOptions()
                    .placeholder(R.drawable.btn_qq_bg)
                    .error(R.drawable.btn_qq_bg)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public <T> void configViewByData(int position, T... item) {
            Message message = (Message) item[0];
            System.out.println("message=" + message + ",position=" + position);
            UserInfo sender = message.getSender();
            GlideApp.with(iconImage.getContext())
                    .load(sender.getAvatar())
                    .apply(options)
                    .into(iconImage);
            nameText.setText(sender.getNick());
            content.setText(message.getContentStr());
        }
    }

    static class ImageHolder extends BaseRecyclerHolder {
        private final LayoutInflater mInflater;
        private final Context context;
        private final RequestOptions options;
        private RelativeLayout imageContent;
        private int imageSigleHeight, imageMaxSize, imageNormalSize, marginRight,
                marginRightSingle, marginBottom;
        private int[] trends_ids = {R.id.trends_id0, R.id.trends_id1, R.id.trends_id2,
                R.id.trends_id3, R.id.trends_id4, R.id.trends_id5,
                R.id.trends_id6, R.id.trends_id7, R.id.trends_id8,};

        public ImageHolder(View itemView) {
            super(itemView);
            imageContent = itemView.findViewById(R.id.image_content);
            context = itemView.getContext();
            mInflater = LayoutInflater.from(context);
            marginBottom = context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_margin_bottom);
            marginRight = context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_margin_right);
            marginRightSingle = context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_single_margin_right);
            final int maxWidth = App.screenWidth - context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_content_left_margin)
                    - context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_content_right_margin);
            imageSigleHeight = context.getResources()
                    .getDimensionPixelSize(R.dimen.trends_image_sigle_height);
            imageMaxSize = maxWidth >> 1;
            imageNormalSize = maxWidth / 3 - marginRight;
            options = new RequestOptions()
                    .placeholder(R.drawable.btn_qzone_bg)
                    .error(R.drawable.btn_qzone_bg)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }

        @Override
        public <T> void configViewByData(int position, T... item) {
            Message message = (Message) item[0];
            if (message != null) {
                List<ImageData> urls = message.getImages();
                imageContent.removeAllViews();
                if (urls != null) {
                    if (urls.size() == 1) {
                        final RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                imageMaxSize, imageMaxSize);
                        final ImageView imageView = (ImageView) mInflater.inflate(
                                R.layout.trends_list_image_item, null);
                        rlp.setMargins(0, 0, marginRight, marginBottom);
                        imageView.setLayoutParams(rlp);
                        imageContent.addView(imageView);
                        Glide.with(context)
                                .load(urls.get(0).getUrl())
                                .apply(options)
                                .thumbnail(Glide.with(context).load(urls.get(0).getUrl()))
                                .into(imageView);
                    } else {
                        for (int i = 0; i < urls.size(); i++) {
                            final RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                    imageNormalSize, imageNormalSize);
                            final ImageView imageView = (ImageView) mInflater.inflate(
                                    R.layout.trends_list_image_item, null);
                            final int imagePosition = i;
                            imageView.setLayoutParams(rlp);
                            imageView.setId(trends_ids[i]);
                            rlp.setMargins(0, 0, marginRight, marginBottom);
                            switch (i % 3) {
                                case 0:
                                    if (i == 3) {
                                        rlp.addRule(RelativeLayout.BELOW, trends_ids[0]);
                                    } else if (i == 6) {
                                        rlp.addRule(RelativeLayout.BELOW, trends_ids[3]);
                                    }
                                    break;
                                case 1:
                                case 2:
                                    rlp.addRule(RelativeLayout.ALIGN_TOP, trends_ids[i - 1]);
                                    rlp.addRule(RelativeLayout.RIGHT_OF, trends_ids[i - 1]);
                                    break;
                            }
                            imageContent.addView(imageView);
                            Glide.with(context)
                                    .load(urls.get(i).getUrl())
                                    .apply(options)
                                    .thumbnail(Glide.with(context).load(urls.get(i).getUrl()))
                                    .into(imageView);
                        }
                    }
                }
            }
        }
    }

    static class BottomHolder extends BaseRecyclerHolder {
        @BindView(R.id.textView2)
        TextView textView2;
        @BindView(R.id.textView3)
        TextView textView3;

        public BottomHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void configViewByData(int position, T... item) {
            Message message = (Message) item[0];
            UserInfo sender = message.getSender();
            if (sender != null) {
                textView2.setText(sender.getNick());
                textView3.setText(message.getContentStr());
            }
        }
    }
}
