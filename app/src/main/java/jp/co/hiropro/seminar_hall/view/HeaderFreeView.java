package jp.co.hiropro.seminar_hall.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppUtils;


public class HeaderFreeView extends FrameLayout {
    TextViewApp tvDescription, mTvTitle;
    ImageView ivBanner, ivFavoriteTag;
    ProgressBar loadingView;

    public HeaderFreeView(Context context) {
        this(context, null);
    }

    public HeaderFreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderFreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.view_free_header, this);
        tvDescription = ViewUtils.findView(this, R.id.tv_description);
        ivBanner = ViewUtils.findView(this, R.id.iv_banner);
        loadingView = ViewUtils.findView(this, R.id.loading);
        ivFavoriteTag = ViewUtils.findView(this, R.id.iv_favorite_tag);
        mTvTitle = ViewUtils.findView(this, R.id.tv_title);
        ivFavoriteTag.setVisibility(View.GONE);
    }

    public void setDataToHeader(Context context, String url) {
//        tvDescription.setText(top.optString("description"));
//        mTvTitle.setText(top.optString("title"));
        int heightOfView = AppUtils.getScreenWidth() * 712 / 1242;
        ivBanner.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightOfView));
        Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                ivBanner.setImageResource(R.mipmap.imv_default);
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
