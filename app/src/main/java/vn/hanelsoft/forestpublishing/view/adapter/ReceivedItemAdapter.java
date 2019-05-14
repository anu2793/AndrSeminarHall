package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.ItemTouchHelperAdapter;
import vn.hanelsoft.forestpublishing.view.ButtonApp;
import vn.hanelsoft.forestpublishing.view.CircleImageView;

/**
 * Created by Kienmt on 1/30/2018.
 */

public class ReceivedItemAdapter extends RecyclerView.Adapter<ReceivedItemAdapter.ReceivedHolder> implements ItemTouchHelperAdapter {
    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private OnItemClickListener onItemClickListener;
    private List<User> listData;
    Context ct;

    public ReceivedItemAdapter(List<User> data, Context ct, OnItemClickListener _even) {
        listData = data;
        this.ct = ct;
        onItemClickListener = _even;
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return listData.size(); // We have to add 2 paddings
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount() - 1) {
            return VIEW_TYPE_PADDING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public ReceivedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_received, parent, false);
            return new ReceivedHolder(v, 0, ct);
        }
        else {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_received_padding, parent, false);
            return new ReceivedHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ReceivedHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            User user = listData.get(position);
            if (user != null) {
                holder.tv_name.setText(user.getFullname());
                holder.tv_description.setText(user.getProfile());
                Glide.with(ct).load(user.getAvatar()).apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_ava_default)).into(holder.imv_avatar);
                holder.mRlTeacher.setVisibility(user.getRoleUser() == AppConstants.TYPE_UER.TEACHER ? View.VISIBLE : View.INVISIBLE);
            }

        }
    }

    public class ReceivedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_description;
        CircleImageView imv_avatar;
        ButtonApp btnAccept;
        Context cxt;
        RelativeLayout mRlTeacher;

        public ReceivedHolder(View itemView, int item, Context _ct) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tvContent);
            imv_avatar = (CircleImageView) itemView.findViewById(R.id.imv_avatar);
            btnAccept = (ButtonApp) itemView.findViewById(R.id.btnAccept);
            cxt = _ct;
            mRlTeacher = itemView.findViewById(R.id.rl_type);
            btnAccept.setOnClickListener(this);
        }

        public ReceivedHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            btnAccept.setText("読み込み中");
            onItemClickListener.onItemClick(getAdapterPosition(), btnAccept);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ButtonApp btn);
    }

}
