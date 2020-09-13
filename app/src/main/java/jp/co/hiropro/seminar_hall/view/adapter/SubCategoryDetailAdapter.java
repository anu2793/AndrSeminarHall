package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 24/7/2017.
 */

public class SubCategoryDetailAdapter extends HFRecyclerView<VideoDetail> {
    private final int VIDEO_PAID = 0;
    private final int VIDEO_FREE = 1;
    private final int VIDEO_FREE_TYPE_2 = 2;
    private final int VIDEO_LIMITED_FREE = 3;
    private final int VIDEO_SPECIAL = 4;
    private final int VIDEO_LIMITED_SPCIAL = 5;

    private Context mContext;
    private String image, description;
    private int favoriteStatus;
    private User mUser;
    private boolean mIsShowTitle = false;

    public SubCategoryDetailAdapter(Context context, List<VideoDetail> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mContext = context;
    }

    public void setDataHeader(String image, int favoriteStatus, String description) {
        this.image = image;
        this.favoriteStatus = favoriteStatus;
        this.description = description;
    }

    public void setDataHeader(String image, int favoriteStatus, String description, User user, boolean isShow) {
        this.image = image;
        this.favoriteStatus = favoriteStatus;
        this.description = description;
        this.mUser = user;
        this.mIsShowTitle = isShow;
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_subcategory_detail, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return new HeaderViewHolder(inflater.inflate(R.layout.header_subcategory_detail, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bindData(getItem(position));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (!TextUtils.isEmpty(image) && !TextUtils.isEmpty(description))
                headerViewHolder.bindData(image, favoriteStatus, description);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubCategory , mImvPoint;
        TextViewApp tvTypeOfSubcategory, tvSpecial;
        ProgressBar loadingView;
        View view;
        LinearLayout mLlPoint;

        ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivSubCategory = itemView.findViewById(R.id.iv_sub_category);
            mLlPoint = itemView.findViewById(R.id.ll_point);
            tvTypeOfSubcategory = itemView.findViewById(R.id.tv_type_of_subcategory);
            tvSpecial = itemView.findViewById(R.id.tv_special);
            mImvPoint = itemView.findViewById(R.id.imv_point);
            loadingView = itemView.findViewById(R.id.loading_view);
            float widthOfView = BaseActivity.screenSize.x - (2 * Utils.convertDpToPx(mContext, 10));
            int heightOfView = (int) ((widthOfView * 509) / 1172);
            ivSubCategory.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));
        }

