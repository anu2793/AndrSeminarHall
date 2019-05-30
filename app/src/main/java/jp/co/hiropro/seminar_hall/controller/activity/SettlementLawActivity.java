package jp.co.hiropro.seminar_hall.controller.activity;

import android.os.Bundle;
import android.webkit.WebView;

import jp.co.hiropro.seminar_hall.R;

public class SettlementLawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen("資金決済法に基づく表示");
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/html/settlement_law.html");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settlement_law ;
    }
}
