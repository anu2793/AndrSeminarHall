package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.MyFavoriteActivity;
import jp.co.hiropro.seminar_hall.controller.activity.WebviewActivity;
import jp.co.hiropro.seminar_hall.model.Campaign;
import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by Tuấn Sơn on 18/7/2017.
 */

public class ViewPagerAdapter extends PagerAdapter implements LoopingPagerAdapter {
    List<Campaign> mList;
    Context mContext;

    public ViewPagerAdapter(List<Campaign> imageList, Context context) {
        mList = imageList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager, null, false);
        final ImageView ivMain = (ImageView) view.findViewById(R.id.iv_main);
        final ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading_view);
        final int index = position % mList.size();
        GlideApp.with(mContext).load(mList.get(index).getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loading.setVisibility(View.GONE);
                ivMain.setImageResource(R.mipmap.imv_default);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loading.setVisibility(View.GONE);
                return false;
            }
        }).into(ivMain);

        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mList.get(index).getLink())) {
                    String link = mList.get(index).getLink();
                    // Go favorite screen
                    if (link.contains("favourite&memberid")) {
                        String prefix = "memberid=";
                        int start = link.indexOf(prefix) + prefix.length();
                        String id = link.substring(start, link.length());
                        mContext.startActivity(new Intent(mContext, MyFavoriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(AppConstants.KEY_INTENT.ID_USER.toString(), id));
                    } else {
                        mContext.startActivity(new Intent(mContext, WebviewActivity.class)
                                .putExtra(AppConstants.KEY_SEND.KEY_URL_CAMPAIN, mList.get(index).getLink()));
                    }
                }
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getRealCount() {
        return mList.size();
    }
}
