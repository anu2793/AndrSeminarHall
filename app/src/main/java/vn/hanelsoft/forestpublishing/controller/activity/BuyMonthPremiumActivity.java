package vn.hanelsoft.forestpublishing.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.PackageItem;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

import static vn.hanelsoft.forestpublishing.ForestApplication.LICENSE_KEY;
import static vn.hanelsoft.forestpublishing.R.id.tv_time_values;

/**
 * Created by dinhdv on 8/4/2017.
 */

public class BuyMonthPremiumActivity extends BaseActivity {
    @BindView(R.id.swipeReNew)
    ImageView mWipeReNewal;
    @BindView(R.id.tv_time)
    TextViewApp mTvNamePackage;
    @BindView(tv_time_values)
    TextViewApp mTvTimeEnd;
    @BindView(R.id.tv_no_purchase)
    TextViewApp mTvNoPurchase;
    @BindView(R.id.tv_description)
    TextViewApp mTvTitle;
    @BindView(R.id.tv_time_begin)
    TextViewApp mTvEndPackage;
    private ProgressDialog mPrg;
    private BillingProcessor mPurchaseService;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private int mIdPurchase;
    private String mCodePurchase = "";
    private int mStatus = AppConstants.STATUS_USER_PURCHASE.FREE;
//    private boolean mGoGooglePlay = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_month_premium;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        btnBack.setVisibility(View.VISIBLE);
        setupTitleScreen("月額課金情報");
        // Progress Bar
        mPrg = new ProgressDialog(BuyMonthPremiumActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        sendRequestGet();
        initBilling();
        // if User is premium. Enable switch user.
        mWipeReNewal.setEnabled(user.isPremiumUser());
    }

