package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.SearchResult;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 21/7/2017.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTENT_FREE = 1;
    private static final int CONTENT_FEE = 2;
    private static final int CONTENT_SPECIAL = 3;

    Context mContext;
    List<SearchResult> mList = new ArrayList<>();

    public SearchResultAdapter(Context context, List<SearchResult> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {   ////SubCategory
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_type_subcategory, parent, false);
            return new SubCateViewHolder(view);
        } else if (viewType == 1) {    /////Sub-SubCategory
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_type_sub_subcategory, parent, false);
            return new SubSubCateViewHolder(view);
        } else {    ////Video
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_type_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubCateViewHolder) {
            ((SubCateViewHolder) holder).bindData(mList.get(position));
        } else if (holder instanceof SubSubCateViewHolder) {
            ((SubSubCateViewHolder) holder).bindData(mList.get(position), position);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bindData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_content)
        ImageView ivContent;
        @BindView(R.id.tv_special)
        TextViewApp tvSpecial;
        @BindView(R.id.tv_title)
        TextViewApp tvTitle;
        @BindView(R.id.tv_category)
        TextViewApp tvCategory;
        @BindView(R.id.tv_sub_category)
        TextViewApp tvSubCategory;
        @BindView(R.id.tv_sub_sub_category)
        TextViewApp tvSubSubCategory;
        @BindView(R.id.tv_price)
        TextViewApp tvPrice;
        @BindView(R.id.loading)
        ProgressBar loadingView;
        @BindView(R.id.iv_cate)
        ImageView ivCate;
        @BindView(R.id.iv_sub_cate)
        ImageView ivSubCate;
        @BindView(R.id.tv_status_premium)
        TextViewApp tvStatusPremium;
        @BindView(R.id.imv_point)
        ImageView mImvPoint;
        @BindView(R.id.ll_point)
        LinearLayout mLlPoint;

        View view;

        VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
        }

        void bindData(final SearchResult item) {
            User user = User.getInstance().getCurrentUser();
            GlideApp.with(mContext).load(item.getSearchVideo().getThumbnail()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ivContent.setImageResource(R.mipmap.imv_default);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivContent);

            if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_FREE || item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_FREE_TYPE_2) {
                mImvPoint.setVisibility(View.GONE);
                if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_type_content));
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
                    tvStatusPremium.setVisibility(View.GONE);
                } else {
                    tvPrice.setSelected(true);
                    tvPrice.setText(mContext.getString(R.string.txt_free));
                    tvSpecial.setVisibility(View.GONE);
                    tvStatusPremium.setVisibility(View.GONE);
//                    tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    mLlPoint.setSelected(true);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                }
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_LIMITED_FREE) {
                mImvPoint.setVisibility(View.GONE);
                tvStatusPremium.setVisibility(View.VISIBLE);
                tvSpecial.setVisibility(View.GONE);
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_type_content));
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
                    mLlPoint.setSelected(false);
                    tvStatusPremium.setVisibility(View.GONE);
                } else {
                    if (!user.isPremiumUser()) {
                        tvSpecial.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        tvSpecial.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                        tvPrice.setSelected(true);
                        mLlPoint.setSelected(true);
                        tvPrice.setText(mContext.getString(R.string.txt_free));
                        tvStatusPremium.setText("期間限定");
                        tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                    } else {
                        mImvPoint.setVisibility(View.VISIBLE);
                        // Dinhdv add with case user is free android video not paid.
                        tvStatusPremium.setVisibility(user.isSkipUser() ? View.GONE : View.VISIBLE);
                        tvStatusPremium.setText("会員無料");
                        tvStatusPremium.setBackgroundColor(Color.parseColor("#686868"));
                        tvPrice.setSelected(false);
//                        tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        mLlPoint.setSelected(false);
                        tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                        tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
                        if (!user.isSkipUser())
                            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_PAID) {
                mImvPoint.setVisibility(View.GONE);
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_type_content));
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
                    mLlPoint.setSelected(false);
                    tvStatusPremium.setVisibility(View.GONE);
                    if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    mImvPoint.setVisibility(View.VISIBLE);
                    if (user.isPremiumUser()) {
                        tvStatusPremium.setVisibility(View.VISIBLE);
                        tvStatusPremium.setBackgroundColor(Color.parseColor("#686868"));
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
//                        tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                        tvPrice.setSelected(false);
                        mLlPoint.setSelected(false);
                    } else {
//                        tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                        tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                        tvPrice.setSelected(false);
                        mLlPoint.setSelected(false);
                        tvStatusPremium.setVisibility(View.GONE);
                        tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
                        if (!user.isSkipUser())
                            if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                                tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }

                tvPrice.setSelected(false);
                mLlPoint.setSelected(false);
                tvSpecial.setVisibility(View.GONE);
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_SPECIAL) {
                mImvPoint.setVisibility(View.GONE);
                tvStatusPremium.setVisibility(View.GONE);
                tvPrice.setSelected(false);
                mLlPoint.setSelected(false);
                if (item.getSearchVideo().getIsBought() == 1) {
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setSelected(false);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_type_content));
                    if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    mImvPoint.setVisibility(View.VISIBLE);
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                    tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
                    if (!user.isSkipUser())
                        if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                            tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                tvSpecial.setVisibility(View.VISIBLE);
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_LIMITED_SPECIAL) {
                mImvPoint.setVisibility(View.GONE);
                tvSpecial.setVisibility(View.VISIBLE);
                if (item.getSearchVideo().getIsBought() == 1) {
                    tvStatusPremium.setVisibility(View.GONE);
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_type_content));
                    mLlPoint.setSelected(false);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_type_content));
                    if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    tvStatusPremium.setVisibility(View.VISIBLE);
                    tvStatusPremium.setText("期間限定");
                    tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                    tvPrice.setSelected(true);
                    tvPrice.setText(mContext.getString(R.string.txt_free));
