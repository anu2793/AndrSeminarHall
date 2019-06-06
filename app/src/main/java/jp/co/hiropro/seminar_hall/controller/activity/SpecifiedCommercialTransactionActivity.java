package jp.co.hiropro.seminar_hall.controller.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class    SpecifiedCommercialTransactionActivity extends BaseActivity {
    private boolean isShowBack = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.name_menu_business));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            isShowBack = bundle.getBoolean(AppConstants.KEY_INTENT.SHOW_BACK.toString(), false);
        btnBack.setVisibility(isShowBack ? View.VISIBLE : View.INVISIBLE);
        btnShop.setVisibility(View.VISIBLE);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/html/specified_commercial_transaction.html");
        webView.setBackgroundColor(Color.TRANSPARENT);
    }
}
