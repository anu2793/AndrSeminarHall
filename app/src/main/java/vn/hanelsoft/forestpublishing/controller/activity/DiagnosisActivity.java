package vn.hanelsoft.forestpublishing.controller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;

/**
 * Created by dinhdv on 8/2/2017.
 */

public class DiagnosisActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.prgLoading)
    ProgressBar mPrgLoading;
    String linkUrl = "http://forest.ito.vn/diagnosis?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_diagnosis_main));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            linkUrl = bundle.getString(AppConstants.KEY_INTENT.URL_DIAGNOSIS.toString());
        }
        btnBack.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPrgLoading.setVisibility(View.VISIBLE);
                if (url.contains("?id=")) {
                    view.stopLoading();
                }
                if (url.contains("diagnosisresult?")) {
                    setupTitleScreen(getString(R.string.title_diagnosis_result));
                } else {
                    setupTitleScreen(getString(R.string.title_diagnosis_main));
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("url", "values :" + url);
                if (url.contains("diagnosisresult?")) {
                    setupTitleScreen(getString(R.string.title_diagnosis_result));
                    AppConstants.setUrlResultQuestion(url);
                } else {
                    setupTitleScreen(getString(R.string.title_diagnosis_main));
                }
                if (url.contains("?id=")) {
                    String id = url.substring(url.indexOf("=") + 1, url.length());
                    VideoDetail detail = new VideoDetail();
                    detail.setId(Integer.valueOf(id));
                    startActivity(new Intent(DiagnosisActivity.this, ContentDetailActivity.class).
                            putExtra(KeyParser.KEY.DATA.toString(), detail));
                }
                mPrgLoading.setVisibility(View.GONE);
            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl(linkUrl);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_diagnosis;
    }
}
