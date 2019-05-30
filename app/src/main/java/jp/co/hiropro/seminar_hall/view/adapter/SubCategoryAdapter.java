package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Tuấn Sơn on 24/7/2017.
 */

public class SubCategoryAdapter extends ArrayAdapter<SubCategory> {
    private Context mContext;
    boolean visibleFavorite = false;

    public SubCategoryAdapter(@NonNull Context context) {
        super(context, -1);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_subcategory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.bindData(getItem(position));
        return convertView;
    }

    public void setVisibleFavoriteImage(boolean visible) {
        visibleFavorite = visible;
    }

    private class ViewHolder {
        ImageView ivSubcategory, ivFavoriteStatus;
        TextViewApp tvDescription, tvReadmore;
        ProgressBar loadingView;
        View view;

        public ViewHolder(View view) {
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
            GlideApp.with(mContext).load(item.getImage()).override(587,176).listener(new RequestListener<Drawable>() {
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