        void bindData(final VideoDetail item) {
            loadingView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext).load(item.getImage()).error(R.mipmap.imv_default).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ivSubCategory.setImageResource(R.mipmap.imv_default);
                    loadingView.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivSubCategory);
            User user = User.getInstance().getCurrentUser();
            if (item.getType() == VIDEO_FREE || item.getType() == VIDEO_FREE_TYPE_2) {
                mImvPoint.setVisibility(View.GONE);
                if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                if (item.getHasBoughtVideo() == 1) {
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                    tvTypeOfSubcategory.setText("購入済み");
                } else {
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#f97e59"));
//                    tvTypeOfSubcategory.setText("無料");
                    tvTypeOfSubcategory.setText(mContext.getString(R.string.txt_free));
                }
                tvSpecial.setVisibility(View.GONE);
            } else if (item.getType() == VIDEO_LIMITED_FREE) {
                if (item.getHasBoughtVideo() == 1) {
                    tvSpecial.setVisibility(View.GONE);
                    tvTypeOfSubcategory.setText("購入済み");
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                } else {
                    mImvPoint.setVisibility(View.GONE);
                    if (user.isPremiumUser()) {
                        tvSpecial.setText("会員無料");
                        tvSpecial.setVisibility(View.VISIBLE);
                        tvSpecial.setBackgroundColor(Color.parseColor("#3a3a3a"));
                        tvSpecial.setTextColor(Color.parseColor("#FFFFFF"));
                        tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                        mImvPoint.setVisibility(View.VISIBLE);
                        tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                        tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                        mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                    } else {
                        if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                            tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        if (item.getHasBoughtVideo() == 1) {
//                            tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                            mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                            tvTypeOfSubcategory.setText("購入済み");
                        } else {
//                            tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                            mLlPoint.setBackgroundColor(Color.parseColor("#f97e59"));
                            tvTypeOfSubcategory.setText(mContext.getString(R.string.txt_free_limited));
                        }
                        tvSpecial.setVisibility(View.GONE);
                    }
                }

            } else if (item.getType() == VIDEO_PAID) {
                mImvPoint.setVisibility(View.GONE);
                if (item.getHasBoughtVideo() == 1) {
                    tvSpecial.setVisibility(View.GONE);
                    tvTypeOfSubcategory.setText("購入済み");
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                } else {
                    mImvPoint.setVisibility(View.VISIBLE);
                    tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                    if (user.isPremiumUser()) {
                        tvSpecial.setText("会員無料");
                        tvSpecial.setVisibility(View.VISIBLE);
                        tvSpecial.setBackgroundColor(Color.parseColor("#000000"));
                        tvSpecial.setTextColor(Color.parseColor("#FFFFFF"));
                        tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                        tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                        mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                    } else {
                        tvSpecial.setVisibility(View.GONE);
//                        tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                        mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                        if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                            tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            } else if (item.getType() == VIDEO_SPECIAL) {
//                tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                tvSpecial.setVisibility(View.VISIBLE);
                tvSpecial.setText(mContext.getString(R.string.type_video_premium));
                tvSpecial.setBackgroundColor(Color.parseColor("#91c527"));

                if (item.getHasBoughtVideo() == 1) {
                    tvTypeOfSubcategory.setText("購入済み");
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                } else {
                    tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                }
                if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else if (item.getType() == VIDEO_LIMITED_SPCIAL) {
                tvSpecial.setVisibility(View.VISIBLE);
                tvSpecial.setText(mContext.getString(R.string.type_video_premium));
                tvSpecial.setBackgroundColor(Color.parseColor("#91c527"));
                if (item.getHasBoughtVideo() == 1) {
                    tvTypeOfSubcategory.setText("購入済み");
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#686868"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#686868"));
                } else {
//                    tvTypeOfSubcategory.setBackgroundColor(Color.parseColor("#f97e59"));
                    mLlPoint.setBackgroundColor(Color.parseColor("#f97e59"));
                    tvTypeOfSubcategory.setText(mContext.getString(R.string.txt_free_limited));
                }
                if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(item);
                }
            });
        }

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubCategoryDetail, ivFavoriteStatus;
        TextViewApp tvDescription, mTvTitle, mTvDescription, mTvTitleList;
        ProgressBar loading;
        View view;
        RelativeLayout mRlTeacher;
        CircleImageView mCirAvatar;


        HeaderViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivSubCategoryDetail = itemView.findViewById(R.id.iv_banner);
            ivFavoriteStatus = itemView.findViewById(R.id.iv_favorite_tag);
            tvDescription = itemView.findViewById(R.id.tv_description);
            loading = itemView.findViewById(R.id.loading);
            mRlTeacher = itemView.findViewById(R.id.rl_teacher);
            mCirAvatar = itemView.findViewById(R.id.imv_avatar);
            mTvTitle = itemView.findViewById(R.id.tv_title_teacher);
            mTvDescription = itemView.findViewById(R.id.tv_description_teacher);
            mTvTitleList = itemView.findViewById(R.id.tv_title_list);
            int heightOfView = BaseActivity.screenSize.x * 712 / 1242;
            ivSubCategoryDetail.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));

            ivFavoriteStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFavoriteListener != null)
                        onFavoriteListener.onFavoriteChange(ivFavoriteStatus);
                }
            });
            mRlTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFavoriteListener != null)
                        onFavoriteListener.onTeacherClick();
                }
            });
        }

        void bindData(String image, int favoriteStatus, String description) {
            ivFavoriteStatus.setSelected(favoriteStatus == 1);
            tvDescription.setText(description);
            GlideApp.with(mContext).load(image).override(621, 359).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loading.setVisibility(View.GONE);
                    ivSubCategoryDetail.setImageResource(R.mipmap.imv_default);
                    e.printStackTrace();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loading.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivSubCategoryDetail);
            if (mUser != null) {
                if (mUser.getAvatar().length() > 0)
//                    GlideApp.with(mCirAvatar).load(mUser.getAvatar()).into(mCirAvatar);
                    GlideApp.with(mCirAvatar).asBitmap().load(mUser.getAvatar()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mCirAvatar.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            mCirAvatar.setImageResource(R.mipmap.ic_user_default);
                        }
                    });
                mTvTitle.setText(mUser.getFullname());
                mTvDescription.setText(mUser.getProfile());
            }
            mRlTeacher.setVisibility(mUser == null ? View.GONE : View.VISIBLE);
            mTvTitleList.setVisibility(mIsShowTitle ? View.VISIBLE : View.GONE);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
            itemView.setPadding(0, 0, 0, 0);
        }
    }

    //Item click
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(VideoDetail item);
    }

    //Favorite Status
    public void setOnFavoriteListener(OnFavoriteListener onFavoriteListener) {
        this.onFavoriteListener = onFavoriteListener;
    }

    private OnFavoriteListener onFavoriteListener;

    public interface OnFavoriteListener {
        void onFavoriteChange(ImageView ivStatus);

        void onTeacherClick();
    }
}
