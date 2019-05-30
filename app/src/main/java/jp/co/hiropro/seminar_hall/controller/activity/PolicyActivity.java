package jp.co.hiropro.seminar_hall.controller.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import jp.co.hiropro.seminar_hall.R;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class PolicyActivity extends BaseActivity {
    WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_policy;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.txt_privacy));
        btnBack.setVisibility(View.GONE);
        webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/html/privacy_policy.html");
    }
}
