package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;

/**
 * Created by dinhdv on 2/3/2018.
 */

public class QrCodePreviewActivity extends FragmentActivity {
    @BindView(R.id.imv_bar_code)
    ImageView mImvQrCode;
    private ProgressDialog mPrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_preview);
        ButterKnife.bind(this);
        // Progress Bar
        mPrg = new ProgressDialog(QrCodePreviewActivity.this);
        mPrg.setMessage(getString(R.string.txt_loading));
        mPrg.setCanceledOnTouchOutside(false);
        mPrg.setCancelable(false);
        mPrg.setMax(100);

        getQrCode();
    }

    private void getQrCode() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, QrCodePreviewActivity.this, AppConstants.SERVER_PATH.GET_QR_CODE.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                String base64 = object.optString(AppConstants.KEY_PARAMS.QR_CODE.toString());
                if (!TextUtils.isEmpty(base64)) {
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    mImvQrCode.setImageBitmap(decodedByte);
                }
                dismissLoading();
            }

            @Override
            public void onFail(int error) {
                dismissLoading();
            }

        });
    }

    @OnClick({R.id.btn_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
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
