package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.FavoriteFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.HistoryFragment;
import jp.co.hiropro.seminar_hall.controller.fragment.PurchaseFragment;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class MyLibraryActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private int position;
    private ViewPagerAdapter adapter;
    private BroadcastReceiver mReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("マイライブラリ");
        btnBack.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            position = bundle.getInt(KeyParser.KEY.DATA.toString(), 0);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_library;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(PurchaseFragment.newInstance(user));
        adapter.addFrag(HistoryFragment.newInstance());
        adapter.addFrag(FavoriteFragment.newInstance());
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
        View viewPurchase = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewPurchase.findViewById(R.id.tv_tabs);
        tvTabs.setText("購入済み");
        tabLayout.getTabAt(0).setCustomView(viewPurchase);

        View viewHistory = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewHistory.findViewById(R.id.tv_tabs);
        tvTabs.setText("視聴履歴");
        tabLayout.getTabAt(1).setCustomView(viewHistory);

        View viewFavorite = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = (TextViewApp) viewFavorite.findViewById(R.id.tv_tabs);
        View separate = viewFavorite.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("お気に入り");
        tabLayout.getTabAt(2).setCustomView(viewFavorite);
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

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadCaseReceive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadCaseReceive();
    }

    private void registerBroadCaseReceive() {
        if (mReceive == null)
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.CHANGE_TAB)) {
                        int index = intent.getIntExtra(AppConstants.KEY_SEND.KEY_SEND_TAB, 0);
                        if (viewPager != null)
                            viewPager.setCurrentItem(index);
                    }
                }
            };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.CHANGE_TAB);
        registerReceiver(mReceive, intentFilter);
    }

    private void unRegisterBroadCaseReceive() {
        if (mReceive != null)
            unregisterReceiver(mReceive);
    }
}
