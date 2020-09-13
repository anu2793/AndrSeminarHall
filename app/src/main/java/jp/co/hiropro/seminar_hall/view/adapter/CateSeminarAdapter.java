package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.CateSeminarObject;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 26/01/2018.
 */

public class CateSeminarAdapter extends RecyclerView.Adapter<CateSeminarAdapter.ViewHolder> {
    private Context mContext;
    private List<CateSeminarObject> mList = new ArrayList<>();
    private int mIndexSelected = 0;

    public CateSeminarAdapter(@NonNull Context context, List<CateSeminarObject> categoryList) {
        mContext = context;
        mList.addAll(categoryList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cate_seminar, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null)
            holder.bindData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextViewApp mTvName;

        ViewHolder(View view) {
            super(view);
            mTvName = view.findViewById(R.id.tv_name);
        }

        void bindData(final CateSeminarObject item, int position) {
            mTvName.setText(item.getName());
            Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.bg_border_gray);
            if (mIndexSelected == position) {
//                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.MULTIPLY));
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#74a809"), PorterDuff.Mode.MULTIPLY));
                mTvName.setBackground(mDrawable);
            } else {
//                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.MULTIPLY));
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#92c627"), PorterDuff.Mode.MULTIPLY));
                mTvName.setBackground(mDrawable);
            }

        }
    }

    public void setIndexSelect(int position) {
        mIndexSelected = position;
        notifyDataSetChanged();
    }
}
