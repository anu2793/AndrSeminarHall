package jp.co.hiropro.seminar_hall.controller.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.RecommendFreeContentFragment;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ViewSubPagerAdapter;

public class RecommendFreeContentActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    @BindView(R.id.loading)
    ProgressBar loadingView;
    @BindView(R.id.ll_header)
    LinearLayout mLlHeader;
    private ViewSubPagerAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("タグの名前");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_free_content;
    }

    public void setDataHeader(String url) {
        mLlHeader.setVisibility(url.length() > 0 ? View.VISIBLE : View.GONE);
        if (url.length() > 0){
            setDataToHeader(url);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPageAdapter = new ViewSubPagerAdapter(getSupportFragmentManager());
        viewPageAdapter.addFrag(RecommendFreeContentFragment.newInstance(AppConstants.TYPE_SEARCH.DEFAULT));
        viewPageAdapter.addFrag(RecommendFreeContentFragment.newInstance(AppConstants.TYPE_SEARCH.VIDEO_FREE));
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(0);
    }

    public void setDataToHeader(String url) {
        int heightOfView = AppUtils.getScreenWidth() * 712 / 1242;
        ivBanner.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightOfView));
        Glide.with(RecommendFreeContentActivity.this).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                ivBanner.setImageResource(R.mipmap.imv_default);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadingView.setVisibility(View.GONE);
                return false;
            }
        }).into(ivBanner);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(RecommendFreeContentActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        tabLayout.getTabAt(0).setCustomView(viewDefault);

        View viewPaidVideo = LayoutInflater.from(RecommendFreeContentActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewPaidVideo.findViewById(R.id.tv_tabs);
        View separate = viewPaidVideo.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("人気");
        tabLayout.getTabAt(1).setCustomView(viewPaidVideo);
    }

}