//                    tvPrice.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_price));
                    mLlPoint.setSelected(true);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_price));
                }
            }
            tvCategory.setText(item.getSearchSubCategory().getCategory());
            tvSubCategory.setText(item.getSearchSubCategory().getSubCategory());

            if (TextUtils.isEmpty(item.getSearchSubCategory().getSubCategory())
                    && TextUtils.isEmpty(item.getSearchSubCategory().getSubCategory())) {
                ivCate.setVisibility(View.GONE);
            } else {
                ivCate.setVisibility(View.VISIBLE);
            }
            ivSubCate.setVisibility(item.getSearchSubCategory().getBook() == null || item.getSearchSubCategory().getBook().length() == 0 ? View.GONE : View.VISIBLE);
            tvSubSubCategory.setText(item.getSearchSubCategory().getBook());
            tvTitle.setText(item.getTitle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onCategoryClick != null)
//                        onCategoryClick.onCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvSubCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onSubCategoryClick != null)
//                        onSubCategoryClick.onSubCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvSubSubCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onSubSubCategoryClick != null)
//                        onSubSubCategoryClick.onSubSubCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });

        }

    }

    class SubCateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_sub_category)
        CircleImageView ivSubCategory;
        @BindView(R.id.tv_title)
        TextViewApp tvTitle;
        @BindView(R.id.tv_category)
        TextViewApp tvCategory;
        View view;

        SubCateViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(final SearchResult item) {
            GlideApp.with(mContext).load(item.getSearchSubCategory().getImage()).into(ivSubCategory);
            tvTitle.setText(item.getTitle());
            tvCategory.setText(item.getSearchSubCategory().getCategory());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onCategoryClick != null)
//                        onCategoryClick.onCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
        }
    }

    class SubSubCateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_content)
        ImageView ivContent;
        @BindView(R.id.tv_title)
        TextViewApp tvTitle;
        @BindView(R.id.tv_category)
        TextViewApp tvCategory;
        @BindView(R.id.tv_sub_category)
        TextViewApp tvSubCategory;
        @BindView(R.id.iv_status_favorite)
        ImageView ivStatusFavorite;
        View view;
        @BindView(R.id.imv_arrow_line)
        ImageView mIMvArrowLine;

        SubSubCateViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(final SearchResult item, final int position) {
            GlideApp.with(mContext).load(item.getSearchSubCategory().getImage()).into(ivContent);
            tvTitle.setText(item.getTitle());
            tvCategory.setText(item.getSearchSubCategory().getCategory());
            mIMvArrowLine.setVisibility(item.getSearchSubCategory().getSubCategory().length() > 0 ? View.VISIBLE : View.GONE);
            tvSubCategory.setText(item.getSearchSubCategory().getSubCategory());
            ivStatusFavorite.setSelected(item.getSearchSubCategory().getIsFavorite() == 1);
            ivStatusFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.getInstance().isSkipUser()) {
                        Toast.makeText(mContext, mContext.getString(R.string.msg_need_login), Toast.LENGTH_SHORT).show();
                    } else {
                        if (onUnfavoriteListener != null) {
                            onUnfavoriteListener.onUnfavorite(item, position);
                        }
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onCategoryClick != null)
//                        onCategoryClick.onCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
            tvSubCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onSubCategoryClick != null)
//                        onSubCategoryClick.onSubCategoryClick(item);
                    if (onItemSearchClick != null)
                        onItemSearchClick.onClick(item);
                }
            });
        }
    }

    public void setOnUnfavoriteListener(OnUnfavoriteListener onRemoveFavorite) {
        this.onUnfavoriteListener = onRemoveFavorite;
    }

    OnUnfavoriteListener onUnfavoriteListener;

    public interface OnUnfavoriteListener {
        void onUnfavorite(SearchResult item, int position);
    }

    OnItemSearchClick onItemSearchClick;

    public void setOnItemSearchClick(OnItemSearchClick onItemSearchClick) {
        this.onItemSearchClick = onItemSearchClick;
    }

    public interface OnItemSearchClick {
        void onClick(SearchResult item);
    }

    public void setOnCategoryClick(OnCategoryClick onCategoryClick) {
        this.onCategoryClick = onCategoryClick;
    }

    OnCategoryClick onCategoryClick;

    public interface OnCategoryClick {
        void onCategoryClick(SearchResult item);
    }

    public void setOnSubCategoryClick(OnSubCategoryClick onSubCategoryClick) {
        this.onSubCategoryClick = onSubCategoryClick;
    }

    OnSubCategoryClick onSubCategoryClick;

    public interface OnSubCategoryClick {
        void onSubCategoryClick(SearchResult item);
    }

    public void setOnSubSubCategoryClick(OnSubSubCategoryClick onSubSubCategoryClick) {
        this.onSubSubCategoryClick = onSubSubCategoryClick;
    }

    OnSubSubCategoryClick onSubSubCategoryClick;

    public interface OnSubSubCategoryClick {
        void onSubSubCategoryClick(SearchResult item);
    }
}
