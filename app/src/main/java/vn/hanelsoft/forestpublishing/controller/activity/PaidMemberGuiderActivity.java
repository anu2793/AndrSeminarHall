package vn.hanelsoft.forestpublishing.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.PackageItem;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.view.dialog.RegisterDialog;

import static vn.hanelsoft.forestpublishing.ForestApplication.LICENSE_KEY;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class PaidMemberGuiderActivity extends BaseActivity {
    public static String KEY_SHOW_BACK = "KEY_SHOW_BACK";
    private BillingProcessor mPurchaseService;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private int mIdPurchase;
    private String mCodePurchase;
    private ProgressDialog mPrg;
    private boolean mIsShowBack = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paid_member_guider;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.txt_title_paid_member_guide));
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsShowBack = bundle.getBoolean(PaidMemberGuiderActivity.KEY_SHOW_BACK, true);
        }
        btnBack.setVisibility(mIsShowBack ? View.VISIBLE : View.GONE);
        // Progress Bar
        mPrg = new ProgressDialog(PaidMemberGuiderActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);
        initBilling();
        sendRequestGet();
    }

    @OnClick({R.id.btn_go_purchase})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_purchase:
                if (user.isSkipUser()) {
                    RegisterDialog dialog = new RegisterDialog(PaidMemberGuiderActivity.this);
                    dialog.setAction(new RegisterDialog.onDialogAction() {
                        @Override
                        public void onRegister() {
                            startActivity(new Intent(PaidMemberGuiderActivity.this, RegisterActivity.class));
                        }
                    });
                    dialog.show();
                } else {
                    if (user.isPremiumUser()) {
                        startActivity(new Intent(PaidMemberGuiderActivity.this, BuyMonthPremiumActivity.class));
                    } else {
                        buyMonthlyPurchase();
                    }
                }
                break;
        }
    }

    protected void buyMonthlyPurchase() {
        if (AppConstants.isTestMode)
            mPurchaseService.purchase(PaidMemberGuiderActivity.this, "android.test.purchased");
        else
            mPurchaseService.subscribe(PaidMemberGuiderActivity.this, String.valueOf(mCodePurchase));
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
                                    JSONArray arrayList = objectData.getJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                                    if (arrayList.length() > 0) {
                                        JSONObject data = arrayList.getJSONObject(0);
                                        if (data != null) {
                                            PackageItem itemSale = PackageItem.parser(data);
                                            mIdPurchase = itemSale.getId();
                                            mCodePurchase = itemSale.getPrice_code();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dismissLoading();
                            } else if (status == AppConstants.REQUEST_SUCCESS) {
                                goMaintainScreen(PaidMemberGuiderActivity.this, msg);
                            } else {
                                dismissLoading();
                                requestAgain();
                            }
                        }
                        System.out.println(response);
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
        HSSDialog.show(PaidMemberGuiderActivity.this, getString(R.string.msg_get_purchase_error_try_again), getString(R.string.btn_ok), new View.OnClickListener() {
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

    private void initBilling() {
        mPurchaseService = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, final TransactionDetails details) {
                if (AppConstants.isTestMode)
                    mPurchaseService.consumePurchase(productId);
                mCodePurchase = productId;
                user.setPremiumUser(true);
                HSSDialog.show(PaidMemberGuiderActivity.this, getResources().getString(R.string.txt_buy_monthly_package_success), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HSSDialog.dismissDialog();
                        sendRequestToServer(details);
                    }
                }).show();
            }

            @Override
            public void onPurchaseHistoryRestored() {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(PaidMemberGuiderActivity.this, getResources().getString(R.string.txt_cancel_purchase), Toast.LENGTH_SHORT).show();
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
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.PREMIUM_ID.toString(), String.valueOf(mIdPurchase));
        params.put(AppConstants.KEY_PARAMS.DATE_FROM.toString(), time);
        params.put(AppConstants.KEY_PARAMS.DATE_TO.toString(), time);
        String currency = "";
        Double cost = 0.0;
        if (mCodePurchase.length() > 0 && mPurchaseService.getSubscriptionListingDetails(mCodePurchase) != null) {
            currency = mPurchaseService.getSubscriptionListingDetails(mCodePurchase).currency;
            cost = mPurchaseService.getSubscriptionListingDetails(mCodePurchase).priceValue;
        }
        params.put(AppConstants.KEY_PARAMS.COST.toString(), String.valueOf(cost));
        params.put(AppConstants.KEY_PARAMS.CURRENCY.toString(), currency);
        if (transactionDetails.purchaseInfo != null) {
            if (transactionDetails.purchaseInfo.responseData != null)
                params.put(AppConstants.KEY_PARAMS.PURCHASE_INFO.toString(), String.valueOf(transactionDetails.purchaseInfo.responseData));
        }
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
//                                        PackageItem itemCurrent;
//                                        if (objectUser.length() > 0) {
//                                            //Purchase user
//                                            itemCurrent = PackageItem.parser(objectUser);
//                                        }
                                        //Change status user.
                                        user.setPremiumUser(true);
                                        startActivity(new Intent(PaidMemberGuiderActivity.this, BuyMonthPremiumActivity.class));
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
        HSSDialog.show(PaidMemberGuiderActivity.this, getString(R.string.msg_login_error_try_again), getString(R.string.btn_ok), new View.OnClickListener() {
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

    public void showLoading() {
        if (!mPrg.isShowing() && mPrg != null)
            mPrg.show();
    }

    public void dismissLoading() {
        if (mPrg.isShowing())
            mPrg.dismiss();
    }
}
