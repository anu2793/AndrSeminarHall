package vn.hanelsoft.forestpublishing.controller.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.fragment.TagDefaultFragment;
import vn.hanelsoft.forestpublishing.controller.fragment.TagFragment;
import vn.hanelsoft.forestpublishing.model.SubCategory;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

public class VideoSameTagActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    private List<VideoDetail> subCategoryList = new ArrayList<>();
    private SubCategory subCategory;
    private String tag;
    private ViewSearchPagerAdapter viewPageAdapterr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        subCategory = getIntent().getParcelableExtra(KeyParser.KEY.DATA.toString());
        tag = getIntent().getStringExtra("tag");
        setupTitleScreen(tag);
        setupViewPager(tag, subCategory, viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_same_tag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (subCategoryList.size() > 0) {
            subCategoryList.clear();
        }
    }


    private void setupViewPager(String tag, SubCategory subCategory, ViewPager viewPager) {
        viewPageAdapterr = new ViewSearchPagerAdapter(getSupportFragmentManager());
        viewPageAdapterr.addFrag(TagDefaultFragment.newInstance(tag, AppConstants.TYPE_SEARCH.DEFAULT, subCategory));
        viewPageAdapterr.addFrag(TagFragment.newInstance(tag, AppConstants.TYPE_SEARCH.VIDEO_FREE, subCategory));
        viewPageAdapterr.addFrag(TagFragment.newInstance(tag, AppConstants.TYPE_SEARCH.VIDEO_BUY, subCategory));
        viewPager.setAdapter(viewPageAdapterr);
        viewPager.setCurrentItem(0);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(VideoSameTagActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewDefault);

        View viewBook = LayoutInflater.from(VideoSameTagActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewBook.findViewById(R.id.tv_tabs);
        tvTabs.setText("無料人気");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(viewBook);

        View viewPaidVideo = LayoutInflater.from(VideoSameTagActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        View separate = viewPaidVideo.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("有料人気");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setCustomView(viewPaidVideo);
    }


    private class ViewSearchPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewSearchPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        public void clear() {
            mFragmentList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "ABC";
        }
    }

}