package vn.hanelsoft.forestpublishing.controller.fragment;

import android.content.Intent;
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
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.TopActivity;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

public class SearchResultFragment extends FragmentBase implements TopActivity.OnSearchListener,
        TopActivity.OnCloseSearchListener {
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    private ViewSearchPagerAdapter viewPageAdapterr;
    private String keySearch;
    private TopActivity topActivity;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance(String keySearch) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString("search", keySearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keySearch = getArguments().getString("search");
        }
        topActivity = (TopActivity) activity;
        topActivity.setOnSearchListener(this);
        topActivity.setOnCloseSearchListener(this);
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }


    private void setupViewPager(ViewPager viewPager) {
        viewPageAdapterr = new ViewSearchPagerAdapter(getChildFragmentManager());
        viewPageAdapterr.addFrag(SearchResultDefaultFragment.newInstance(keySearch));
//        viewPageAdapterr.addFrag(SearchResultBookFragment.newInstance(keySearch));
        viewPageAdapterr.addFrag(SearchResultFreeVideoFragment.newInstance(keySearch));
//        viewPageAdapterr.addFrag(SearchResultPaidVideoFragment.newInstance(keySearch));
        viewPager.setAdapter(viewPageAdapterr);
        viewPager.setCurrentItem(0);
    }

    private void setupTabLayout() {
        TextViewApp tvTabs;
        View viewDefault = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewDefault.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewDefault);

//        View viewBook = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabs_item, null);
//        tvTabs = (TextViewApp) viewBook.findViewById(R.id.tv_tabs);
//        tvTabs.setText("書籍人気");
//        tabLayout.getTabAt(1).setCustomView(viewBook);

//        View viewFreeVideo = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabs_item, null);
//        tvTabs = (TextViewApp) viewFreeVideo.findViewById(R.id.tv_tabs);
//        tvTabs.setText("人気");
//        tabLayout.getTabAt(1).setCustomView(viewFreeVideo);

        View viewPaidVideo = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabs_item, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        View separate = viewPaidVideo.findViewById(R.id.view_separate);
        separate.setVisibility(View.GONE);
        tvTabs.setText("人気");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(viewPaidVideo);
    }

    @Override
    public void onSearch(String querry) {
        keySearch = querry;
        if (getActivity() != null)
            getActivity().sendBroadcast(new Intent(AppConstants.BROAD_CAST.SEARCH).putExtra(AppConstants.KEY_INTENT.SEARCH_VALUES.toString(), keySearch));
    }

    @Override
    public void onClose() {

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
