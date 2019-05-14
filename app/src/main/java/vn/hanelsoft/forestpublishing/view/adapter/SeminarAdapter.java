package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import vn.hanelsoft.forestpublishing.GlideApp;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.BaseActivity;
import vn.hanelsoft.forestpublishing.model.SubCategory;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class SeminarAdapter extends HFRecyclerView<SubCategory> {
    private Context mContext;

    public SeminarAdapter(List<SubCategory> data, boolean withHeader, boolean withFooter, Context context) {
        super(data, withHeader, withFooter);
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_seminar_top, parent, false);
        int widthOfView = (BaseActivity.screenSize.x / 3 * 2);
        int heightOfView = (widthOfView / 3);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = widthOfView;
        params.height = heightOfView;
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new SeminarAdapter.FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubcategory;
        ProgressBar mPrgLoading;

        public ViewHolder(View view) {
            super(view);
            ivSubcategory = view.findViewById(R.id.imv_seminar);
            mPrgLoading = view.findViewById(R.id.prg_loading);
            int widthOfView = (BaseActivity.screenSize.x / 3 * 2);
            int heightOfView = (widthOfView / 3);
            ivSubcategory.setLayoutParams(new RelativeLayout.LayoutParams(widthOfView, heightOfView));
        }

        void bindData(final SubCategory item) {
            mPrgLoading.setVisibility(View.VISIBLE);
            GlideApp.with(mContext).asBitmap().load(item.getImage()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ivSubcategory.setImageBitmap(resource);
                    mPrgLoading.setVisibility(View.GONE);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    ivSubcategory.setImageResource(R.mipmap.imv_default);
                    mPrgLoading.setVisibility(View.GONE);
                }
            });
        }
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
