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
import vn.hanelsoft.forestpublishing.controller.fragment.TeacherContentListFragment;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 1/24/2018.
 */

public class TeacherContentListActivity extends BaseActivity {
    ViewPagerAdapter vAdapter;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    int idUserActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("全てのコンテンツ");
        btnBack.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            idUserActive = extras.getInt(AppConstants.KEY_INTENT.ID_USER.toString());
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_content_list;
    }

    private void setupViewPager(ViewPager viewPager) {
        vAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vAdapter.addFrag(TeacherContentListFragment.newInstance(0, idUserActive));
        vAdapter.addFrag(TeacherContentListFragment.newInstance(1, idUserActive));
        viewPager.setAdapter(vAdapter);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;

        View viewHistory = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewHistory.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewHistory);

        View viewFavorite = LayoutInflater.from(this).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewFavorite.findViewById(R.id.tv_tabs);
        View separate = viewFavorite.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("人気");
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
