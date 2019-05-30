package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 1/31/2018.
 */

public class QrCodeScannerActivity extends FragmentActivity implements ZBarScannerView.ResultHandler {
    public static int GET_CONTACT_QR_CDODE = 111;
    @BindView(R.id.scanner)
    ZBarScannerView mScanner;
    @BindView(R.id.rl_loading)
    RelativeLayout mRlLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        ButterKnife.bind(this);
        mScanner.setAspectTolerance(0.5f);
        mScanner.setResultHandler(this); // Register ourselves as a handler for scan results.
    }

    @Override
    public void onResume() {
        mScanner.resumeCameraPreview(QrCodeScannerActivity.this);// Start camera on resume
        mScanner.startCamera();
        super.onResume();

    }

    @Override
    public void onPause() {
        // Stop camera on pause
        mScanner.stopCamera();
        super.onPause();

    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("Result", "Result : " + rawResult.getContents() + "--" + rawResult.getBarcodeFormat().getName());
        if (rawResult.getContents().length() > 0) {
            Intent intent = new Intent(QrCodeScannerActivity.this, SingleReceiveCardActivity.class);
            intent.putExtra(AppConstants.KEY_SEND.KEY_DATA, rawResult.getContents());
            startActivityForResult(intent, GET_CONTACT_QR_CDODE);
        } else {
            mScanner.resumeCameraPreview(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CONTACT_QR_CDODE) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    @OnClick({R.id.imv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_back:
                finish();
                break;
        }
    }

    public void showLoading() {
        mRlLoading.setVisibility(View.VISIBLE);
    }

    public void dismissLoading() {
        mRlLoading.setVisibility(View.GONE);
    }

}
