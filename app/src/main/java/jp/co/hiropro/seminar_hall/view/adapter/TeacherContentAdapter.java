package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Kienmt on 1/25/2018.
 */

public class TeacherContentAdapter extends RecyclerView.Adapter<TeacherContentAdapter.ViewHolder> {
    private List<VideoDetail> contactObjects;
    private Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final int VIDEO_PAID = 0;
    private final int VIDEO_FREE = 1;
    private final int VIDEO_FREE_TYPE_2 = 2;
    private final int VIDEO_LIMITED_FREE = 3;
    private final int VIDEO_SPECIAL = 4;
    private final int VIDEO_LIMITED_SPCIAL = 5;

    public TeacherContentAdapter(List<VideoDetail> contactObjects, Context context, ItemClickListener _even) {
        this.contactObjects = contactObjects;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mClickListener = _even;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_teacher_content_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(contactObjects.get(position).getTitle());
        holder.tv_content.setText(contactObjects.get(position).getDescription());
        RequestOptions options = new RequestOptions();
        options.placeholder(context.getDrawable(R.mipmap.icon_default_video));
        Glide.with(context).load(contactObjects.get(position).getImage()).apply(options).into(holder.imv_content);
        checkStatus(contactObjects.get(position), holder.tvStatus);
    }

    private void checkStatus(VideoDetail item, TextViewApp tvTypeOfSubcategory) {
        User user = User.getInstance().getCurrentUser();
        if (item.getType() == VIDEO_FREE || item.getType() == VIDEO_FREE_TYPE_2) {
            if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            if (item.getHasBoughtVideo() == 1) {
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
                tvTypeOfSubcategory.setText("購入済み");
            } else {
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list));
                tvTypeOfSubcategory.setText("無料");
            }
        } else if (item.getType() == VIDEO_LIMITED_FREE) {
            if (item.getHasBoughtVideo() == 1) {
                tvTypeOfSubcategory.setText("購入済み");
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
            } else {
                if (user.isPremiumUser()) {
                    tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
                } else {
                    if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    if (item.getHasBoughtVideo() == 1) {
                        tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
                        tvTypeOfSubcategory.setText("購入済み");
                    } else {
                        tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list));
                        tvTypeOfSubcategory.setText(context.getString(R.string.txt_free_limited));
                    }
                }
            }

        } else if (item.getType() == VIDEO_PAID) {
            if (item.getHasBoughtVideo() == 1) {
                tvTypeOfSubcategory.setText("購入済み");
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
            } else {
                tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
                if (user.isPremiumUser()) {
                    tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
                } else {
                    tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
                    if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                        tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        } else if (item.getType() == VIDEO_SPECIAL) {
            tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
            if (item.getHasBoughtVideo() == 1) {
                tvTypeOfSubcategory.setText("購入済み");
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
            } else {
                tvTypeOfSubcategory.setText(Utils.formatPrice(item.getPrice()));
            }
            if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else if (item.getType() == VIDEO_LIMITED_SPCIAL) {
            if (item.getHasBoughtVideo() == 1) {
                tvTypeOfSubcategory.setText("購入済み");
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list_price));
            } else {
                tvTypeOfSubcategory.setBackground(context.getDrawable(R.drawable.bg_edt_teacher_content_list));
                tvTypeOfSubcategory.setText(context.getString(R.string.txt_free_limited));
            }
            if ((tvTypeOfSubcategory.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0)
                tvTypeOfSubcategory.setPaintFlags(tvTypeOfSubcategory.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return contactObjects.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextViewApp tvName, tv_content, tvStatus;
        ImageView imv_content;
        ConstraintLayout rl;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextViewApp) itemView.findViewById(R.id.tv_title);
            tv_content = (TextViewApp) itemView.findViewById(R.id.tv_content);
            tvStatus = (TextViewApp) itemView.findViewById(R.id.tvStatus);
            imv_content = (ImageView) itemView.findViewById(R.id.imv_content);
            rl = (ConstraintLayout) itemView.findViewById(R.id.layout);
            rl.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
