package jp.co.hiropro.seminar_hall.view.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Favorite;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 25/7/2017.
 */

public class FavoriteAdapter extends HFRecyclerView<Favorite> {
    List<Favorite> mList = new ArrayList<>();

    public FavoriteAdapter(List<Favorite> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mList.addAll(data);
    }

    @Override
    protected ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {

        return new ItemViewHolder(inflater.inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    protected ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bindData(getItem(position), position);
        }
    }

    class ItemViewHolder extends ViewHolder {
        @BindView(R.id.iv_sub_category)
        CircleImageView ivSubCategory;
        @BindView(R.id.tv_title)
        TextViewApp tvTitle;
        @BindView(R.id.tv_category)
        TextViewApp tvCategory;
        @BindView(R.id.iv_status_favorite)
        ImageView ivStatusFavorite;
        @BindView(R.id.tv_name_sub)
        TextViewApp mTvNameSub;
        View view;
        @BindView(R.id.imv_next)
        ImageView mImvNext;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(final Favorite item, final int position) {
            Glide.with(view.getContext()).asBitmap().load(item.getImage()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ivSubCategory.setImageBitmap(resource);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    ivSubCategory.setImageResource(R.mipmap.imv_default);
                }
            });
            tvTitle.setText(item.getTitle());
            tvCategory.setText(item.getCateTitle());
            mTvNameSub.setText(item.getSubcateTitle());
            ivStatusFavorite.setSelected(item.isFavorite());
            mImvNext.setVisibility(item.getSubcateTitle().length() > 0 ? View.VISIBLE : View.GONE);
            ivStatusFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUnfavoriteListener != null) {
                        onUnfavoriteListener.onUnfavorite(item, position);
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
            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onCategoryClick != null)
//                        onCategoryClick.onItemClick(item);
                    if (onItemClick != null)
                        onItemClick.onItemClick(item);
                }
            });
            mTvNameSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (onSubCategoryClick != null)
//                        onSubCategoryClick.onItemClick(item);
                    if (onItemClick != null)
                        onItemClick.onItemClick(item);
                }
            });
        }
    }

    class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnUnfavoriteListener(OnUnfavoriteListener onRemoveFavorite) {
        this.onUnfavoriteListener = onRemoveFavorite;
    }

    OnUnfavoriteListener onUnfavoriteListener;

    public interface OnUnfavoriteListener {
        void onUnfavorite(Favorite item, int position);
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

    OnItemClick onItemClick, onCategoryClick, onSubCategoryClick;

    public interface OnItemClick {
        void onItemClick(Favorite item);
    }
}