    @OnClick({R.id.rl_buy, R.id.swipeReNew})
    public void OnClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rl_buy:
                switch (mStatus) {
                    case AppConstants.STATUS_USER_PURCHASE.FREE:
                        buyMonthlyPurchase();
                        break;
                    case AppConstants.STATUS_USER_PURCHASE.BOUGHT:
                        HSSDialog.show(BuyMonthPremiumActivity.this, getString(R.string.msg_has_bought_this_monthly_package));
                        break;
                    case AppConstants.STATUS_USER_PURCHASE.UPDATE:
                        HSSDialog.show(BuyMonthPremiumActivity.this, getString(R.string.msg_has_bought_update_package));
                        break;
                }
                break;
            case R.id.swipeReNew:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=vn.hanelsoft.forestpublishing"));
                startActivity(intent);
//                mGoGooglePlay = true;
//                mWipeReNewal.setImageResource(R.mipmap.ic_switch_purchase_enable : R.mipmap.ic_switch_purchase_disable);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Dùng để check người dùng đã cancel gói mua đấy hay chưa.
         * Chúy ý : Nếu chưa mua gói hàng đó lần nào . hàm getSubscriptionTransactionDetails trả về giá trị null.
         */
        if (mCodePurchase.length() > 0) {
            updateStatusPurchase(mCodePurchase);
        }
    }

    private void updateStatusPurchase(String mIdPurchase) {
        if (mPurchaseService != null) {
            String subcription_id = "";
            String order_id = "";
            boolean auto = false;
            String mCurrency = "";
            String token = "";
            double mCost = 0;
            if (mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase) != null) {
                Log.e("Detail", "values :" + mPurchaseService.getSubscriptionListingDetails(mIdPurchase) + "-- Time duration : " + mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase));
                String response = mPurchaseService.getSubscriptionTransactionDetails(mIdPurchase).purchaseInfo.responseData;

//                String packageName = "";
                try {
                    JSONObject object = new JSONObject(response);
                    token = object.optString("purchaseToken", "");
//                    packageName = object.optString("packageName", "");
                    subcription_id = object.optString("productId", "");
                    order_id = object.optString("orderId", "");
//                    int status = object.optInt("purchaseState", 1);
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
                sendRequestUpdate(subcription_id, order_id, mCost, mCurrency, auto, token);
        }
    }

    private void sendRequestUpdate(String code_product, String order_no, double cost, String current, boolean auto, String token) {
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.PREMIUM_CODE.toString(), code_product);
        params.put(AppConstants.KEY_PARAMS.ORDER_NO.toString(), order_no);
        params.put(AppConstants.KEY_PARAMS.COST.toString(), String.valueOf(cost));
        params.put(AppConstants.KEY_PARAMS.CURRENCY.toString(), current);
        params.put(AppConstants.KEY_PARAMS.STATUS.toString(), String.valueOf(auto ? "1" : "0"));
        params.put(AppConstants.KEY_PARAMS.PURCHASE_TOKEN.toString(), token);
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


    protected void buyMonthlyPurchase() {
        if (AppConstants.isTestMode)
            mPurchaseService.purchase(BuyMonthPremiumActivity.this, "android.test.purchased");
        else
            mPurchaseService.subscribe(BuyMonthPremiumActivity.this, String.valueOf(mCodePurchase));
    }

    protected void setUserType(boolean isFreeUser) {
        mTvNamePackage.setVisibility(isFreeUser ? View.INVISIBLE : View.VISIBLE);
        mTvTimeEnd.setVisibility(isFreeUser ? View.INVISIBLE : View.VISIBLE);
        mTvNoPurchase.setVisibility(isFreeUser ? View.VISIBLE : View.INVISIBLE);
        mWipeReNewal.setEnabled(user.isPremiumUser());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPurchaseService.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mPurchaseService != null)
            mPurchaseService.release();
        super.onDestroy();
    }

    private void initBilling() {
        mPurchaseService = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, final TransactionDetails details) {
                if (AppConstants.isTestMode)
                    mPurchaseService.consumePurchase(productId);
                HSSDialog.show(BuyMonthPremiumActivity.this, getResources().getString(R.string.txt_buy_monthly_package_success), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendRequestToServer(details);
                    }
                }).show();
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
                Toast.makeText(BuyMonthPremiumActivity.this, getResources().getString(R.string.txt_cancel_purchase), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {

            }
        });
    }

    /**
     * Update status of user to server.
     * if done : Update status of User object static.
     * if fail : Save this status. Push again to server when run splash screen.
     */
    private void sendRequestToServer(final TransactionDetails transactionDetails) {
        showLoading();
        String formatDate = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        String currentDate = sdf.format(new Date());
        String time = (AppConstants.isTestMode ? currentDate : AppUtils.getDate(transactionDetails.purchaseTime.getTime(), formatDate));
        Map<String, String> params = new HashMap();
        params.put(AppConstants.KEY_PARAMS.PREMIUM_ID.toString(), String.valueOf(mIdPurchase));
        params.put(AppConstants.KEY_PARAMS.DATE_FROM.toString(), time);
        params.put(AppConstants.KEY_PARAMS.DATE_TO.toString(), time);
        if (transactionDetails.purchaseInfo != null) {
            if (transactionDetails.purchaseInfo.responseData != null)
                params.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), String.valueOf(transactionDetails.purchaseInfo.responseData));
        }
        String currency = "";
        Double cost = 0.0;
        if (mCodePurchase.length() > 0 && mPurchaseService.getSubscriptionListingDetails(mCodePurchase) != null) {
            currency = mPurchaseService.getSubscriptionListingDetails(mCodePurchase).currency;
            cost = mPurchaseService.getSubscriptionListingDetails(mCodePurchase).priceValue;
        }
        params.put(AppConstants.KEY_PARAMS.COST.toString(), String.valueOf(cost));
        params.put(AppConstants.KEY_PARAMS.CURRENCY.toString(), currency);
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.BUY_MONTHLY_SUCCESS.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    if (objectData.length() > 0) {
                                        JSONObject objectUser = objectData.getJSONObject(AppConstants.KEY_PARAMS.MY_PREMIUM.toString());
                                        PackageItem itemCurrent;
                                        if (objectUser.length() > 0) {
                                            //Purchase user
                                            itemCurrent = PackageItem.parser(objectUser);
                                            mTvNamePackage.setText(itemCurrent.getPrice_code());
                                            mTvTimeEnd.setText(itemCurrent.getName());
                                        }
                                        mStatus = AppConstants.STATUS_USER_PURCHASE.BOUGHT;
                                        //Change status user.
                                        user.setPremiumUser(true);
                                        setUserType(true);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dismissLoading();
                            } else {
                                dismissLoading();
                                requestAgainUpdateAgain(transactionDetails);
                            }
                        }
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                requestAgainUpdateAgain(transactionDetails);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void requestAgainUpdateAgain(final TransactionDetails transactionDetails) {
        HSSDialog.show(BuyMonthPremiumActivity.this, getString(R.string.msg_login_error_try_again), getString(R.string.btn_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestToServer(transactionDetails);
            }
        }, getString(R.string.btn_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    private void sendRequestGet() {
        showLoading();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, AppConstants.SERVER_PATH.LIST_PURCHASE.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            String msg = response.optString(AppConstants.KEY_PARAMS.MESSAGE.toString(), "");
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                try {
                                    JSONObject objectData = response.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                    //User purchase.
                                    JSONObject objectUser = objectData.getJSONObject(AppConstants.KEY_PARAMS.MY_PREMIUM.toString());
                                    setUserType(objectUser.length() == 0);
                                    PackageItem itemCurrent = new PackageItem(), itemSale = new PackageItem();
                                    if (objectUser.length() > 0) {
                                        //Purchase user
                                        itemCurrent = PackageItem.parser(objectUser);
                                        mTvNamePackage.setText(itemCurrent.getCurrent_price());
                                        mTvTimeEnd.setText(itemCurrent.getCurrent_enddate());
                                        mWipeReNewal.setImageResource(itemCurrent.getStatus() == AppConstants.STATUS_SUBCRIPTION.ENABLE_RENEW ? R.mipmap.ic_switch_purchase_enable : R.mipmap.ic_switch_purchase_disable);
                                    }
                                    // List
                                    JSONArray arrayList = objectData.getJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                                    if (arrayList.length() > 0) {
                                        JSONObject data = arrayList.getJSONObject(0);
                                        if (data != null) {
                                            itemSale = PackageItem.parser(data);
                                            mTvEndPackage.setText(itemCurrent.getNext_startdate());
                                            mTvTitle.setText(itemCurrent.getNext_price());
                                            mIdPurchase = itemSale.getId();
                                            mCodePurchase = itemSale.getPrice_code();
                                        }
                                    }
                                    /**
                                     * 1. Nếu là user free thì cho click vào button mua.
                                     * 2. Nếu là user trả phí mà gói bán khác với gói đang dùng thì cho update.
                                     * 3. Nếu gói đang dùng và gói bán khác nhau. Hiện thông báo bạn đã mua gói này rồi.
                                     */
                                    if (user.isPremiumUser()) {
                                        if (itemCurrent.getPrice_code().equalsIgnoreCase(itemSale.getPrice_code())) {
                                            mStatus = AppConstants.STATUS_USER_PURCHASE.BOUGHT;
                                        } else {
                                            mStatus = AppConstants.STATUS_USER_PURCHASE.UPDATE;
                                        }
                                    }

                                    if (mCodePurchase.length() > 0) {
                                        updateStatusPurchase(mCodePurchase);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dismissLoading();
                            } else if (status == AppConstants.REQUEST_SUCCESS) {
                                goMaintainScreen(BuyMonthPremiumActivity.this, msg);
                            } else {
                                dismissLoading();
                                requestAgain();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                requestAgain();
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    protected void requestAgain() {
        HSSDialog.show(BuyMonthPremiumActivity.this, getString(R.string.msg_get_purchase_error_try_again), getString(R.string.btn_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
                sendRequestGet();
            }
        }, getString(R.string.btn_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSDialog.dismissDialog();
            }
        }).show();
    }

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }
}
