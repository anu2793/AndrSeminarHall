package jp.co.hiropro.seminar_hall.controller.activity;

import android.net.Uri;
import android.os.Bundle;
/*import android.support.design.widget.TabLayout;*/
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.VideoFreeFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.VideoPaidFragment;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class MyFavoriteActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    int position;
    private ViewPagerAdapter adapter;
    private int mId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("人気のコンテンツ");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mId = bundle.getInt(AppConstants.KEY_INTENT.ID_USER.toString(), 0);
        }
        btnBack.setVisibility(View.VISIBLE);
        // Deep LINK.
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            int index = uri.indexOf("?id=");
//            mIdCampaign = uri.substring(index + 4, uri.length()).replace("/", "");
        }
        // END DEEP LINK.
        position = getIntent().getIntExtra(KeyParser.KEY.DATA.toString(), 0);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_favorite;
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(BookFragment.newInstance(mId));
        adapter.addFrag(VideoFreeFragment.newInstance());
        adapter.addFrag(VideoPaidFragment.newInstance());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
//        View viewPurchase = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
//        tvTabs = (TextViewApp) viewPurchase.findViewById(R.id.tv_tabs);
//        tvTabs.setText("書籍");
//        tabLayout.getTabAt(0).setCustomView(viewPurchase);

        View viewHistory = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewHistory.findViewById(R.id.tv_tabs);
        tvTabs.setText("無料動画");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewHistory);

        View viewFavorite = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewFavorite.findViewById(R.id.tv_tabs);
        View separate = viewFavorite.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("有料動画");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(viewFavorite);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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
            return null;
        }
    }
}
