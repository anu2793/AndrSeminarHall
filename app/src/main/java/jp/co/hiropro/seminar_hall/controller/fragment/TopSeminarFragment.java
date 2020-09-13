package jp.co.hiropro.seminar_hall.controller.fragment;

import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.google.android.material.tabs.TabLayout;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.CateSeminarObject;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.CateSeminarAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.ViewSubPagerAdapter;

/**
 * Created by dinhdv on 1/25/2018.
 */

public class TopSeminarFragment extends FragmentBase implements Paginate.Callbacks {
    public static int INDEX_SELECT = 0;
    @BindView(R.id.lv_category)
    RecyclerView mRcyCategory;
    @BindView(R.id.tabs_sub)
    TabLayout tabLayout;
    @BindView(R.id.pager_sub)
    ViewPager mPager;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.ll_container)
    LinearLayout mllContainer;
    CateSeminarAdapter mAdapter = null;
    ArrayList<CateSeminarObject> mListCate = new ArrayList<>();
    ViewSubPagerAdapter viewPageAdapter;
    private int page = 1;
    private boolean isLoading;


    public TopSeminarFragment() {
    }

    public static TopSeminarFragment newInstance() {
        return new TopSeminarFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_seminar_content;
    }

    @Override
    protected void inflateView() {
        initListSeminar();
        if (mAdapter == null) {
            mAdapter = new CateSeminarAdapter(activity, mListCate);
            mRcyCategory.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        Paginate.with(mRcyCategory, this)
                .setLoadingTriggerThreshold(0)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    private void getListCate() {
        isLoading = true;
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.PAGE.toString(), String.valueOf(page));
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, getActivity(), AppConstants.SERVER_PATH.SHOP_CATE_LIST.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                try {
                    if (page == 1)
                        mListCate.clear();
                    JSONArray arrayCate = object.getJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                    if (arrayCate.length() > 0) {
                        for (int i = 0; i < arrayCate.length(); i++) {
                            CateSeminarObject objectCate = CateSeminarObject.parserData(arrayCate.getJSONObject(i));
                            mListCate.add(objectCate);
                        }
                    }
                    mAdapter = new CateSeminarAdapter(activity, mListCate);
                    mRcyCategory.setAdapter(mAdapter);
                    Log.e("INDEX", "Values : " + INDEX_SELECT);
                    if (INDEX_SELECT > mListCate.size())
                        INDEX_SELECT = 0;
                    if (mListCate.size() > 0)
                        setupViewPager(mPager, mListCate.get(INDEX_SELECT));
                    page = object.optInt(AppConstants.KEY_PARAMS.NEXT_PAGE.toString(), 0);
                    mTvCopyRight.setVisibility(mListCate.size() > 0 ? View.GONE : View.VISIBLE);
                    isLoading = false;
                    mAdapter.setIndexSelect(INDEX_SELECT);
                    mAdapter.notifyDataSetChanged();
                    dismissLoading();
                } catch (JSONException e) {
                    page = 0;
                    isLoading = false;
                    mAdapter.notifyDataSetChanged();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int error) {
                page = 0;
                isLoading = false;
                mAdapter.notifyDataSetChanged();
                dismissLoading();
            }
        });
    }

    private void initListSeminar() {
        mRcyCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        mRcyCategory.addOnItemTouchListener(new RecyclerItemClickListener(activity, mRcyCategory, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CateSeminarObject cate = mListCate.get(position);
                INDEX_SELECT = position;
                if (getActivity() != null && cate != null) {
                    mAdapter.setIndexSelect(position);
                    Intent intent = new Intent(AppConstants.BROAD_CAST.UPDATE_SEMINAR_LIST);
                    intent.putExtra(AppConstants.KEY_SEND.KEY_DATA, cate);
                    getActivity().sendBroadcast(intent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onDestroy() {
        INDEX_SELECT = 0;
        super.onDestroy();
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(mPager);
        TextViewApp tvTabs;
//        View viewDefault = LayoutInflater.from(activity).inflate(R.layout.custom_tabs_item_focus, null);
//        tvTabs = (TextViewApp) viewDefault.findViewById(R.id.tv_tabs);
//        tvTabs.setText("新着");
//        tabLayout.getTabAt(0).setCustomView(viewDefault);

        View viewPaidVideo = LayoutInflater.from(activity).inflate(R.layout.custom_tabs_item_focus, null);
        tvTabs = viewPaidVideo.findViewById(R.id.tv_tabs);
        tvTabs.setText("新着");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(viewPaidVideo);

        View list = LayoutInflater.from(activity).inflate(R.layout.custom_tabs_item_focus, null);
        tvTabs = list.findViewById(R.id.tv_tabs);
        View separateList = list.findViewById(R.id.view_separate);
        separateList.setVisibility(View.GONE);
        tvTabs.setText("人気");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(list);
    }

    private void setupViewPager(ViewPager viewPager, CateSeminarObject object) {
        viewPageAdapter = new ViewSubPagerAdapter(getFragmentManager());
//        viewPageAdapter.addFrag(SeminarListFragment.newInstance(AppConstants.STATUS_LIST_SEMINAR.ALL, object));
        viewPageAdapter.addFrag(SeminarListFragment.newInstance(AppConstants.STATUS_LIST_SEMINAR.NEW, object));
        viewPageAdapter.addFrag(SeminarListFragment.newInstance(AppConstants.STATUS_LIST_SEMINAR.POPULAR, object));
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(0);
        setupTabLayout();
    }

    private void showLoading() {
        LoadingDialog.getDialog(getActivity()).show();
    }

    private void dismissLoading() {
        LoadingDialog.getDialog(getActivity()).dismiss();
    }

    @Override
    public void onLoadMore() {
        getListCate();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }
}
