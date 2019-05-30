package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.JackImageObject;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by dinhdv on 11/9/2017.
 */

public class JackImageAdapter extends HFRecyclerView<JackImageObject> {
    private Context mContext;

    public JackImageAdapter(Context context, List<JackImageObject> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        View mRootView = inflater.inflate(R.layout.item_jack_image, parent, false);
        return new JackImageAdapter.ViewHolder(mRootView);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new JackImageAdapter.FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).binData(getItem(position));
        }
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImvBackground;
        TextViewApp mTvBody;

        public ViewHolder(View itemView) {
            super(itemView);
            mImvBackground = (ImageView) itemView.findViewById(R.id.imv_background);
            mTvBody = (TextViewApp) itemView.findViewById(R.id.tv_body);
        }

        public void binData(JackImageObject object) {
            mTvBody.setVisibility(object.getUrl().length() > 0 ? View.GONE : View.VISIBLE);
            if (object.getUrl().length() > 0) {
                GlideApp.with(mContext).load(object.getUrl()).error(R.mipmap.imv_default_error_banner).into(mImvBackground);
            } else {
                ImageSpan span = new ImageSpan(mContext, R.mipmap.ic_youtube_link, ImageSpan.ALIGN_BOTTOM) {
                    public void draw(Canvas canvas, CharSequence text, int start,
                                     int end, float x, int top, int y, int bottom,
                                     Paint paint) {
                        Drawable b = getDrawable();
                        canvas.save();
                        int transY = bottom - b.getBounds().bottom;
                        // this is the key
                        transY -= paint.getFontMetricsInt().descent * 3.5;
                        canvas.translate(x, transY);
                        b.draw(canvas);
                        canvas.restore();
                    }
                };
                String name = object.getContent();
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(name + " ").append(" ", span, 0);
                mTvBody.setText(builder);
            }
        }
    }
}
