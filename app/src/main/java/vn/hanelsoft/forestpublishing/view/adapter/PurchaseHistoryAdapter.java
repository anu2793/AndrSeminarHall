package vn.hanelsoft.forestpublishing.view.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 8/8/2017.
 */

public class PurchaseHistoryAdapter extends HFRecyclerView<SearchResult> {
    List<SearchResult> mList = new ArrayList<>();

    public PurchaseHistoryAdapter(List<SearchResult> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mList.addAll(data);
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
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
            ItemViewHolder holderView = (ItemViewHolder) holder;
            holderView.bindData(getItem(position));
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
        ImageView mImvSubCate;
        @BindView(R.id.imv_arrow_sub_cate)
        ImageView mImvBook;
        View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(SearchResult item) {
            GlideApp.with(view.getContext()).load(item.getSearchVideo().getThumbnail()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivContent);
            tvPrice.setSelected(false);
            tvPrice.setText("購入済み");
            tvTitle.setText(item.getTitle());
            if (item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_SPECIAL
                    || item.getSearchVideo().getVideoType() == VideoDetail.VIDEO_LIMITED_SPECIAL) {
                tvSpecial.setVisibility(View.VISIBLE);
            } else {
                tvSpecial.setVisibility(View.GONE);
            }
            tvCategory.setText(item.getSearchSubCategory().getCategory());
            mImvSubCate.setVisibility(item.getSearchSubCategory().getSubCategory().length() > 0 ? View.VISIBLE : View.GONE);
            tvSubCategory.setText(item.getSearchSubCategory().getSubCategory());
            mImvBook.setVisibility(item.getSearchSubCategory().getBook().length() > 0 ? View.VISIBLE : View.GONE);
            tvSubSubCategory.setText(item.getSearchSubCategory().getBook());
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
