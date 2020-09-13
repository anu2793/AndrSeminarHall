package jp.co.hiropro.seminar_hall.controller.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.IdRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollState;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.ShopContentActivity;
import jp.co.hiropro.seminar_hall.controller.activity.SubCategoryDetailActivity;
import jp.co.hiropro.seminar_hall.controller.activity.TeacherProfileCardActivity;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.RecyclerSubCategoryNoDescriptionAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.SeminarAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.TeacherAdapter;
import jp.co.hiropro.utils.NetworkUtils;

/**
 * Created by dinhdv on 1/25/2018.
 */

public class TopContentFragment extends FragmentBase {
    @BindView(R.id.lv_seminar_paid)
    RecyclerView mRcyPaid;
    @BindView(R.id.lv_seminar_free)
    RecyclerView mRcyFree;
    @BindView(R.id.lv_teacher)
    RecyclerView mRcyTeacher;
    @BindView(R.id.lv_seminar_newest)
    RecyclerView lvSubcategory;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.layout_search_view)
    RelativeLayout layoutSearchBar;
    @BindView(R.id.appbar)
    AppBarLayout searchContainer;
    @BindView(R.id.layout_search)
    FrameLayout layoutSearch;
    @BindView(R.id.layout_content_container)
    LinearLayout layoutContentContainer;
    @BindView(R.id.ll_view_free)
    LinearLayout mLlViewFree;
    @BindView(R.id.ll_view_teacher)
    LinearLayout mLlViewTeacher;
    @BindView(R.id.ll_view_seminar)
    LinearLayout mLlViewSeminar;
    @BindView(R.id.container_main)
    CoordinatorLayout ContainerMain;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;

    private ImageView closeIcon;
    private ArrayList<SubCategory> subCategoryList = new ArrayList<>();
    private RecyclerSubCategoryNoDescriptionAdapter adapter;
    private Timer timer = new Timer();
    private ArrayList<SubCategory> listSeminarPay, listSeminarFree;
    private SeminarAdapter mAdapterSeminarPay, mAdapterSeminarFree;
    private TeacherAdapter mAdapterTeacher;
    private IOverScrollDecor decor;
    private ArrayList<User> mListUser = new ArrayList<>();

    private DividerItemDecoration mDivider, mTop = null;
    private ShopSearchResultFragment searchResultFragment;
    private BroadcastReceiver mReceive;

    public TopContentFragment() {
    }

    public static TopContentFragment newInstance() {
        return new TopContentFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_top_content;
    }

    @Override
    protected void inflateView() {
        listSeminarFree = new ArrayList<>();
        listSeminarPay = new ArrayList<>();
//        mAdapterSeminarPay = new SeminarAdapter(listSeminarPay, false, false, getActivity());
        mAdapterSeminarFree = new SeminarAdapter(listSeminarFree, false, false, getActivity());
        mAdapterTeacher = new TeacherAdapter(mListUser, false, false, getActivity());
        mRcyTeacher.setAdapter(mAdapterTeacher);
//        mRcyPaid.setAdapter(mAdapterSeminarPay);
        mRcyFree.setAdapter(mAdapterSeminarFree);
        initListSeminar();
        initSeminarNewest();
        initListSubCategory();
        layoutContentContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int height = layoutContentContainer.getHeight();
                        int heightScreen = AppUtils.getScreenHigh() - (2 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar));
                        showCopyRight(height != 0 && (height < heightScreen));
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        setupOverScroll();
        setupSearchView();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    public void onDestroy() {
        unRegisterBroadcast();
        super.onDestroy();
    }

    @OnClick({R.id.tv_go_seminar_list, R.id.tv_go_seminar_list_free, R.id.tv_go_list_teacher, R.id.tv_go_seminar_list_new, R.id.tv_cancel})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go_seminar_list:
                ((ShopContentActivity) getActivity()).selectionTabIndex(1);
                break;
            case R.id.tv_go_seminar_list_free:
                ((ShopContentActivity) getActivity()).selectionTabIndex(1);
                break;
            case R.id.tv_go_list_teacher:
                ((ShopContentActivity) getActivity()).selectionTabIndex(2);
                break;
            case R.id.tv_go_seminar_list_new:
                ((ShopContentActivity) getActivity()).selectionTabIndex(1);
                break;
            case R.id.tv_cancel:
                layoutSearchBar.setVisibility(View.GONE);
                ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
                closeIcon.performClick();
                searchResultFragment = null;
                tvCancel.setVisibility(View.GONE);
                layoutContentContainer.setVisibility(View.VISIBLE);
                layoutSearch.setVisibility(View.GONE);
                setupOverScroll();
                searchView.clearFocus();
                AppUtils.hiddenKeyboard(activity);
                break;
        }
    }

    private void setupSearchView() {
        closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeIcon.setEnabled(false);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
                searchIcon.performClick();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                tvCancel.setVisibility(newText.length() > 0 ? View.VISIBLE : View.GONE);
                if (TextUtils.isEmpty(newText)) {
                    closeIcon.setVisibility(View.GONE);
                    closeIcon.setEnabled(false);
                    if (onCloseSearch != null)
                        onCloseSearch.onClose();
                } else closeIcon.setEnabled(true);

                if (timer != null) timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * @Nga Confirm : Khi toàn kí tự trắng. Ẩn màn kể quả search . Vào top.
                                 */
                                if (newText.trim().length() == 0) {
                                    searchView.setQuery("", false);
//                                    searchView.clearFocus();
                                    layoutContentContainer.setVisibility(View.VISIBLE);
                                    layoutSearch.setVisibility(View.GONE);
                                    closeIcon.setVisibility(View.GONE);
                                } else {
                                    layoutContentContainer.setVisibility(View.GONE);
                                    layoutSearch.setVisibility(View.VISIBLE);
                                    if (searchResultFragment == null && !TextUtils.isEmpty(newText)) {
                                        searchResultFragment = ShopSearchResultFragment.newInstance(newText, TopContentFragment.this);
                                        attachFragment(searchResultFragment, R.id.layout_search, true);
                                    }
                                    if (onSearchListener != null && !TextUtils.isEmpty(newText))
                                        onSearchListener.onSearch(newText);
                                }
                            }
                        });
                    }
                }, 1000);
                return false;
            }
        });

        searchView.setIconified(false);
        searchView.clearFocus();
    }

    private void setupOverScroll() {
        decor = OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        Log.e("Size: ", String.valueOf(Utils.convertDpToPx(activity, 60)));
        decor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(final IOverScrollDecor decor, int state, float offset) {
                if (state == IOverScrollState.STATE_DRAG_START_SIDE && offset >= 15) {
                    ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutSearchBar, "translationY", -Utils.convertDpToPx(activity, 60), 0);
                    AnimatorSet as = new AnimatorSet();
                    as.setDuration(250);
                    as.play(translationX);
                    if (layoutSearchBar.getVisibility() == View.GONE || searchContainer.getVisibility() == View.GONE) {
                        layoutSearchBar.setVisibility(View.VISIBLE);
                        searchContainer.setVisibility(View.VISIBLE);
                        searchContainer.setExpanded(true, true);
                        as.start();
                    }

                    as.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            searchView.setIconified(false);
                            searchView.clearFocus();
                            closeIcon.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            }
        });

        final int heightOfSearchBar = (int) Utils.convertDpToPx(activity, 50);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= heightOfSearchBar) {
                    searchContainer.setExpanded(false, false);
                    searchContainer.setVisibility(View.GONE);
                    layoutSearchBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initListSubCategory() {
        adapter = new RecyclerSubCategoryNoDescriptionAdapter(activity, subCategoryList, User.getInstance().getCurrentUser());
        adapter.setVisibleFavoriteImage(true);
        lvSubcategory.setAdapter(adapter);
        adapter.setOnFavoriteListener(new RecyclerSubCategoryNoDescriptionAdapter.FavoriteListener() {
            @Override
            public void onFavorite(SubCategory subCategory) {
                setFavorite(subCategory);
            }
        });
        adapter.setOnItemClick(new RecyclerSubCategoryNoDescriptionAdapter.OnItemClick() {
            @Override
            public void onItemClick(SubCategory subCategory) {
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }
        });
    }

    private void setFavorite(SubCategory subCategory) {
        LoadingDialog.getDialog(activity).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", subCategory.getId());
        params.put("status", subCategory.getIsFavorite());
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.FAVORITE_SUBCATEGORY.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialog.getDialog(activity).dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.getDialog(activity).dismiss();
                        NetworkUtils.showDialogError(activity, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void initSeminarNewest() {
        lvSubcategory.setLayoutManager(new LinearLayoutManager(activity));
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(ContextCompat.getDrawable(activity, R.drawable.line_divider));
            lvSubcategory.addItemDecoration(mDivider);
        }
    }

    private void initListSeminar() {
        mRcyPaid.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        mRcyFree.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        mRcyTeacher.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        if (mTop == null) {
            mTop = new DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL);
            mTop.setDrawable(ContextCompat.getDrawable(activity, R.drawable.line_divider_horizontal));
//            mRcyPaid.addItemDecoration(mTop);
            mRcyFree.addItemDecoration(mTop);
            mRcyTeacher.addItemDecoration(mTop);
        }
//        mRcyPaid.addOnItemTouchListener(new RecyclerItemClickListener(activity, mRcyPaid, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                SubCategory subCategory = listSeminarPay.get(position);
//                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
//                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));

        mRcyFree.addOnItemTouchListener(new RecyclerItemClickListener(activity, mRcyFree, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SubCategory subCategory = listSeminarFree.get(position);
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mRcyTeacher.addOnItemTouchListener(new RecyclerItemClickListener(activity, mRcyTeacher, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User teacher = mListUser.get(position);
                if (teacher != null)
                    startActivity(new Intent(getActivity(), TeacherProfileCardActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, teacher));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void getData() {
        HashMap<String, String> params = new HashMap<>();
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, getActivity(), AppConstants.SERVER_PATH.SHOP_TOP.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                if (TextUtils.isEmpty(object.toString()))
                    return;
                // list paid seminar.
//                try {
//                    JSONArray listSemi = object.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_PAY.toString());
//                    listSeminarPay.clear();
//                    if (listSemi.length() > 0) {
//                        for (int i = 0; i < listSemi.length(); i++) {
//                            SubCategory seminarPay = SubCategory.parse(listSemi.getJSONObject(i));
//                            listSeminarPay.add(seminarPay);
//                        }
//                    }
//                    mAdapterSeminarPay.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                // list free seminar.
//                try {
//                    JSONArray listSemi = object.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_FREE.toString());
//                    listSeminarFree.clear();
//                    if (listSemi.length() > 0) {
//                        for (int i = 0; i < listSemi.length(); i++) {
//                            SubCategory seminarPay = SubCategory.parse(listSemi.getJSONObject(i));
//                            listSeminarFree.add(seminarPay);
//                        }
//                    }
//                    mAdapterSeminarFree.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                // list seminar popular.
                try {
                    JSONArray listSemi = object.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_POPULAR.toString());
                    listSeminarFree.clear();
                    if (listSemi.length() > 0) {
                        for (int i = 0; i < listSemi.length(); i++) {
                            SubCategory seminarPay = SubCategory.parse(listSemi.getJSONObject(i));
                            listSeminarFree.add(seminarPay);
                        }
                    }
                    mAdapterSeminarFree.notifyDataSetChanged();
                    mLlViewFree.setVisibility(listSeminarFree.size() > 0 ? View.VISIBLE : View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // list teacher.
                try {
                    JSONArray listSemi = object.getJSONArray(AppConstants.KEY_PARAMS.LIST_TEACHER.toString());
                    mListUser.clear();
                    if (listSemi.length() > 0) {
                        for (int i = 0; i < listSemi.length(); i++) {
                            User seminarPay = User.parse(listSemi.getJSONObject(i));
                            mListUser.add(seminarPay);
                        }
                    }
                    mLlViewTeacher.setVisibility(mListUser.size() > 0 ? View.VISIBLE : View.GONE);
                    mAdapterTeacher.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // list seminar.
                try {
                    JSONArray listSemi = object.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_GENERAL.toString());
                    subCategoryList.clear();
                    if (listSemi.length() > 0) {
                        for (int i = 0; i < listSemi.length(); i++) {
                            SubCategory subCate = SubCategory.parse(listSemi.getJSONObject(i));
                            subCategoryList.add(subCate);
                        }
                    }
                    mLlViewSeminar.setVisibility(subCategoryList.size() > 0 ? View.VISIBLE : View.GONE);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int error) {

            }
        });
    }

    public void attachFragment(Fragment fragment, @IdRes int containerViewID, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(containerViewID, fragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    OnSearchListener onSearchListener;

    public interface OnSearchListener {
        void onSearch(String querry);
    }

    public void setOnCloseSearchListener(OnCloseSearchListener onCloseSearch) {
        this.onCloseSearch = onCloseSearch;
    }

    OnCloseSearchListener onCloseSearch;

    public interface OnCloseSearchListener {
        void onClose();
    }

    protected void registerBroadcast() {
        if (getActivity() == null)
            return;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BROAD_CAST.REFRESH);
        if (mReceive == null) {
            mReceive = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null != intent.getAction()) {
                        if (intent.getAction().equalsIgnoreCase(AppConstants.BROAD_CAST.REFRESH)) {
                            getData();
                        }
                    }
                }
            };
        }
        getActivity().registerReceiver(mReceive, intentFilter);
    }

    protected void unRegisterBroadcast() {
        if (getActivity() == null || mReceive == null)
            return;
        getActivity().unregisterReceiver(mReceive);

    }

    /**
     * Show copy right in bottom with 2 case :
     * 1 >  empty list.
     * 2 > item not full screen.
     *
     * @param showBottom
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }

}
