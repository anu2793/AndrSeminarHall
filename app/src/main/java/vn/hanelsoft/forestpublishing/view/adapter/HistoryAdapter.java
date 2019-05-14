package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.hanelsoft.forestpublishing.GlideApp;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.SearchResult;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.Utils;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 8/8/2017.
 */

public class HistoryAdapter extends HFRecyclerView<SearchResult> {
    List<SearchResult> mList = new ArrayList<>();
    private Context mContext;

    public HistoryAdapter(List<SearchResult> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mList.addAll(data);
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        mContext = parent.getContext();
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
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
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.tv_status_premium)
        TextViewApp tvStatusPremium;
        @BindView(R.id.loading)
        ProgressBar loadingView;
        @BindView(R.id.imv_arrow_cate)
        ImageView mImvCate;
        @BindView(R.id.imv_arrow_sub_cate)
        ImageView mImvSubCate;
        @BindView(R.id.imv_point)
        ImageView mImvPoint;
        @BindView(R.id.ll_point)
        LinearLayout mLlPoint;
        View view;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(final SearchResult item) {
            User user = User.getInstance().getCurrentUser();
            GlideApp.with(view.getContext()).load(item.getSearchVideo().getThumbnail()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    ivContent.setImageResource(R.mipmap.imv_default);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivContent);
            tvTitle.setText(item.getTitle());
            if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_FREE || item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_FREE_TYPE_2) {
                mImvPoint.setVisibility(View.GONE);
                if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_type_content));
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
                    mLlPoint.setSelected(false);
                    tvStatusPremium.setVisibility(View.GONE);
                } else {
                    tvPrice.setSelected(true);
//                    tvPrice.setText("無料");
                    tvPrice.setText(mContext.getString(R.string.txt_free));
                    tvSpecial.setVisibility(View.GONE);
                    tvStatusPremium.setVisibility(View.GONE);
//                    tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    mLlPoint.setSelected(true);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                }
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_LIMITED_FREE) {
                mImvPoint.setVisibility(View.GONE);
                tvStatusPremium.setVisibility(View.VISIBLE);
                tvSpecial.setVisibility(View.GONE);
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_type_content));
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
                    mLlPoint.setSelected(false);
                    tvStatusPremium.setVisibility(View.GONE);
                } else {
                    if (!user.isPremiumUser()) {
                        tvSpecial.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        tvSpecial.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                        tvPrice.setSelected(true);
//                        tvPrice.setText("無料");
                        tvPrice.setText(mContext.getString(R.string.txt_free));
                        tvStatusPremium.setText("期間限定");
                        mLlPoint.setSelected(true);
                        tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                    } else {
                        mImvPoint.setVisibility(View.VISIBLE);
                        tvStatusPremium.setVisibility(user.isSkipUser() ? View.GONE : View.VISIBLE);
                        tvStatusPremium.setText("会員無料");
                        tvStatusPremium.setBackgroundColor(Color.parseColor("#686868"));
                        tvPrice.setSelected(false);
//                        tvPrice.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        mLlPoint.setSelected(false);
                        tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                        tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
                        if (!user.isSkipUser())
                            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }

            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_PAID) {
                mImvPoint.setVisibility(View.GONE);
                if (item.getSearchVideo().getIsBought() == 1) {
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_type_content));
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
//                        tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                        tvPrice.setSelected(false);
                    } else {
//                        tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                        tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                        tvPrice.setSelected(false);
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
                if (item.getSearchVideo().getIsBought() == 1) {
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setSelected(false);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_type_content));
                    if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    mImvPoint.setVisibility(View.VISIBLE);
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                    tvPrice.setText(Utils.formatPrice(item.getSearchVideo().getPrice()));
                    if (!user.isSkipUser())
                        if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                            tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                tvSpecial.setVisibility(View.VISIBLE);
            } else if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_LIMITED_SPECIAL) {
                tvSpecial.setVisibility(View.VISIBLE);
                if (item.getSearchVideo().getIsBought() == 1) {
                    tvStatusPremium.setVisibility(View.GONE);
                    tvPrice.setText("購入済み");
                    tvPrice.setSelected(false);
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_type_content));
                    mLlPoint.setSelected(false);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_type_content));
                    if ((tvPrice.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    tvStatusPremium.setVisibility(View.VISIBLE);
                    tvStatusPremium.setText("期間限定");
                    tvStatusPremium.setBackgroundColor(Color.parseColor("#f97e59"));
                    tvPrice.setSelected(true);
//                    tvPrice.setText("無料");
                    tvPrice.setText(mContext.getString(R.string.txt_free));
//                    tvPrice.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    mLlPoint.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_price));
                    mLlPoint.setSelected(true);
                    tvPrice.setTextColor(ContextCompat.getColorStateList(view.getContext(), R.color.color_price));
                }
            }

            tvCategory.setText(item.getSearchSubCategory().getCategory());
            mImvCate.setVisibility(item.getSearchSubCategory().getSubCategory().length() > 0 ? View.VISIBLE : View.GONE);
            tvSubCategory.setText(item.getSearchSubCategory().getSubCategory());
            mImvSubCate.setVisibility(item.getSearchSubCategory().getBook().length() > 0 ? View.VISIBLE : View.GONE);
            tvSubSubCategory.setText(item.getSearchSubCategory().getBook());

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onItemClick != null)
//                        onItemClick.onItemClick(item);
//                }
//            });
//            tvCategory.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onCategoryClick != null)
//                        onCategoryClick.onItemClick(item);
//                }
//            });
//            tvSubCategory.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onSubCategoryClick != null)
//                        onSubCategoryClick.onItemClick(item);
//                }
//            });
//            tvSubSubCategory.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onSubSubCategoryClick != null)
//                        onSubSubCategoryClick.onItemClick(item);
//                }
//            });
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnCategoryClick(OnItemClick onCategoryClick) {
        this.onCategoryClick = onCategoryClick;
    }

    public void setOnSubCategoryClick(OnItemClick onSubCategoryClick) {
        this.onSubCategoryClick = onSubCategoryClick;
    }

    public void setOnSubSubCategoryClick(OnItemClick onSubSubCategoryClick) {
        this.onSubSubCategoryClick = onSubSubCategoryClick;
    }

    OnItemClick onItemClick, onCategoryClick, onSubCategoryClick, onSubSubCategoryClick;

    public interface OnItemClick {
        void onItemClick(SearchResult item);
    }

}
