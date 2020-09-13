package jp.co.hiropro.seminar_hall.controller.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
/*import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;*/
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.SubSubCategoryBookFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.SubSubCategoryFragment;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ViewSubPagerAdapter;

public class SubSubCategoryActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    //    @BindView(R.id.scrollable_layout)
//    ScrollableLayout mScrMain;
//    @BindView(R.id.header)
//    HeaderSubSubView mHeader;
    @BindView(R.id.iv_banner)
    ImageView mImvBanner;
    @BindView(R.id.tv_description)
    TextViewApp mTvDescription;
    SubCategory subCategoryIntent;
    @BindView(R.id.loading)
    ProgressBar loadingView;
    @BindView(R.id.iv_favorite_tag)
    ImageView mImvFavorite;
    private ViewSubPagerAdapter viewPageAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        subCategoryIntent = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        if (subCategoryIntent != null) {
            setupTitleScreen(subCategoryIntent.getTitle());
        } else {
            setupTitleScreen(getString(R.string.title_screen_category_top));
        }
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();

        setupToolbar();

        setupCollapsingToolbar();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TabbedCoordinatorLayout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(
                R.id.collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);
    }


    public void setDataHeader(JSONObject object) {
        mTvDescription.setText(object.optString("description"));
        String url = object.optString("image");
        if (url.length() > 0) {
            int heightOfView = AppUtils.getScreenWidth() * 712 / 1242;
            mImvBanner.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightOfView));
            Glide.with(SubSubCategoryActivity.this).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    mImvBanner.setImageResource(R.mipmap.imv_default);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingView.setVisibility(View.GONE);
                    return false;
                }
            }).into(mImvBanner);
        }
        mImvFavorite.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sub_sub_category;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPageAdapter = new ViewSubPagerAdapter(getSupportFragmentManager());
        viewPageAdapter.addFrag(SubSubCategoryFragment.newInstance(subCategoryIntent));
        viewPageAdapter.addFrag(SubSubCategoryBookFragment.newInstance(subCategoryIntent));
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(0);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(SubSubCategoryActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        tabLayout.getTabAt(0).setCustomView(viewDefault);

        View viewPaidVideo = LayoutInflater.from(SubSubCategoryActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        View separate = viewPaidVideo.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("人気");
        tabLayout.getTabAt(1).setCustomView(viewPaidVideo);
    }
}
