package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 24/7/2017.
 */

public class RecyclerSubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    boolean visibleFavorite = false;
    private static final int FOOTER_VIEW = 1;
    private ArrayList<SubCategory> mListSub = new ArrayList<>();
    private User mUser = null;

    public RecyclerSubCategoryAdapter(Context context, ArrayList<SubCategory> list) {
        this.mContext = context;
        this.mListSub = list;
    }

    public RecyclerSubCategoryAdapter(Context context, ArrayList<SubCategory> list, User user) {
        this.mContext = context;
        this.mListSub = list;
        this.mUser = user;
    }

    public void setVisibleFavoriteImage(boolean visible) {
        visibleFavorite = visible;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == FOOTER_VIEW) {
//            View view = LayoutInflater.from(mContext).inflate(R.layout.footer_copy_right, parent, false);
//            return new FooterViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(mContext).inflate(R.layout.item_subcategory, parent, false);
//            return new SubViewHolder(view);
//        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_subcategory, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubViewHolder) {
            SubCategory sub = mListSub.get(position);
            if (sub != null)
                ((SubViewHolder) holder).bindData(sub);
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder vh = (FooterViewHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mListSub.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mListSub == null) {
            return 0;
        }
//        if (mListSub.size() == 0) {
//            //Return 1 here to show nothing
//            return 1;
//        }
        // Add extra view to show the footer view
        return mListSub.size();
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }


    private class SubViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubcategory, ivFavoriteStatus;
        TextViewApp tvReadmore, tvDescription;
        ProgressBar loadingView;
        View view;
        RelativeLayout mRlView;

        public SubViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            this.view.setFocusable(true);
            ivSubcategory = (ImageView) itemView.findViewById(R.id.iv_sub_category);
            ivFavoriteStatus = (ImageView) itemView.findViewById(R.id.iv_favorite_status);
            tvDescription = (TextViewApp) itemView.findViewById(R.id.tv_description);
            mRlView = (RelativeLayout) itemView.findViewById(R.id.layout_image);
            tvDescription.setFocusable(true);
            loadingView = (ProgressBar) itemView.findViewById(R.id.loading_view);
            tvReadmore = (TextViewApp) itemView.findViewById(R.id.tv_readmore);
            float widthOfView = BaseActivity.screenSize.x - (2 * Utils.convertDpToPx(mContext, 10));
            int heightOfView = (int) ((widthOfView * 352) / 1174);
            ivSubcategory.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));
            ivFavoriteStatus.setVisibility(visibleFavorite ? View.VISIBLE : View.GONE);
        }

        void bindData(final SubCategory item) {
            loadingView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext).asBitmap().load(item.getImage()).override(587, 176).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ivSubcategory.setImageBitmap(resource);
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    ivSubcategory.setImageResource(R.mipmap.imv_default);
                    loadingView.setVisibility(View.GONE);
                }
            });
//            GlideApp.with(mContext).load(item.getImage()).override(587, 176).listener(new RequestListener<Drawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    ivSubcategory.setImageResource(R.mipmap.imv_default);
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    loadingView.setVisibility(View.GONE);
//                    return false;
//                }
//            }).into(ivSubcategory);
            tvDescription.setText(item.getDescription());
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvDescription.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    tvDescription.getViewTreeObserver().removeOnPreDrawListener(this);
                    int lineCount = tvDescription.getLineCount();
                    if (tvDescription.getLineCount() > 5) {
                        tvDescription.setMaxLines(5);
                        tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                        tvReadmore.setVisibility(View.VISIBLE);
                    } else {
                        tvReadmore.setVisibility(View.GONE);
                    }
                    tvReadmore.setVisibility(lineCount > 5 ? View.VISIBLE : View.GONE);
                    return true;
                }
            });
            ivFavoriteStatus.setSelected(item.getIsFavorite() == 1);
            ivFavoriteStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFavoriteListener != null) {
                        if (mUser != null) {
                            if (mUser.isSkipUser()) {
                                Toast.makeText(mContext, mContext.getString(R.string.msg_need_login), Toast.LENGTH_SHORT).show();
                            } else {
                                item.setIsFavorite(item.getIsFavorite() == 1 ? 0 : 1);
                                onFavoriteListener.onFavorite(item);
                                notifyDataSetChanged();
                            }
                        } else {
                            item.setIsFavorite(item.getIsFavorite() == 1 ? 0 : 1);
                            onFavoriteListener.onFavorite(item);
                            notifyDataSetChanged();
                        }
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
            ivSubcategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null)
                        onItemClick.onItemClick(item);
                }
            });
            mRlView.setOnClickListener(new View.OnClickListener() {
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
