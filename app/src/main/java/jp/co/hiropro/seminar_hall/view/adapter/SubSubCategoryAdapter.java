package jp.co.hiropro.seminar_hall.view.adapter;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 31/8/2017.
 */

public class SubSubCategoryAdapter extends HFRecyclerView<SubCategory> {

    private String image, description;

    public SubSubCategoryAdapter(List<SubCategory> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
    }

    public void setDataHeader(String image, String description) {
        this.image = image;
        this.description = description;
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_subcategory, parent, false));
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
            itemViewHolder.bindData(getItem(position),position);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (!TextUtils.isEmpty(image) && !TextUtils.isEmpty(description))
                headerViewHolder.bindData(image, description);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubcategory, ivFavoriteStatus;
        TextViewApp tvDescription, tvReadmore;
        ProgressBar loadingView;
        View view;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            this.view.setFocusable(true);
            ivSubcategory = (ImageView) view.findViewById(R.id.iv_sub_category);
            ivFavoriteStatus = (ImageView) view.findViewById(R.id.iv_favorite_status);
            tvDescription = (TextViewApp) view.findViewById(R.id.tv_description);
            tvDescription.setFocusable(true);
            loadingView = (ProgressBar) view.findViewById(R.id.loading_view);
            tvReadmore = (TextViewApp) view.findViewById(R.id.tv_readmore);
            float widthOfView = BaseActivity.screenSize.x - (2 * Utils.convertDpToPx(view.getContext(), 10));
            int heightOfView = (int) ((widthOfView * 352) / 1174);
            ivSubcategory.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));
        }

        void bindData(final SubCategory item,final int position) {
            loadingView.setVisibility(View.VISIBLE);
            GlideApp.with(view.getContext()).load(item.getImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ivSubcategory.setImageResource(R.mipmap.imv_default);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivSubcategory);
            tvDescription.setText(item.getDescription());
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvDescription.post(new Runnable() {
                @Override
                public void run() {
                    if (tvDescription.getLineCount() > 5) {
                        tvDescription.setMaxLines(5);
                        tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                        tvReadmore.setVisibility(View.VISIBLE);
                    } else tvReadmore.setVisibility(View.GONE);
                }
            });
            ivFavoriteStatus.setSelected(item.getIsFavorite() == 1);
            ivFavoriteStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFavoriteListener != null) {
                        item.setIsFavorite(item.getIsFavorite() == 1 ? 0 : 1);
                        onFavoriteListener.onFavorite(item);
                        notifyItemChanged(position);

                    }

                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null)
                        onItemClick.onItemClick(item);
                }
            });
            tvReadmore.setTag(true);
            tvReadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isReadmore = (boolean) tvReadmore.getTag();
                    if (isReadmore) {
                        tvReadmore.setTag(false);
                        tvDescription.setMaxLines(Integer.MAX_VALUE);
                        tvReadmore.setText("短く表示");
                    } else {
                        tvReadmore.setTag(true);
                        tvDescription.setMaxLines(5);
                        tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                        tvReadmore.setText("もっと見る");
                    }
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubCategoryDetail;
        TextViewApp tvDescription;
        ProgressBar loading;
        View view;

        HeaderViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivSubCategoryDetail = (ImageView) itemView.findViewById(R.id.iv_banner);
            tvDescription = (TextViewApp) itemView.findViewById(R.id.tv_description);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            int heightOfView = BaseActivity.screenSize.x * 712 / 1242;
            ivSubCategoryDetail.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));

        }

        void bindData(String image, String description) {
            tvDescription.setText(description);
            GlideApp.with(view.getContext()).load(image).override(621, 359).listener(new RequestListener<Drawable>() {
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
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
            itemView.setPadding(0, 0, 0, 0);
        }
    }

    public void setOnFavoriteListener(FavoriteListener onFavoriteListener) {
        this.onFavoriteListener = onFavoriteListener;
    }

    FavoriteListener onFavoriteListener;

    public interface FavoriteListener {
        void onFavorite(SubCategory subCategory);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    OnItemClick onItemClick;

    public interface OnItemClick {
        void onItemClick(SubCategory subCategory);
    }
}
