package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by Administrator on 09/05/2017.
 */

public class NewsTopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NewsItem> mListMusic = new ArrayList<>();
    private Context mContext;

    public NewsTopAdapter(Context context, ArrayList<NewsItem> list) {
        mListMusic = list;
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news_list_top, parent, false);
        return new TopViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TopViewHolder) {
            final NewsItem item = mListMusic.get(position);
            final TopViewHolder userViewHolder = (TopViewHolder) holder;
            // This text set to view to count line of textview.
            if (item.isRead()) {
                userViewHolder.mTvTitle.setText(item.getTitle());
            } else {
                SpannableStringBuilder builder = AppUtils.getAddNewTextToFirstGray(item.getTitle(), " NEW", mContext);
                userViewHolder.mTvTitle.setText(builder, TextView.BufferType.SPANNABLE);
            }
            Log.e("List", "values :" + position + "--" + mListMusic.size());
            userViewHolder.mLine.setVisibility(position + 1 == mListMusic.size() ? View.GONE : View.VISIBLE);
//            AppUtils.makeTextViewResizable(mContext, userViewHolder.mTvTitle, item.getTitle(), 2, " NEW", item.isRead());
        }

    }

    @Override
    public int getItemCount() {
        return mListMusic == null ? 0 : mListMusic.size();
    }


    private class TopViewHolder extends RecyclerView.ViewHolder {
        TextViewApp mTvTitle;
        View mLine;

        public TopViewHolder(View view) {
            super(view);
            mTvTitle = (TextViewApp) view.findViewById(R.id.news_list_title);
            mLine = view.findViewById(R.id.line);
        }
    }
}


