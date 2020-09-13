package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 18/7/2017.
 */

public class NewCategoryAdapter extends RecyclerView.Adapter<NewCategoryAdapter.ViewHolder> {

    Context mContext;
    int heightOfView;
    List<SubCategory> mList = new ArrayList<>();

    public NewCategoryAdapter(@NonNull Context context, List<SubCategory> categoryList) {
        mContext = context;
        mList.addAll(categoryList);
    }

    public int getHeightOfView() {
        return heightOfView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutContainer;
        TextViewApp tvNameJP, tvNameEng;
        View view;
        @BindView(R.id.iv_category)
        ImageView ivCategory;
        @BindView(R.id.loading_view)
        ProgressBar loadingView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
            layoutContainer = (RelativeLayout) view.findViewById(R.id.layout_category_container);
            tvNameJP = (TextViewApp) view.findViewById(R.id.tv_name_jp);
            tvNameEng = (TextViewApp) view.findViewById(R.id.tv_name_eng);
            float widthOfView = BaseActivity.screenSize.x - Utils.convertDpToPx(mContext, 20);
            heightOfView = (int) ((widthOfView * 354) / 1178);
            layoutContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightOfView));

        }

        void bindData(final SubCategory item) {
            loadingView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(item.getImage()))
                GlideApp.with(mContext).load(item.getImage()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ivCategory.setImageResource(R.mipmap.imv_default);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingView.setVisibility(View.GONE);
                        return false;
                    }
                }).into(ivCategory);
            tvNameEng.setText(item.getTitle().toUpperCase());
            tvNameJP.setText(item.getTitleJp());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(item);
                    }
                }
            });
        }
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(SubCategory category);
    }
}
