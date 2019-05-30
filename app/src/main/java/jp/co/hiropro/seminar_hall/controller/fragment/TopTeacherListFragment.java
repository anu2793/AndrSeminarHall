package jp.co.hiropro.seminar_hall.controller.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ViewSubPagerAdapter;

/**
 * Created by dinhdv on 1/25/2018.
 */

public class TopTeacherListFragment extends FragmentBase {
    @BindView(R.id.tabs_sub)
    TabLayout tabLayout;
    @BindView(R.id.pagers)
    ViewPager mPager;

    ViewSubPagerAdapter viewPageAdapter;

    public TopTeacherListFragment() {
    }

    public static TopTeacherListFragment newInstance() {
        return new TopTeacherListFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teacher_list_content;
    }

    @Override
    protected void inflateView() {
        setupViewPager(mPager);
        tabLayout.setupWithViewPager(mPager);
        setupTabLayout();
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(activity).inflate(R.layout.custom_tabs_item_focus, null);
        tvTabs = viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewDefault);

        View viewPaidVideo = LayoutInflater.from(activity).inflate(R.layout.custom_tabs_item_focus, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        tvTabs.setText("人気");
        View separateList = viewPaidVideo.findViewById(R.id.view_separate);
        separateList.setVisibility(View.GONE);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(viewPaidVideo);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPageAdapter = new ViewSubPagerAdapter(getFragmentManager());
        viewPageAdapter.addFrag(TeacherListFragment.newInstance(1));
        viewPageAdapter.addFrag(TeacherListFragment.newInstance(2));
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(0);
    }
}
