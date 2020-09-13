package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Point;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.HSSPreference;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.PointAdapter;

import static jp.co.hiropro.seminar_hall.ForestApplication.LICENSE_KEY;
import static jp.co.hiropro.seminar_hall.ForestApplication.MERCHANT_ID;

public class PointManagerActivity extends BaseActivity implements Paginate.Callbacks {
    @BindView(R.id.rcy_point)
    RecyclerView mRcyPoint;
    @BindView(R.id.tv_point)
    TextViewApp mTvPoint;
    @BindView(R.id.rcy_main)
    NestedScrollView mRcyMain;
    private DividerItemDecoration mDivider = null;
    private ArrayList<Point> mListPoint = new ArrayList<>();
    private PointAdapter mAdapter;
    private int page = 1;
    private boolean isLoading;
    private BillingProcessor mBilling;
    private Point mPoint = null;
    private boolean mIsShowBack = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_point_manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_screen_point));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mIsShowBack = bundle.getBoolean(AppConstants.KEY_INTENT.SHOW_BACK.toString(), false);
        btnBack.setVisibility(mIsShowBack ? View.VISIBLE : View.INVISIBLE);
        mAdapter = new PointAdapter(mListPoint, false, false);
        mRcyPoint.setLayoutManager(new LinearLayoutManager(this));
        mRcyPoint.setAdapter(mAdapter);
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider_aaaaaa)));
            mRcyPoint.addItemDecoration(mDivider);
        }
        mRcyPoint.addOnItemTouchListener(new RecyclerItemClickListener(PointManagerActivity.this, mRcyPoint, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isLoading) {
                    Point item = mListPoint.get(mRcyPoint.getChildAdapterPosition(view));
                    mPoint = item;
                    int currentUserPoint = user.getPoint();
                    if (item.getPoint() + currentUserPoint > 50000) {
                        HSSDialog.show(PointManagerActivity.this, "残高が５万ポイントを超える購入はできません");
                    } else {
                        if (AppConstants.isTestMode)
                            mBilling.purchase(PointManagerActivity.this, "android.test.purchased");
                        else mBilling.purchase(PointManagerActivity.this, item.getCode());
                    }
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        Paginate.with(mRcyPoint, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        initBilling();
    }

    @Override
    public void onLoadMore() {
        getListPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = User.getInstance().getCurrentUser();
//        mTvPoint.setText(Utils.formatNormalPrice(user.getPoint()));
        AppUtils.updatePoint(PointManagerActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBilling.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.tv_go_top, R.id.tv_go_settlement_law})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go_top:
                startActivity(new Intent(activity, SpecifiedCommercialTransactionActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(AppConstants.KEY_INTENT.SHOW_BACK.toString(), true));
                break;
            case R.id.tv_go_settlement_law:
                startActivity(new Intent(PointManagerActivity.this, SettlementLawActivity.class));
                break;
        }
    }

    private void initBilling() {
        mBilling = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                /**
                 * TODO : Hàm này để tránh việc consume khi mua subcribe.
                 * Khi mua subcribe không được consume.
                 */
                mBilling.consumePurchase(productId);
                // create object data.
                JSONObject object = new JSONObject();
                try {
                    object.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getId());
                    object.put(AppConstants.KEY_PARAMS.PACKAGE_ID.toString(), mPoint.getId());
                    object.put(AppConstants.KEY_PARAMS.COST_PRICE.toString(), mPoint.getMoney());
                    object.put(AppConstants.KEY_PARAMS.COST_POINT.toString(), mPoint.getPoint());
                    object.put(AppConstants.KEY_PARAMS.CURRENCY.toString(), mPoint.getCurrency());
                    if (details.purchaseInfo != null) {
                        if (details.purchaseInfo.responseData != null) {
                            String response = details.purchaseInfo.responseData;
                            object.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), response);
//                            if (response.length() > 0) {
//                                JSONObject objectResponse = new JSONObject(response);
//                                String token = objectResponse.optString("purchaseToken", "");
//                                object.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), token);
//                            }
                        }
                    } else {
                        object.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray array = new JSONArray();
                array.put(object);
                sendRequestToServer(array, object);
            }

            @Override
            public void onPurchaseHistoryRestored() {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(PointManagerActivity.this, getResources().getString(R.string.txt_cancel_purchase), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {

            }
        });
    }

    /**
     * Send request update purchase to server.
     *
     * @param array
     */
    private void sendRequestToServer(final JSONArray array, final JSONObject object) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        final JSONObject data = new JSONObject();
        try {
            data.put(AppConstants.KEY_PARAMS.OS_TYPE.toString(), "2");
            data.put(AppConstants.KEY_PARAMS.LIST.toString(), array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put(AppConstants.KEY_PARAMS.DATA.toString(), "" + data);
        RequestDataUtils.requestData(Request.Method.GET, PointManagerActivity.this, AppConstants.SERVER_PATH.UPDATE_PURCHASE.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                if (object.length() > 0) {
                    try {
                        JSONObject dataPoint = object.getJSONObject(AppConstants.KEY_PARAMS.MY_POINT.toString());
                        if (dataPoint.length() > 0) {
                            int point = dataPoint.optInt(AppConstants.KEY_PARAMS.POINT.toString(), 0);
                            user.setPoint(point);
                            mTvPoint.setText(Utils.formatNormalPrice(point));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismissLoading();
                mRcyMain.scrollTo(0, 0);
            }

            @Override
            public void onFail(int error) {
                dismissLoading();
                String listMissing = HSSPreference.getInstance(PointManagerActivity.this).getString(AppConstants.KEY_PREFERENCE.DATA_POINT.toString(), "");
                if (listMissing.length() > 0) {
                    try {
                        array.put(object);
                        array.put(array.length(), object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HSSPreference.getInstance(PointManagerActivity.this).putString(AppConstants.KEY_PREFERENCE.DATA_POINT.toString(), "" + array);
                } else {
                    HSSPreference.getInstance(PointManagerActivity.this).putString(AppConstants.KEY_PREFERENCE.DATA_POINT.toString(), "" + array);
                }
                HSSDialog.show(PointManagerActivity.this, getString(R.string.msg_cannot_update_point)).show();
            }
        });
    }

    private void getListPoint() {
        isLoading = true;
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        RequestDataUtils.requestData(Request.Method.GET, PointManagerActivity.this,
                AppConstants.SERVER_PATH.GET_POINT.toString(), params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        //Update point.
                        try {
                            JSONObject point = object.getJSONObject(AppConstants.KEY_PARAMS.MY_POINT.toString());
                            int pointValue = point.optInt(AppConstants.KEY_PARAMS.POINT.toString(), 0);
                            user.setPoint(pointValue);
                            mTvPoint.setText(Utils.formatNormalPrice(pointValue));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray listPoint = object.getJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                            if (listPoint.length() > 0) {
                                for (int i = 0; i < listPoint.length(); i++) {
                                    mListPoint.add(Point.parserData(listPoint.getJSONObject(i)));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dismissLoading();
                        page = 0;
                        isLoading = false;
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(int error) {
                        dismissLoading();
                        page = 0;
                        isLoading = false;
                        mAdapter.notifyDataSetChanged();
                    }
                });
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
