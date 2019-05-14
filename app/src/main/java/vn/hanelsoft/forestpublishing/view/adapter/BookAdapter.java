package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

import butterknife.ButterKnife;
import vn.hanelsoft.forestpublishing.GlideApp;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.BaseActivity;
import vn.hanelsoft.forestpublishing.model.SubCategory;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.util.Utils;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 24/7/2017.
 */

public class BookAdapter extends HFRecyclerView<SubCategory> {
    private Context mContext;
    boolean visibleFavorite = false;

    public BookAdapter(List<SubCategory> data, boolean withHeader, boolean withFooter, Context context) {
        super(data, withHeader, withFooter);
        mContext = context;
    }

    public void setVisibleFavoriteImage(boolean visible) {
        visibleFavorite = visible;
    }


    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_subcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return new HeaderViewHolder(inflater.inflate(R.layout.header_subcategory_detail, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new BookAdapter.FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
//            if (mUrlBanner != null && mUrlBanner.length() > 0) {
//                viewHolder.mImvBanner.setVisibility(View.VISIBLE);
//                viewHolder.mPrgLoading.setVisibility(View.VISIBLE);
//                viewHolder.bindData(mUrlBanner);
//            } else {
//                viewHolder.mImvBanner.setVisibility(View.GONE);
//                viewHolder.mPrgLoading.setVisibility(View.GONE);
//            }
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubcategory, ivFavoriteStatus;
        TextViewApp tvDescription, tvReadmore;
        ProgressBar loadingView;
        View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
            this.view.setFocusable(true);
            ivSubcategory = (ImageView) view.findViewById(R.id.iv_sub_category);
            ivFavoriteStatus = (ImageView) view.findViewById(R.id.iv_favorite_status);
            tvDescription = (TextViewApp) view.findViewById(R.id.tv_description);
            tvDescription.setFocusable(true);
            loadingView = (ProgressBar) view.findViewById(R.id.loading_view);
            tvReadmore = (TextViewApp) view.findViewById(R.id.tv_readmore);
            float widthOfView = BaseActivity.screenSize.x - (2 * Utils.convertDpToPx(mContext, 10));
            int heightOfView = (int) ((widthOfView * 352) / 1174);
            ivSubcategory.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));
            ivFavoriteStatus.setVisibility(visibleFavorite ? View.VISIBLE : View.GONE);

        }

        void bindData(final SubCategory item) {
            loadingView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext).load(item.getImage()).override(587, 176).listener(new RequestListener<Drawable>() {
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
                        notifyDataSetChanged();
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        ////
        TextViewApp tvDescription;
        private ImageView ivBanner, ivFavoriteTag;
        private ProgressBar loadingView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvDescription = (TextViewApp) itemView.findViewById(R.id.tv_description);
            ivBanner = (ImageView) itemView.findViewById(R.id.iv_banner);
            loadingView = (ProgressBar) itemView.findViewById(R.id.loading);
            ivFavoriteTag = (ImageView) itemView.findViewById(R.id.iv_favorite_tag);
            int heightOfView = AppUtils.getScreenWidth() * 712 / 1242;
            ivBanner.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightOfView));
        }

        public void bindData(String url) {
            GlideApp.with(mView.getContext()).load(url).listener(new RequestListener<Drawable>() {
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
            }).into(ivBanner);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
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
