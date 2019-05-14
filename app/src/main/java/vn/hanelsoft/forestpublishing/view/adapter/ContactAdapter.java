package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.OnSingleClickListener;
import vn.hanelsoft.forestpublishing.view.CircleImageView;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by Kienmt on 1/25/2018.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<User> contactObjects;
    private Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ContactAdapter(List<User> contactObjects, Context context) {
        this.contactObjects = contactObjects;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(contactObjects.get(position).getFullname());
        if (position == contactObjects.size() - 1) {
            holder.line.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(contactObjects.get(position).getAvatar()))
            Glide.with(context).load(contactObjects.get(position).getAvatar()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return contactObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextViewApp tvName;
        CircleImageView imageView;
        RelativeLayout imv_hide;
        View line;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imageView = itemView.findViewById(R.id.imv_avatar);
            imv_hide = itemView.findViewById(R.id.imv_hide);
            line = itemView.findViewById(R.id.line);
            tvName.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(getAdapterPosition());
                }
            });
            imv_hide.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (mClickListener != null)
                        mClickListener.onRemoveClick(getAdapterPosition());
                }
            });
        }
    }

    public void notificationItemRemove(int index) {
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, contactObjects.size());
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);

        void onRemoveClick(int position);
    }
}
