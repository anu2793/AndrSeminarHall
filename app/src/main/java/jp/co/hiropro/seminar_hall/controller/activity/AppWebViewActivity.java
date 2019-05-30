package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.JackImageObject;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.dialog.DialogOpenBrowser;

/**
 * Created by dinhdv on 8/2/2017.
 */

public class AppWebViewActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.prgLoading)
    ProgressBar mPrgLoading;
    @BindView(R.id.tv_title)
    TextViewApp mTvTitle;
    JackImageObject object = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            object = bundle.getParcelable(AppConstants.KEY_SEND.KEY_DATA);
        }
        mTvTitle.setText(object.getContent() != null ? (object.getContent().length() > 0 ? object.getContent() : object.getLink()) : object.getLink());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPrgLoading.setVisibility(View.VISIBLE);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mPrgLoading.setVisibility(View.GONE);
            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl(object.getLink());
    }

    @OnClick({R.id.imv_close, R.id.imv_back, R.id.imv_open, R.id.imv_next})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_close:
                finish();
                break;
            case R.id.imv_back:
                finish();
                break;
            case R.id.imv_open:
                DialogOpenBrowser dialogOpenBrowser = new DialogOpenBrowser(AppWebViewActivity.this);
                dialogOpenBrowser.getWindow().getAttributes().gravity = Gravity.BOTTOM;
                dialogOpenBrowser.setActionDialog(new DialogOpenBrowser.onActionDiloag() {
                    @Override
                    public void onOpen() {
                        Intent intentOpen = null;
                        if (URLUtil.isValidUrl(object.getLink())) {
                            intentOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getLink()));
                        } else {
                            intentOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getLink()));
                        }
                        startActivity(intentOpen);
                    }

                    @Override
                    public void onCopy() {
                        AppUtils.setClipboard(AppWebViewActivity.this, object.getLink());
                        Toast.makeText(AppWebViewActivity.this, getString(R.string.msg_copy_success), Toast.LENGTH_SHORT).show();
                    }
                });
                dialogOpenBrowser.show();
                break;
        }
    }
}
