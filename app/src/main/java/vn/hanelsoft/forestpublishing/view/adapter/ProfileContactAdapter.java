package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import vn.hanelsoft.forestpublishing.GlideApp;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.BaseActivity;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.view.CircleImageView;

/**
 * Created by DinhDV.
 */

public class ProfileContactAdapter extends RecyclerView.Adapter<ProfileContactAdapter.ViewHolder> {
    private List<User> mListObjects = new ArrayList<>();
    private Context mContext;

    public ProfileContactAdapter(List<User> contactObjects, Context context) {
        this.mListObjects = contactObjects;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_profile_contact, null);
        int widthOfView = (BaseActivity.screenSize.x / 9 * 2);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = widthOfView;
            params.height = widthOfView;
            view.setLayoutParams(params);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ViewHolder)
            holder.bindData(mListObjects.get(position));
    }

    @Override
    public int getItemCount() {
        return mListObjects.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imv_avatar);
        }

        public void bindData(User contact) {
            int widthOfView = (BaseActivity.screenSize.x / 9 * 2);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(widthOfView, widthOfView));
            if (contact.getAvatar().length() > 0)
                GlideApp.with(mContext).load(contact.getAvatar()).error(R.mipmap.ic_user_default).into(imageView);
            else
                GlideApp.with(mContext).load(R.mipmap.ic_user_default).into(imageView);
        }
    }

}
