package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class CardAdapter extends ArrayAdapter<User> {
    private ArrayList<User> mListUser = new ArrayList<>();
    private onActionSendRequest mAction;
    private Context mActivity;
    private boolean mIsShowAccept = false;

    public CardAdapter(@NonNull Context context, @NonNull ArrayList<User> objects, boolean isShow) {
        super(context, 0, objects);
        mListUser = objects;
        mActivity = context;
        mIsShowAccept = isShow;
    }

    public void remove(int index) {
        if (index > -1 && index < mListUser.size()) {
            mListUser.remove(index);
            notifyDataSetChanged();
        }
    }

    public void setItems(ArrayList<User> myList) {
        this.mListUser.clear();
        this.mListUser.addAll(myList);
        notifyDataSetChanged();
    }

    //
    @Override
    public int getCount() {
        return mListUser.size();
    }
//
//    @Override
//    public User getItem(int position) {
//        if (mListUser == null || mListUser.size() == 0) return null;
//        return mListUser.get(position);
//    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            holder = new ViewHolder();
            holder.ivSubcategory = convertView.findViewById(R.id.imv_avatar);
            holder.mTvName = convertView.findViewById(R.id.tv_name);
            holder.mTvDescription = convertView.findViewById(R.id.tv_description);
            holder.mBtnSend = convertView.findViewById(R.id.btn_request_add);
            holder.mTvTeacher = convertView.findViewById(R.id.tv_teacher);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (user.getAvatar().length() > 0)
            Glide.with(mActivity).asBitmap().load(user.getAvatar()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    holder.ivSubcategory.setImageBitmap(resource);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    holder.ivSubcategory.setImageResource(R.mipmap.ic_user_default);
                }
            });
        holder.mBtnSend.setVisibility(mIsShowAccept ? View.VISIBLE : View.GONE);
        if (user.getStatus() == AppConstants.STATUS_USER.NORMAL) {
            holder.mBtnSend.setEnabled(true);
            holder.mBtnSend.setText("受信");
        } else if (user.getStatus() == AppConstants.STATUS_USER.REQUESTING) {
            holder.mBtnSend.setEnabled(false);
            holder.mBtnSend.setText("読み込み中");
        } else if (user.getStatus() == AppConstants.STATUS_USER.ERROR) {
            holder.mBtnSend.setEnabled(true);
            holder.mBtnSend.setText("ERROR");
        } else if (user.getStatus() == AppConstants.STATUS_USER.DONE) {
            holder.mBtnSend.setText("受信完了");
        }
        holder.mTvTeacher.setVisibility(user.getRoleUser() == AppConstants.TYPE_UER.TEACHER ? View.VISIBLE : View.INVISIBLE);
        holder.mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onSend(0);
            }
        });

        holder.mTvName.setText(user.getFullname());
        holder.mTvDescription.setText(user.getProfile());
        return convertView;
    }

    private class ViewHolder {
        CircleImageView ivSubcategory;
        TextViewApp mTvName, mTvDescription, mTvTeacher;
        Button mBtnSend;
    }

    public void setListener(onActionSendRequest action) {
        mAction = action;
    }

    public interface onActionSendRequest {
        void onSend(int position);
    }

}
