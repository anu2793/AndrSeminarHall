package jp.co.hiropro.seminar_hall.controller.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.fragment.SearchResultFragment;
import jp.co.hiropro.seminar_hall.model.Campaign;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.model.SubCategory;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.AutoScrollViewPager;
import jp.co.hiropro.seminar_hall.view.LoopingCirclePageIndicator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.NewCategoryAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.NewsTopAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.SeminarAdapter;
import jp.co.hiropro.seminar_hall.view.adapter.ViewPagerAdapter;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollState;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.leolin.shortcutbadger.ShortcutBadger;

import static jp.co.hiropro.seminar_hall.ForestApplication.LICENSE_KEY;

public class TopActivity extends BaseActivity {
    @BindView(R.id.view_pager)
    AutoScrollViewPager viewPager;
    @BindView(R.id.view_indicator)
    LoopingCirclePageIndicator viewIndicator;
    @BindView(R.id.lv_category)
    RecyclerView lvCategory;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.layout_search_view)
    RelativeLayout layoutSearchBar;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.layout_content_container)
    LinearLayout layoutContentContainer;
    @BindView(R.id.layout_search)
    FrameLayout layoutSearch;
    @BindView(R.id.btn_content_free)
    ImageView ivContentFree;
    @BindView(R.id.btn_diagnosis)
    ImageView ivDiagnosis;
    @BindView(R.id.tv_list_news)
    TextViewApp tvListNews;
    @BindView(R.id.appbar)
    AppBarLayout searchContainer;
    @BindView(R.id.lv_new)
    RecyclerView mRcyNews;
    @BindView(R.id.ll_new)
    LinearLayout mLlNew;
    @BindView(R.id.lv_seminar_top)
    RecyclerView mRcySeminarTop;
    @BindView(R.id.lv_seminar_new)
    RecyclerView mRcySeminarNew;
    @BindView(R.id.ll_seminar_new)
    LinearLayout mLlSeminarNew;
    @BindView(R.id.ll_seminar_suggest_top)
    LinearLayout mLlSeminarSuggest;
    @BindView(R.id.ll_category)
    LinearLayout mLlCategory;
    @BindView(R.id.tv_news)
    TextViewApp tvNews;
    @BindView(R.id.lnWrapnews)
    LinearLayout lnWrapnews;
    @BindView(R.id.tvRemian)
    TextView tvRemian;

    private IOverScrollDecor decor;
    private List<Campaign> campaignList = new ArrayList<>();
    private ViewPagerAdapter adapterPager;
    private List<SubCategory> categoryList = new ArrayList<>();
    private List<SubCategory> categoryRecomend = new ArrayList<>();
    private List<SubCategory> categoryNews = new ArrayList<>();
    private NewCategoryAdapter categoryAdapter;
    private String mUrlDiagnosis = "";
    private DividerItemDecoration mDivider = null, mCateDivider = null, mTop = null;
    private Timer timer = new Timer();
    private BillingProcessor mPurchaseService;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private String mIdPurchase = "";
    private ArrayList<NewsItem> listNew = new ArrayList<>();
    private NewsTopAdapter mAdapterNew;

    SearchResultFragment searchResultFragment;
    ImageView closeIcon;
    NewsItem newsItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupOverScroll();
        setupSearchView();
        initButton();
        // Get id campain.
        // Using for deep link.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString(AppConstants.KEY_SEND.KEY_ID_CAMPAIGN, "");
            if (id.length() > 0) {
                VideoDetail detail = new VideoDetail();
                detail.setId(Integer.valueOf(id));
                startActivity(new Intent(TopActivity.this, ContentDetailActivity.class).
                        putExtra(KeyParser.KEY.DATA.toString(), detail));
            }
            mIdPurchase = bundle.getString(AppConstants.KEY_SEND.KEY_SEND_ID_PURCHASE, "");
        }
        initBilling();
        initListNew();
        initListSeminar();
        mAdapterNew = new NewsTopAdapter(TopActivity.this, listNew);
        mRcyNews.setAdapter(mAdapterNew);
    }

    private void initListNew() {
        mRcyNews.setLayoutManager(new LinearLayoutManager(TopActivity.this));
//        if (mDivider == null) {
//            mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//            mDivider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider_gray));
//            mRcyNews.addItemDecoration(mDivider);
//        }
        mRcyNews.addOnItemTouchListener(new RecyclerItemClickListener(TopActivity.this, mRcyNews, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsItem item = listNew.get(position);
                startActivity(new Intent(TopActivity.this, NewDetailActivity.class).putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, item));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void initListSeminar() {
        mRcySeminarTop.setLayoutManager(new LinearLayoutManager(TopActivity.this, LinearLayout.HORIZONTAL, false));
        mRcySeminarNew.setLayoutManager(new LinearLayoutManager(TopActivity.this, LinearLayout.HORIZONTAL, false));
        if (mTop == null) {
            mTop = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
            mTop.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider_horizontal)));
            mRcySeminarTop.addItemDecoration(mTop);
            mRcySeminarNew.addItemDecoration(mTop);
        }
        mRcySeminarTop.addOnItemTouchListener(new RecyclerItemClickListener(TopActivity.this, mRcySeminarTop, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SubCategory subCategory = categoryRecomend.get(position);
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mRcySeminarNew.addOnItemTouchListener(new RecyclerItemClickListener(TopActivity.this, mRcySeminarNew, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SubCategory subCategory = categoryNews.get(position);
                startActivity(new Intent(activity, SubCategoryDetailActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), subCategory));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onLogoAppClick() {
        super.onLogoAppClick();
        scrollView.scrollTo(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Dùng để check người dùng đã cancel gói mua đấy hay chưa.
         * Chúy ý : Nếu chưa mua gói hàng đó lần nào . hàm getSubscriptionTransactionDetails trả về giá trị null.
         */
        getData();
        if (mIdPurchase.length() > 0) {
            updateStatusPurchase(mIdPurchase);
        }
        // Using update point.
        AppUtils.updatePoint(TopActivity.this);
    }

    @Override
    protected void onDestroy() {
        if (mPurchaseService != null)
            mPurchaseService.release();
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_top;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            layoutSearchBar.setVisibility(View.GONE);
            ImageView closeIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            closeIcon.performClick();
            searchResultFragment = null;
            tvCancel.setVisibility(View.GONE);
            layoutContentContainer.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.GONE);
//            setupOverScroll();
        } else
            super.onBackPressed();
    }

    @OnClick({R.id.tv_cancel, R.id.btn_content_free, R.id.tv_list_news, R.id.btn_diagnosis, R.id.imv_profile, R.id.tv_go_seminar_list, R.id.tv_go_new_list, R.id.tv_list_new, R.id.layout_top_news})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                layoutSearchBar.setVisibility(View.GONE);
                ImageView closeIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
                closeIcon.performClick();
                searchResultFragment = null;
                tvCancel.setVisibility(View.GONE);
                layoutContentContainer.setVisibility(View.VISIBLE);
                layoutSearch.setVisibility(View.GONE);
                setupOverScroll();
                searchView.clearFocus();
                AppUtils.hiddenKeyboard(TopActivity.this);
                break;
            case R.id.btn_content_free:
                startActivity(new Intent(TopActivity.this, RecommendFreeContentActivity.class));
                break;
            case R.id.tv_go_new_list:
                startActivity(new Intent(TopActivity.this, NewsActivity.class));
                break;
            case R.id.tv_list_new:
                startActivity(new Intent(TopActivity.this, NewsActivity.class));
                break;
            case R.id.layout_top_news:
                startActivityForResult(new Intent(TopActivity.this, NewDetailActivity.class)
                        .putExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT, newsItem), 103);
                break;
            case R.id.btn_diagnosis:
                String urlSend = "";
                /**
                 * TODO : Neu la user skip thi luon luon la man hinh cau hoi.
                 */
                if (user.isSkipUser()) {
                    urlSend = mUrlDiagnosis;
                } else {
                    if (mUrlDiagnosis.length() > 0) {
                        if (AppConstants.getUrlResultQuestion().length() > 0 && AppConstants.getUrlResultQuestion().contains("diagnosisresult?")) {
                            urlSend = AppConstants.getUrlResultQuestion();
                        } else {
                            urlSend = mUrlDiagnosis;
                        }
                    }
                }
                startActivity(new Intent(TopActivity.this, DiagnosisActivity.class).putExtra(AppConstants.KEY_INTENT.URL_DIAGNOSIS.toString(), urlSend));
                break;
            case R.id.imv_profile:
                startActivity(new Intent(TopActivity.this, JackProfileActivity.class));
                break;

            case R.id.tv_go_seminar_list:
                startActivity(new Intent(activity, ShopContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(AppConstants.KEY_SEND.KEY_POSITION_TAB, 1));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 103) {
                getData();
            }
        }
        if (!mPurchaseService.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViewPager() {
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (718 * screenSize.x) / 1242));
        viewPager.startAutoScroll();
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        adapterPager = new ViewPagerAdapter(campaignList, this);
        viewPager.setAdapter(adapterPager);
        viewIndicator.setViewPager(viewPager);
