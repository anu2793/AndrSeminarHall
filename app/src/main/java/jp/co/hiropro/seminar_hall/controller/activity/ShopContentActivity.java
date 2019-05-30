package jp.co.hiropro.seminar_hall.controller.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.TopContentFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.TopSeminarFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.TopTeacherListFragment;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.NonSwipeableViewPager;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ViewSubPagerAdapter;

/**
 * Created by dinhdv on 1/22/2018.
 */

public class ShopContentActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    NonSwipeableViewPager mPager;
    private int index = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.title_screen_shop_content));
        ButterKnife.bind(this);
        btnBack.setVisibility(View.INVISIBLE);
        setupViewPager(mPager);
        tabLayout.setupWithViewPager(mPager);
        setupTabLayout();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            index = bundle.getInt(AppConstants.KEY_SEND.KEY_POSITION_TAB, 0);
        mPager.setCurrentItem(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewSubPagerAdapter viewPageAdapter = new ViewSubPagerAdapter(getSupportFragmentManager());
        viewPageAdapter.addFrag(TopContentFragment.newInstance());
        viewPageAdapter.addFrag(TopSeminarFragment.newInstance());
        viewPageAdapter.addFrag(TopTeacherListFragment.newInstance());
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(0);
    }

    public void selectionTabIndex(int index) {
        if (mPager != null)
            mPager.setCurrentItem(index, true);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(ShopContentActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("トップ");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewDefault);

        View viewPaidVideo = LayoutInflater.from(ShopContentActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        tvTabs.setText("セミナー");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(viewPaidVideo);

        View list = LayoutInflater.from(ShopContentActivity.this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = list.findViewById(R.id.tv_tabs);
        View separateList = list.findViewById(R.id.view_separate);
        separateList.setVisibility(View.GONE);
        tvTabs.setText("講師");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setCustomView(list);
    }
}
