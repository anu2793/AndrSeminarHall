package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;

/**
 * Created by Administrator on 09/05/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private onActionClick mActionClick;

    private ArrayList<NewsItem> mListMusic = new ArrayList<>();
    private Context mContext;

    public NewsAdapter(Context context, ArrayList<NewsItem> list) {
        mListMusic = list;
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cr_news_list_row, parent, false);
        return new MusicViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NewsItem item = mListMusic.get(position);
        final MusicViewHolder userViewHolder = (MusicViewHolder) holder;
        if (item != null) {
            if (position == 0) {
                userViewHolder.mTvTime.setVisibility(View.VISIBLE);
            } else {
                NewsItem itemCompare = mListMusic.get(position - 1);
                if (item.getDateWithFomatJP().equals(itemCompare.getDateWithFomatJP())) {
                    userViewHolder.mTvTime.setVisibility(View.GONE);
                } else {
                    userViewHolder.mTvTime.setVisibility(View.VISIBLE);
                }
            }
            userViewHolder.mTvTitle.setText(item.getTitle());
            userViewHolder.mTvTime.setText(item.getDateWithFomatJP());
            userViewHolder.mTvDescription.setText(item.getDescription());
            if (item.getThumbNail().length() > 0) {
                Glide.with(mContext).load(item.getThumbNail()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        userViewHolder.mImvBg.setImageResource(R.mipmap.imv_default);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(userViewHolder.mImvBg);
                userViewHolder.mImvBg.setVisibility(View.VISIBLE);
            } else {
                userViewHolder.mImvBg.setVisibility(View.GONE);
            }
            userViewHolder.mImvRead.setVisibility(item.getRead() == AppConstants.STATUS_NEW.READ ? View.GONE : View.VISIBLE);
            if (TextUtils.isEmpty(item.getThumbNail())) {
                userViewHolder.mRlmain.getLayoutParams().height = (int) mContext.getResources().getDimension(R.dimen.value_height_view_new);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) userViewHolder.mLlTitle.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                userViewHolder.mLlTitle.setLayoutParams(params);
            } else {
                int widthScreen = AppUtils.getScreenWidth() > 0 ? AppUtils.getScreenWidth() : (int) mContext.getResources().getDimension(R.dimen.news_img_height);
                userViewHolder.mRlmain.getLayoutParams().height = widthScreen / 2;
                userViewHolder.mImvBg.getLayoutParams().height = widthScreen / 2;
            }
        }
        userViewHolder.mTvTime.setOnClickListener(null);
        userViewHolder.rlMark.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (mActionClick != null) {
                    mActionClick.onItemClick(item, position);
                }
            }
        });
        userViewHolder.mLlTitle.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (mActionClick != null) {
                    mActionClick.onItemClick(item, position);
                }
            }
        });
        userViewHolder.mRlmain.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mActionClick != null) {
                    mActionClick.onItemClick(item, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListMusic == null ? 0 : mListMusic.size();
    }


    private class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTime, mTvTitle, mTvDescription;
        ImageView mImvBg;
        RelativeLayout rlMark, mRlmain;
        LinearLayout mLlTitle;
        ImageView mImvRead;

        public MusicViewHolder(View view) {
            super(view);
            mTvTime = (TextView) view.findViewById(R.id.news_list_time);
            mImvBg = (ImageView) view.findViewById(R.id.news_image_view);
            mTvTitle = (TextView) view.findViewById(R.id.news_list_title);
            mTvDescription = (TextView) view.findViewById(R.id.layout_description);
            rlMark = (RelativeLayout) view.findViewById(R.id.rl_mark);
            mLlTitle = (LinearLayout) view.findViewById(R.id.rl_title);
            mImvRead = (ImageView) view.findViewById(R.id.imv_new_corner);
            mRlmain = (RelativeLayout) view.findViewById(R.id.rl_main);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface onActionClick {
        void onItemClick(NewsItem newsItem, int pos);

    }

    public void setActionClick(onActionClick action) {
        mActionClick = action;
    }

}


