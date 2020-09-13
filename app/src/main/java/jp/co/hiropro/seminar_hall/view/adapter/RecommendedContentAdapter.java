package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

import static jp.co.hiropro.seminar_hall.controller.activity.BaseActivity.user;
import static jp.co.hiropro.seminar_hall.model.VideoDetail.VIDEO_FREE;
import static jp.co.hiropro.seminar_hall.model.VideoDetail.VIDEO_FREE_TYPE_2;
import static jp.co.hiropro.seminar_hall.model.VideoDetail.VIDEO_LIMITED_FREE;
import static jp.co.hiropro.seminar_hall.model.VideoDetail.VIDEO_LIMITED_SPECIAL;

/**
 * Created by Tuấn Sơn on 25/7/2017.
 */

public class RecommendedContentAdapter extends HFRecyclerView<VideoDetail> {
    private String mUrlBanner = "";
    private Context mContext;

    public void setHeaderView(String url) {
        mUrlBanner = url;
    }

    public RecommendedContentAdapter(List<VideoDetail> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        mContext = parent.getContext();
        return new ItemViewHolder(inflater.inflate(R.layout.item_recommended_free_content, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return new HeaderViewHolder(inflater.inflate(R.layout.item_recommended_free_header, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.bindData(getItem(position));
        }
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            if (mUrlBanner != null && mUrlBanner.length() > 0) {
                viewHolder.mImvBanner.setVisibility(View.VISIBLE);
                viewHolder.mPrgLoading.setVisibility(View.VISIBLE);
                viewHolder.bindData(mUrlBanner);
            } else {
                viewHolder.mImvBanner.setVisibility(View.GONE);
                viewHolder.mPrgLoading.setVisibility(View.GONE);
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFreeContent;
        TextViewApp tvTypeOfSubcategory, tvTitle, tvDescription, tvSpecial;
        View view;
        ProgressBar loadingView;

        ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivFreeContent = (ImageView) itemView.findViewById(R.id.iv_free_content);
            tvTypeOfSubcategory = (TextViewApp) itemView.findViewById(R.id.tv_type_of_subcategory);
            tvTitle = (TextViewApp) itemView.findViewById(R.id.tv_title);
            tvDescription = (TextViewApp) itemView.findViewById(R.id.tv_description);
            loadingView = (ProgressBar) itemView.findViewById(R.id.loading_view);
            tvSpecial = (TextViewApp) itemView.findViewById(R.id.tv_special);
        }

        void bindData(final VideoDetail item) {
            loadingView.setVisibility(View.VISIBLE);
            GlideApp.with(view.getContext()).load(item.getImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    ivFreeContent.setImageResource(R.mipmap.imv_default);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivFreeContent);
            if (item.getType() == VIDEO_FREE || item.getType() == VIDEO_FREE_TYPE_2) {
                tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                tvTypeOfSubcategory.setText("無料");
                tvSpecial.setVisibility(View.GONE);
            } else if (item.getType() == VIDEO_LIMITED_FREE) {
                if (user.isPremiumUser()) {
                    tvSpecial.setText("会員無料");
                    tvSpecial.setVisibility(View.VISIBLE);
                    tvSpecial.setBackgroundColor(Color.parseColor("#3a3a3a"));
                    tvSpecial.setTextColor(Color.parseColor("#FFFFFF"));
                    tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                } else {
                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                    tvTypeOfSubcategory.setText(mContext.getString(R.string.txt_free_limited));
                    tvSpecial.setVisibility(View.GONE);
                }
            } else if (item.getType() == VIDEO_LIMITED_SPECIAL) {
                tvSpecial.setVisibility(View.VISIBLE);
                tvSpecial.setText("PREMIUM");
                tvSpecial.setBackgroundColor(Color.parseColor("#91c527"));
                tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                tvTypeOfSubcategory.setText(mContext.getString(R.string.txt_free_limited));
            }

            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(item);
                }
            });
        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImvBanner;
        private ProgressBar mPrgLoading;
        private View mView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImvBanner = (ImageView) itemView.findViewById(R.id.imv_banner);
            int heightOfView = BaseActivity.screenSize.x * 712 / 1242;
            mImvBanner.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightOfView));
            mPrgLoading = (ProgressBar) itemView.findViewById(R.id.prg_loading);
        }

        public void bindData(String url) {
            GlideApp.with(mView.getContext()).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    mPrgLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mPrgLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(mImvBanner);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(VideoDetail item);
    }
}
