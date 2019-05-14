package vn.hanelsoft.forestpublishing.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.hanelsoft.dialog.LoadingDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.utils.NetworkUtils;

/**
 * Created by dinhdv on 2/10/2018.
 */

public class PurchaseActivity extends BaseSkipShowDialogActivity {
    private VideoDetail mItemVideo;
    private User mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_purchase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.title_screen_purchase));
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mItemVideo = bundle.getParcelable(AppConstants.KEY_SEND.KEY_VIDEO);
        }
        mUser = User.getInstance().getCurrentUser();
    }

    @OnClick({R.id.btn_login, R.id.btn_register, R.id.btn_buy})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(PurchaseActivity.this, LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(PurchaseActivity.this, RegisterActivity.class));
                break;
            case R.id.btn_buy:
                setResult(ContentDetailActivity.UNREGISTER_BUY_VIDEO);
                finish();
//                WaringPurchaseDialog dialog = new WaringPurchaseDialog(PurchaseActivity.this);
//                dialog.setListener(new WaringPurchaseDialog.onDialogAction() {
//                    @Override
//                    public void onOk() {
//                        setResult(ContentDetailActivity.UNREGISTER_BUY_VIDEO);
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//                dialog.show();

//                showLoading();
//                HashMap<String, String> params = new HashMap<>();
//                RequestDataUtils.requestData(Request.Method.GET, PurchaseActivity.this,
//                        AppConstants.SERVER_PATH.GET_POINT.toString(), params, new RequestDataUtils.onResult() {
//                            @Override
//                            public void onSuccess(JSONObject object, String msg) {
//                                //Update point.
//                                try {
//                                    JSONObject point = object.getJSONObject(AppConstants.KEY_PARAMS.MY_POINT.toString());
//                                    int pointValue = point.optInt(AppConstants.KEY_PARAMS.POINT.toString(), 0);
//                                    user.setPoint(pointValue);
//                                    User.getInstance().getCurrentUser().setPoint(pointValue);
//                                    if (mUser.getPoint() >= mItemVideo.getPrice()) {
//                                        DialogPurchaseVideo.showDialog(PurchaseActivity.this, getString(R.string.txt_content_point, Utils.formatPrice(mItemVideo.getPrice())), Utils.formatPrice(mUser.getPoint()), new OnSingleClickListener() {
//                                            @Override
//                                            public void onSingleClick(View v) {
//                                                DialogPurchaseVideo.dismissDialog();
//                                                purchaseVideo(mItemVideo.getId());
//                                            }
//                                        }).show();
//                                    } else {
//                                        DialogPurchaseVideo.showDialog(PurchaseActivity.this, getString(R.string.title_not_enough_point), Utils.formatPrice(mUser.getPoint()), new OnSingleClickListener() {
//                                            @Override
//                                            public void onSingleClick(View v) {
//                                                DialogPurchaseVideo.dismissDialog();
//                                                startActivity(new Intent(PurchaseActivity.this, PointManagerActivity.class).putExtra(AppConstants.KEY_INTENT.SHOW_BACK.toString(), true));
//                                            }
//                                        }).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                dismissLoading();
//                            }
//
//                            @Override
//                            public void onFail(int error) {
//                                dismissLoading();
//                                Toast.makeText(PurchaseActivity.this, getString(R.string.msg_cannot_buy_video), Toast.LENGTH_SHORT).show();
//                            }
//                        });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = User.getInstance().getCurrentUser();
        if (!User.getInstance().getCurrentUser().isSkipUser())
            finish();
    }

    private void purchaseVideo(int videoId) {
        LoadingDialog.getDialog(this).show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("videoid", videoId);
        JsonObjectRequest request = new JsonObjectRequest(AppConstants.SERVER_PATH.PURCHASE_VIDEO.toString(), new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LoadingDialog.getDialog(activity).dismiss();
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == 200) {
                                Toast.makeText(PurchaseActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                setResult(ContentDetailActivity.UNREGISTER_BUY_VIDEO);
                                finish();
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
}
