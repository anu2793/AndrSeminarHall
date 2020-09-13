package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.PurchaseItem;
import jp.co.hiropro.seminar_hall.util.AppUtils;

/**
 * Created by Administrator on 09/05/2017.
 */

public class PurchaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private onActionClick mActionClick;

    private ArrayList<PurchaseItem> mListPurchase = new ArrayList<>();
    private Context mContext;

    public PurchaseAdapter(Context context, ArrayList<PurchaseItem> list) {
        mListPurchase = list;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return mListPurchase.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_purchase, parent, false);
            return new MusicViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_loadmore, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MusicViewHolder) {
            final PurchaseItem item = mListPurchase.get(position);
            final MusicViewHolder userViewHolder = (MusicViewHolder) holder;
            if (item != null) {
                userViewHolder.mTvTitle.setText(item.getName().toString());
                userViewHolder.mTvDescription.setText(item.getDescription());
                if (item.getImage().length() > 0) {
                    Glide.with(mContext).load(item.getImage()).into(userViewHolder.mImvBg);
                    userViewHolder.mImvBg.setVisibility(View.VISIBLE);
                } else {
                    userViewHolder.mImvBg.setVisibility(View.GONE);
                }
                if (TextUtils.isEmpty(item.getImage())) {
                    userViewHolder.mRlmain.getLayoutParams().height = (int) mContext.getResources().getDimension(R.dimen.value_70dp);
                } else {
                    int widthScreen = AppUtils.getScreenWidth() > 0 ? AppUtils.getScreenWidth() : (int) mContext.getResources().getDimension(R.dimen.news_img_height);
                    userViewHolder.mRlmain.getLayoutParams().height = widthScreen / 2;
                }
            }

            userViewHolder.mLlTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActionClick != null) {
                        mActionClick.onItemClick(item, position);
                    }
                }
            });

            userViewHolder.mImvBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActionClick != null) {
                        mActionClick.onItemClick(item, position);
                    }
                }
            });

            userViewHolder.mBtnPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActionClick != null) {
                        mActionClick.onPurchaseAction(item, position);
                    }
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mListPurchase == null ? 0 : mListPurchase.size();
    }


    private class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitle, mTvDescription;
        ImageView mImvBg;
        RelativeLayout mRlmain;
        LinearLayout mLlTitle;
        ImageView mImvRead;
        Button mBtnPurchase;

        public MusicViewHolder(View view) {
            super(view);
            mImvBg = (ImageView) view.findViewById(R.id.news_image_view);
            mTvTitle = (TextView) view.findViewById(R.id.news_list_title);
            mTvDescription = (TextView) view.findViewById(R.id.layout_description);
            mLlTitle = (LinearLayout) view.findViewById(R.id.rl_title);
            mImvRead = (ImageView) view.findViewById(R.id.imv_new_corner);
            mRlmain = (RelativeLayout) view.findViewById(R.id.rl_main);
            mBtnPurchase = (Button) view.findViewById(R.id.btn_purchase);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public interface onActionClick {
        void onItemClick(PurchaseItem newsItem, int pos);

        void onPurchaseAction(PurchaseItem purchase, int post);
    }

    public void setActionClick(onActionClick action) {
        mActionClick = action;
    }

}


