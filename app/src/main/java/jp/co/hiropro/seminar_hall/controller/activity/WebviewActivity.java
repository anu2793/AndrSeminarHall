package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.dialog.DialogOpenBrowser;

public class WebviewActivity extends FragmentActivity {
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.prgLoading)
    ProgressBar mPrgLoading;
    @BindView(R.id.imv_back)
    ImageView mImvBack;
    @BindView(R.id.imv_next)
    ImageView mImvNext;
    @BindView(R.id.tv_title)
    TextViewApp tvTitle;
    String urlCampain;
    private String mUrlPageLoad = "";
    private boolean isRedirected = true;
    private String urlHyperLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            urlCampain = getIntent().getStringExtra(AppConstants.KEY_SEND.KEY_URL_CAMPAIN);
            urlHyperLink = getIntent().getStringExtra(AppConstants.KEY_SEND.KEY_URL_HYPERLINK);
            if (urlHyperLink != null && !urlHyperLink.isEmpty()) {
                tvTitle.setText(urlHyperLink);
                initWebview(urlHyperLink);
            } else {
                if (Patterns.WEB_URL.matcher(urlCampain).matches()) {
                    if (!urlCampain.startsWith("http://") && !urlCampain.startsWith("https://"))
                        urlCampain = "http://" + urlCampain;
                    initWebview(urlCampain);
                } else {
                    if (!urlCampain.startsWith("http://") && !urlCampain.startsWith("https://"))
                        urlCampain = "http://" + urlCampain;
                    initWebview(urlCampain);
                }
            }


        }
    }

    private void initWebview(String url) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mUrlPageLoad = url;
                mPrgLoading.setVisibility(View.VISIBLE);
                if (url.contains("#Intent;")) {
                    view.stopLoading();
                    webView.goBack();
                }
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("url", "url :" + url);
                mPrgLoading.setVisibility(View.GONE);
                mImvBack.setEnabled(webView.canGoBack());
                mImvBack.setImageResource(webView.canGoBack() ? R.mipmap.ic_back_web_enable : R.mipmap.ic_back_disable);
                mImvNext.setEnabled(webView.canGoForward());
                mImvNext.setImageResource(webView.canGoForward() ? R.mipmap.ic_next_normal : R.mipmap.ic_next_disable);
                if (url.contains("#Intent;")) {
                    if (webView.canGoBack())
                        webView.goBack();
                    if (isRedirected) {
                        isRedirected = false;
                        if (url.length() > 0) {
                            int index = url.indexOf("paign?id=");
                            int indexEnd = url.indexOf("#Intent;");
                            String idCampain = url.substring(index + 9, indexEnd).replace("/", "");
                            if (idCampain.length() > 0) {
                                VideoDetail detail = new VideoDetail();
                                detail.setId(Integer.valueOf(idCampain));
                                startActivity(new Intent(WebviewActivity.this, ContentDetailActivity.class).
                                        putExtra(KeyParser.KEY.DATA.toString(), detail));
                            }
                        }
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webView.goBack();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.goBack();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                webView.goBack();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                webView.goBack();
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(url);
    }

    @OnClick({R.id.imv_close, R.id.imv_back, R.id.imv_open, R.id.imv_next})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_close:
                finish();
                break;
            case R.id.imv_open:
                DialogOpenBrowser dialogOpenBrowser = new DialogOpenBrowser(WebviewActivity.this);
                dialogOpenBrowser.getWindow().getAttributes().gravity = Gravity.BOTTOM;
                dialogOpenBrowser.setActionDialog(new DialogOpenBrowser.onActionDiloag() {
                    @Override
                    public void onOpen() {
                        Intent intentOpen = null;
                        if (URLUtil.isValidUrl(mUrlPageLoad)) {
                            intentOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlPageLoad));
                        } else {
                            intentOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(urlCampain));
                        }
                        startActivity(intentOpen);
                    }

                    @Override
                    public void onCopy() {
                        AppUtils.setClipboard(WebviewActivity.this, urlCampain);
                        Toast.makeText(WebviewActivity.this, getString(R.string.msg_copy_success), Toast.LENGTH_SHORT).show();
                    }
                });
                dialogOpenBrowser.show();
                break;
            case R.id.imv_next:
                webView.goForward();
                break;
            case R.id.imv_back:
                webView.goBack();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRedirected = true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }
}
