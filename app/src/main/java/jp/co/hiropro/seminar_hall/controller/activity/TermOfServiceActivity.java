package jp.co.hiropro.seminar_hall.controller.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.AppConstants;

/**
 * Created by dinhdv on 7/24/2017.
 */

public class TermOfServiceActivity extends BaseActivity {
    private boolean isShowMenu = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jack_terms;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTitleScreen(getString(R.string.name_menu_service));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isShowMenu = bundle.getBoolean(AppConstants.KEY_SEND.KEY_DATA, false);
        }
        if (isShowMenu) {
            btnBack.setVisibility(View.GONE);
            btnMenu.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.INVISIBLE);
        }
        btnShop.setVisibility(View.INVISIBLE);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/html/term_of_service.html");
    }
}
