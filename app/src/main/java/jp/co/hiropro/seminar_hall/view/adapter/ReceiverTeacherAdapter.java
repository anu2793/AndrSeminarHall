package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.ButtonApp;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class ReceiverTeacherAdapter extends ArrayAdapter<User> {
    private ArrayList<User> mListUser = new ArrayList<>();
    private onActionSendRequest mAction;
    private Context mActivity;

    public ReceiverTeacherAdapter(@NonNull Context context, @NonNull ArrayList<User> objects) {
        super(context, 0, objects);
        mListUser = objects;
        mActivity = context;
    }

//    public ReceiverTeacherAdapter(Activity activity) {
//        mListUser = new ArrayList<>();
//        mActivity = activity;
//    }

//    public void addAll(Collection<User> collection) {
//        if (isEmpty()) {
//            mListUser.addAll(collection);
//            notifyDataSetChanged();
//        } else {
//            mListUser.addAll(collection);
//        }
//    }

//    public void clear() {
//        mListUser.clear();
//        notifyDataSetChanged();
//    }
//
//    public boolean isEmpty() {
//        return mListUser.isEmpty();
//    }

    public void remove(int index) {
        if (index > -1 && index < mListUser.size()) {
            mListUser.remove(index);
            notifyDataSetChanged();
        }
    }

    public void changeStatusItem(int position, int status) {
        User user = new User();
        user.setStatus(status);
        user.setEmail("Tao click vao roi day");
        mListUser.set(position, user);
        notifyDataSetChanged();
    }

    //
//    @Override
//    public int getCount() {
//        return mListUser.size();
//    }
//
    @Override
    public User getItem(int position) {
        if (mListUser == null || mListUser.size() == 0) return null;
        return mListUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_card, parent, false);
            holder = new ViewHolder();
            holder.ivSubcategory = convertView.findViewById(R.id.imv_avatar);
            holder.mTvName = convertView.findViewById(R.id.tv_name);
            holder.mTvDescription = convertView.findViewById(R.id.tv_description);
            holder.mBtnSend = convertView.findViewById(R.id.btn_request_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GlideApp.with(mActivity).asBitmap().load(position % 2 == 0 ? "https://hinhanhdephd.com/wp-content/uploads/2016/08/anh-girl-xinh-1998-1.jpg" : "http://i.a4vn.com/2017/10/18/tong-hop-girl-xinh-vong-1-cang-tron-thang-10-b41a4c.jpg").into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                holder.ivSubcategory.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                holder.ivSubcategory.setImageResource(R.mipmap.imv_default);
            }
        });
        holder.mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction != null)
                    mAction.onSend(0);
            }
        });

        holder.mTvName.setText(user.getEmail());
        holder.mTvDescription.setText("講師の紹介テキスト３行まで。講師の紹介テキスト３行まで講師の紹介テキスト３行まで講師の紹介テキスト３行まで講師の紹介テキスト３行まで講師の紹介テキスト３行まで長いテキストの場合最後にを付けまししょう。講師の...");
        if (user.getStatus() == AppConstants.STATUS_USER.NORMAL) {
            holder.mBtnSend.setText("受信");
        } else if (user.getStatus() == AppConstants.STATUS_USER.REQUESTING) {
            holder.mBtnSend.setText("読み込み中");
        } else if (user.getStatus() == AppConstants.STATUS_USER.DONE) {
            holder.mBtnSend.setText("読み込み完了");
        } else if (user.getStatus() == AppConstants.STATUS_USER.ERROR) {
            holder.mBtnSend.setText("ERROR ! Hay thu lai");
        }
        return convertView;
    }

    private class ViewHolder {
        CircleImageView ivSubcategory;
        TextViewApp mTvName, mTvDescription;
        ButtonApp mBtnSend;
    }

    public void setListener(onActionSendRequest action) {
        mAction = action;
    }

    public interface onActionSendRequest {
        void onSend(int position);
    }

}