//        viewIndicator.setRadius(Utils.convertDpToPx(this, 3));
//        viewIndicator.setPosition(0);
    }

    private void initListCategory() {
        categoryAdapter = new NewCategoryAdapter(TopActivity.this, categoryList);
        lvCategory.setLayoutManager(new LinearLayoutManager(TopActivity.this));
        lvCategory.setAdapter(categoryAdapter);
        if (mCateDivider == null) {
            mCateDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            mCateDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider)));
            lvCategory.addItemDecoration(mCateDivider);
        }
        categoryAdapter.setOnItemClickListener(new NewCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SubCategory category) {
                startActivity(new Intent(TopActivity.this, SubSubCategoryActivity.class)
                        .putExtra(KeyParser.KEY.DATA.toString(), category));
            }
        });
    }

    private void initButton() {
        int width = (int) (screenSize.x / 2 - Utils.convertDpToPx(this, 15));
        int height = width * 356 / 573;
        LinearLayout.LayoutParams layoutParamsContent = new LinearLayout.LayoutParams(width, height);
        layoutParamsContent.leftMargin = (int) Utils.convertDpToPx(this, 5);

        LinearLayout.LayoutParams layoutParamsDiagnosis = new LinearLayout.LayoutParams(width, height);
        layoutParamsDiagnosis.rightMargin = (int) Utils.convertDpToPx(this, 5);

        ivContentFree.setLayoutParams(layoutParamsContent);
        ivDiagnosis.setLayoutParams(layoutParamsDiagnosis);
    }

    private void setupOverScroll() {
        decor = OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        Log.e("Size: ", String.valueOf(Utils.convertDpToPx(activity, 60)));
        decor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(final IOverScrollDecor decor, int state, float offset) {
                if (state == IOverScrollState.STATE_DRAG_START_SIDE && offset >= 15) {

                    ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutSearchBar,
                            "translationY", -Utils.convertDpToPx(activity, 60), 0);
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

    private void setupSearchView() {
        closeIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeIcon.setEnabled(false);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
                searchIcon.performClick();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Event click", "Search");
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                tvCancel.setVisibility(View.VISIBLE);
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
                        runOnUiThread(new Runnable() {
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
                                        searchResultFragment = SearchResultFragment.newInstance(newText);
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
    }


    private void initBilling() {
        mPurchaseService = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, final TransactionDetails details) {
                mPurchaseService.consumePurchase(productId);
            }

            @Override
            public void onPurchaseHistoryRestored() {
                for (String sku : mPurchaseService.listOwnedProducts())
                    Log.d("FOREST", "Owned Managed Product: " + sku);
                for (String sku : mPurchaseService.listOwnedSubscriptions())
                    Log.d("FOREST", "Owned Subscription: " + sku);
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(TopActivity.this, getResources().getString(R.string.txt_cancel_purchase), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {

            }
        });
    }

    private void updateStatusPurchase(String mIdPurchase) {
        if (mPurchaseService != null) {
            String subcription_id = "";
            String order_id = "";
            boolean auto = false;
            String mCurrency = "";
            double mCost = 0;
            String token = "";
            String response = "";
            if (mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase) != null) {
                Log.e("Detail", "values :" + mPurchaseService.getSubscriptionListingDetails(mIdPurchase) + "-- Time duration : " + mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase));
                response = mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase).purchaseInfo.responseData;
                String packageName = "";
                try {
                    JSONObject object = new JSONObject(response);
                    token = object.optString("purchaseToken", "");
                    packageName = object.optString("packageName", "");
                    subcription_id = object.optString("productId", "");
                    order_id = object.optString("orderId", "");
                    int status = object.optInt("purchaseState", 1);
                    auto = object.optBoolean("autoRenewing", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (mPurchaseService.getSubscriptionListingDetails(mIdPurchase) != null) {
                mCurrency = mPurchaseService.getSubscriptionListingDetails(mIdPurchase).currency;
                mCost = mPurchaseService.getSubscriptionListingDetails(mIdPurchase).priceValue;
            }
            if (subcription_id.length() > 0 && mCurrency.length() > 0)
                sendRequestUpdate(subcription_id, order_id, mCost, mCurrency, auto, token, response);
        }
    }

    private void sendRequestUpdate(String code_product, String order_no, double cost, String current, boolean auto, String token, String response) {
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.PREMIUM_CODE.toString(), code_product);
        params.put(AppConstants.KEY_PARAMS.ORDER_NO.toString(), order_no);
        params.put(AppConstants.KEY_PARAMS.COST.toString(), String.valueOf(cost));
        params.put(AppConstants.KEY_PARAMS.CURRENCY.toString(), current);
        params.put(AppConstants.KEY_PARAMS.STATUS.toString(), String.valueOf(auto ? "1" : "0"));
        params.put(AppConstants.KEY_PARAMS.PURCHASE_TOKEN.toString(), token);
        params.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), response);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.UPDATE_STATUS_PURCHASE.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    JSONObject objectPremium = objectData.getJSONObject(AppConstants.KEY_PARAMS.MY_PREMIUM.toString());
                                    if (objectPremium.length() > 0) {
                                        int remain_date = objectPremium.optInt(AppConstants.KEY_PARAMS.PREMIUM_REMAIN.toString(), -1);
                                        User.getInstance().getCurrentUser().setRemainPremium(remain_date);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {

                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("error", "values :" + error.toString());
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void getData() {
        LoadingDialog.getDialog(this).show();
        HashMap<String, String> params = new HashMap<>();
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, TopActivity.this, AppConstants.SERVER_PATH.TOP.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject data, String msg) {
                LoadingDialog.getDialog(TopActivity.this).dismiss();
                campaignList.clear();
                categoryList.clear();
                // Update number remain.
                int numberRemain = data.optInt(AppConstants.KEY_PARAMS.REMAIN.toString(), 0);
                ShortcutBadger.applyCount(getApplicationContext(), numberRemain);
                tvRemian.setText("未読メッセージが" + numberRemain + "件あります！」");
                if (data.length() > 0) {
                    mUrlDiagnosis = data.optString("diagnosis_link");
                    if (mUrlDiagnosis.length() > 0)
                        AppConstants.setUrlResultQuestion(mUrlDiagnosis);
                }
                //Campaign
                try {
                    JSONArray campaign = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_CAMPAIGN.toString());
                    if (campaign.length() > 0) {
                        for (int i = 0; i < campaign.length(); i++) {
                            JSONObject item = campaign.getJSONObject(i);
                            campaignList.add(Campaign.parse(item));
                        }
                        initViewPager();
                    } else {
                        viewPager.setVisibility(View.GONE);
                        viewIndicator.setVisibility(View.GONE);
                    }
                    //News
//                    JSONObject news = data.getJSONObject("news");
//                    if (news.length() > 0) {
//                        newsItem = NewsItem.parser(news);
//                        tvNews.setText(news.optString(KeyParser.KEY.TITLE.toString()));
//                    } else {
//                        lnWrapnews.setVisibility(View.GONE);
//                    }
                    //news  top
                    JSONArray news = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_NEWS.toString());
                    if (news.length() > 0) {
                        listNew.clear();
                        lnWrapnews.setVisibility(View.VISIBLE);
                        for (int i = 0; i < news.length(); i++) {
                            if (i < 2) {
                                NewsItem item = NewsItem.parser(news.getJSONObject(i));
                                listNew.add(item);
                            }
                        }
                        mAdapterNew.notifyDataSetChanged();
                    } else {
                        lnWrapnews.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // NEW block.
//                int isShowNew = data.optInt(AppConstants.KEY_PARAMS.HIDE_NEWS.toString(), 0);
//                mLlNew.setVisibility(isShowNew > 0 ? View.VISIBLE : View.GONE);
//                if (isShowNew > 0) {
//                    listNew.clear();
//                    //News
//                    try {
//                        JSONArray news = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_NEWS.toString());
//                        mRcyNews.setVisibility(news.length() > 0 ? View.VISIBLE : View.GONE);
//                        if (news.length() > 0) {
//                            for (int i = 0; i < news.length(); i++) {
//                                NewsItem item = NewsItem.parser(news.getJSONObject(i));
//                                listNew.add(item);
//                            }
//                        }
//                        mAdapterNew.notifyDataSetChanged();
//                        mLlNew.setVisibility(listNew.size() > 0 ? View.VISIBLE : View.GONE);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }

                //Category
                try {
                    JSONArray category = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_CATEGORY.toString());
                    if (category.length() > 0) {
                        for (int i = 0; i < category.length(); i++) {
                            JSONObject item = category.getJSONObject(i);
                            categoryList.add(SubCategory.parserJson(item));
                        }
                        initListCategory();
                    }
                    mLlCategory.setVisibility(categoryList.size() > 0 ? View.VISIBLE : View.GONE);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                //list seminar new.
                try {
                    JSONArray seminarNew = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_NEWS.toString());
                    categoryNews.clear();
                    if (seminarNew.length() > 0) {
                        for (int i = 0; i < seminarNew.length(); i++) {
                            JSONObject item = seminarNew.getJSONObject(i);
                            categoryNews.add(SubCategory.parserDataSub(item));
                        }
                        initSeminarNew();
                    }
                    mLlSeminarNew.setVisibility(categoryNews.size() > 0 ? View.VISIBLE : View.GONE);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //list seminar suggest.
                try {
                    JSONArray seminarSuggest = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_SUGGEST.toString());
                    categoryRecomend.clear();
                    if (seminarSuggest.length() > 0) {
                        for (int i = 0; i < seminarSuggest.length(); i++) {
                            JSONObject item = seminarSuggest.getJSONObject(i);
                            categoryRecomend.add(SubCategory.parserDataSub(item));
                        }
                        createSuggestSeminar();
                    }
                    mLlSeminarSuggest.setVisibility(categoryRecomend.size() > 0 ? View.VISIBLE : View.GONE);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //Update profile.
//                                int statusUpdate = data.optInt(AppConstants.KEY_PARAMS.CHECK_UPDATE_PROFILE.toString(), 0);
//                                if (statusUpdate == 0)
//                                    showAdviseDialog();
            }

            @Override
            public void onFail(int error) {
                LoadingDialog.getDialog(TopActivity.this).dismiss();
                if (error == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                    String mess = getString(R.string.msg_server_maintain);
                    goMaintainScreen(activity, mess);
                } else if (error == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                    sessionExpire();
                } else {
                    HSSDialog.show(activity, getString(R.string.error_io_exception));
                }
            }
        });
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.TOP.toString(), new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        LoadingDialog.getDialog(TopActivity.this).dismiss();
//
//                        try {
//                            int status = response.optInt(KeyParser.KEY.STATUS.toString());
//                            if (status == AppConstants.REQUEST_SUCCESS) {
//                                campaignList.clear();
//                                categoryList.clear();
//                                JSONObject data = response.optJSONObject(KeyParser.KEY.DATA.toString());
//                                // Update number remain.
//                                int numberRemain = data.optInt(AppConstants.KEY_PARAMS.REMAIN.toString(), 0);
//                                ShortcutBadger.applyCount(getApplicationContext(), numberRemain);
//                                if (data.length() > 0) {
//                                    mUrlDiagnosis = data.optString("diagnosis_link");
//                                    if (mUrlDiagnosis.length() > 0)
//                                        AppConstants.setUrlResultQuestion(mUrlDiagnosis);
//                                }
//                                //Campaign
//                                JSONArray campaign = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_CAMPAIGN.toString());
//                                if (campaign.length() > 0) {
//                                    for (int i = 0; i < campaign.length(); i++) {
//                                        JSONObject item = campaign.getJSONObject(i);
//                                        campaignList.add(Campaign.parse(item));
//                                    }
//                                    initViewPager();
//                                } else {
//                                    viewPager.setVisibility(View.GONE);
//                                    viewIndicator.setVisibility(View.GONE);
//                                }
//                                // NEW block.
//                                int isShowNew = data.optInt(AppConstants.KEY_PARAMS.HIDE_NEWS.toString(), 0);
//                                mLlNew.setVisibility(isShowNew > 0 ? View.VISIBLE : View.GONE);
//                                if (isShowNew > 0) {
//                                    listNew.clear();
//                                    //News
//                                    try {
//                                        JSONArray news = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_NEWS.toString());
//                                        mRcyNews.setVisibility(news.length() > 0 ? View.VISIBLE : View.GONE);
//                                        if (news.length() > 0) {
//                                            for (int i = 0; i < news.length(); i++) {
//                                                NewsItem item = NewsItem.parser(news.getJSONObject(i));
//                                                listNew.add(item);
//                                            }
//                                        }
//                                        mAdapterNew.notifyDataSetChanged();
//                                        mLlNew.setVisibility(listNew.size() > 0 ? View.VISIBLE : View.GONE);
//                                    } catch (Exception ex) {
//                                        ex.printStackTrace();
//                                    }
//                                }
//                                //Category
//                                try {
//                                    JSONArray category = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_CATEGORY.toString());
//                                    if (category.length() > 0) {
//                                        for (int i = 0; i < category.length(); i++) {
//                                            JSONObject item = category.getJSONObject(i);
//                                            categoryList.add(SubCategory.parserJson(item));
//                                        }
//                                        initListCategory();
//                                    }
//                                    mLlCategory.setVisibility(categoryList.size() > 0 ? View.VISIBLE : View.GONE);
//                                } catch (JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//
//                                //list seminar new.
//                                try {
//                                    JSONArray seminarNew = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_NEWS.toString());
//                                    categoryNews.clear();
//                                    if (seminarNew.length() > 0) {
//                                        for (int i = 0; i < seminarNew.length(); i++) {
//                                            JSONObject item = seminarNew.getJSONObject(i);
//                                            categoryNews.add(SubCategory.parserDataSub(item));
//                                        }
//                                        initSeminarNew();
//                                    }
//                                    mLlSeminarNew.setVisibility(categoryNews.size() > 0 ? View.VISIBLE : View.GONE);
//
//                                } catch (JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//                                //list seminar suggest.
//                                try {
//                                    JSONArray seminarSuggest = data.getJSONArray(AppConstants.KEY_PARAMS.LIST_SEMINAR_SUGGEST.toString());
//                                    categoryRecomend.clear();
//                                    if (seminarSuggest.length() > 0) {
//                                        for (int i = 0; i < seminarSuggest.length(); i++) {
//                                            JSONObject item = seminarSuggest.getJSONObject(i);
//                                            categoryRecomend.add(SubCategory.parserDataSub(item));
//                                        }
//                                        createSuggestSeminar();
//                                    }
//                                    mLlSeminarSuggest.setVisibility(categoryRecomend.size() > 0 ? View.VISIBLE : View.GONE);
//                                } catch (JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//                                //Update profile.
////                                int statusUpdate = data.optInt(AppConstants.KEY_PARAMS.CHECK_UPDATE_PROFILE.toString(), 0);
////                                if (statusUpdate == 0)
////                                    showAdviseDialog();
//                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
//                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
//                                goMaintainScreen(activity, mess);
//                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
//                                sessionExpire();
//                            } else {
//                                HSSDialog.show(activity, getString(R.string.error_io_exception));
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                NetworkUtils.showDialogError(TopActivity.this, error);
//            }
//        });
//        request.setHeaders(getAuthHeader());
//        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void initSeminarNew() {
        SeminarAdapter mAdapter = new SeminarAdapter(categoryNews, false, false, TopActivity.this);
        mRcySeminarNew.setAdapter(mAdapter);
    }

    private void createSuggestSeminar() {
        SeminarAdapter mAdapter = new SeminarAdapter(categoryRecomend, false, false, TopActivity.this);

        mRcySeminarTop.setAdapter(mAdapter);
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

}
