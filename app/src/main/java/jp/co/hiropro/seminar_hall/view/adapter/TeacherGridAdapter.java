package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class TeacherGridAdapter extends HFRecyclerView<User> {
    private Context mContext;

    public TeacherGridAdapter(List<User> data, boolean withHeader, boolean withFooter, Context context) {
        super(data, withHeader, withFooter);
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_teacher_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new TeacherGridAdapter.FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivSubcategory;
        ProgressBar mPrgLoading;
        TextViewApp mTvName, mTvDescription;

        public ViewHolder(View view) {
            super(view);
            ivSubcategory = view.findViewById(R.id.imv_seminar);
            mTvName = view.findViewById(R.id.tv_name);
            mTvDescription = view.findViewById(R.id.tv_description);
            mPrgLoading = view.findViewById(R.id.prg_loading);
//            int widthOfView = (BaseActivity.screenSize.x / 2);
//            int heightOfView = (widthOfView / 2);
//            ivSubcategory.setLayoutParams(new RelativeLayout.LayoutParams(widthOfView, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        void bindData(final User item) {
            mPrgLoading.setVisibility(View.VISIBLE);
            if (item.getAvatar().length() > 0) {
                GlideApp.with(mContext).load(item.getAvatar()).into(ivSubcategory);
                mPrgLoading.setVisibility(View.GONE);
            } else {
                GlideApp.with(mContext).load(R.mipmap.ic_user_default).into(ivSubcategory);
                mPrgLoading.setVisibility(View.GONE);
            }
            mTvName.setText(item.getFullname());
            mTvDescription.setText(item.getProfile());
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
