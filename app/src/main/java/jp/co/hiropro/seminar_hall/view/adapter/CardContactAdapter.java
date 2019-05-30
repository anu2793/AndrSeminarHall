package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.OnSingleClickListener;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class CardContactAdapter extends RecyclerView.Adapter<CardContactAdapter.ViewHolder> {
    private ArrayList<User> mListUser = new ArrayList<>();
    private onActionSendRequest mAction;
    private Context mActivity;
    private int mHeightView = 1;

    public CardContactAdapter(@NonNull Context context, @NonNull ArrayList<User> objects, int height, onActionSendRequest action) {
        mListUser = objects;
        mActivity = context;
        mAction = action;
//        mHeightView = AppUtils.getScreenHigh() * 3 / 4;
        mHeightView = height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_contact, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final int newPosition = holder.getAdapterPosition();
        final User user = mListUser.get(newPosition);
        if (user.getAvatar().length() > 0)
            Glide.with(mActivity).load(user.getAvatar()).into(holder.ivSubcategory);
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
            holder.mBtnSend.setEnabled(false);
            holder.mBtnSend.setText("受信完了");
        }
        holder.mTvTeacher.setVisibility(user.getRoleUser() == AppConstants.TYPE_UER.TEACHER ? View.VISIBLE : View.INVISIBLE);
//        holder.mBtnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAction != null)
//                    mAction.onSend(user, holder.getAdapterPosition());
//            }
//        });
        holder.mBtnSend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mAction != null)
                    mAction.onSend(user, newPosition);
            }
        });

        holder.mTvName.setText(user.getFullname());
        holder.mTvDescription.setText(user.getProfile());
    }

    @Override
    public int getItemCount() {
        return mListUser.size();
    }

    public void removeItemAtIndex(int index) {
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mListUser.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivSubcategory;
        TextViewApp mTvName, mTvTeacher, mTvDescription;
        Button mBtnSend;
        RelativeLayout mRlTop;
        RelativeLayout mRlContent, RlGradient;
        CardView mCardHolder;
        private LinearLayout mLlSubContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSubcategory = itemView.findViewById(R.id.imv_avatar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvDescription = itemView.findViewById(R.id.tv_description_contact);
            mBtnSend = itemView.findViewById(R.id.btn_request_add);
            mTvTeacher = itemView.findViewById(R.id.tv_teacher);
            mRlTop = itemView.findViewById(R.id.rl_top);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mCardHolder = itemView.findViewById(R.id.card_view);
            mLlSubContent = itemView.findViewById(R.id.ll_sub_content);
            RlGradient = itemView.findViewById(R.id.rl_gradient);
            // Top
            Log.e("HeightView", "Values :" + mHeightView);
            int heightTop = mHeightView / 8 * 3;
            LinearLayout.LayoutParams list = (LinearLayout.LayoutParams) mRlTop.getLayoutParams();
            list.height = heightTop;
            mRlTop.setLayoutParams(list);
            // Gradient.
            RelativeLayout.LayoutParams rlGradient = (RelativeLayout.LayoutParams) RlGradient.getLayoutParams();
            rlGradient.height = heightTop / 6 * 5;
            RlGradient.setLayoutParams(rlGradient);

            // Gradient.
            RelativeLayout.LayoutParams avatar = (RelativeLayout.LayoutParams) ivSubcategory.getLayoutParams();
            avatar.height = heightTop / 7 * 5;
            avatar.width = heightTop / 7 * 5;
            ivSubcategory.setLayoutParams(avatar);

            //Content
            int heightContainerContent = mHeightView - heightTop;
//            LinearLayout.LayoutParams content = (LinearLayout.LayoutParams) mRlContent.getLayoutParams();
//            content.height = heightContainerContent;
//            content.gravity = Gravity.TOP;
//            mRlContent.setLayoutParams(content);

            // Content
            int heightContent = (int) ((heightContainerContent / 7 * 2) + mTvDescription.getLineSpacingExtra());
            LinearLayout.LayoutParams contentDes = (LinearLayout.LayoutParams) mTvDescription.getLayoutParams();
            contentDes.height = heightContent;
            mTvDescription.setLayoutParams(contentDes);

            // mRlButton
//            RelativeLayout.LayoutParams rlButton = (RelativeLayout.LayoutParams) mLlSubContent.getLayoutParams();
//            rlButton.height = heightContainerContent - heightContent;
//            mLlSubContent.setLayoutParams(rlButton);

//            // Button request.
//            RelativeLayout.LayoutParams btnRequest = (RelativeLayout.LayoutParams) mBtnSend.getLayoutParams();
//            btnRequest.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            btnRequest.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            btnRequest.height = heightContainerContent / 9 * 2;
//            mBtnSend.setLayoutParams(btnRequest);


        }
    }

    public void setListener(onActionSendRequest action) {
        mAction = action;
    }

    public interface onActionSendRequest {
        void onSend(User user, int position);
    }

}
