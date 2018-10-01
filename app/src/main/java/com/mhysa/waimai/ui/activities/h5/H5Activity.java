package com.mhysa.waimai.ui.activities.h5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CustomerWebViewClient;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.NetworkUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.ui.activities.order.OrderDetailActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/8/21
 *
 * @author xusheng
 */

public class H5Activity extends BaseActivity {

    private static final String INTENT_TITLE = "intent_title";

    private static final String INTENT_URL = "intent_url";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.webview)
    BridgeWebView mWebView;

    private String mTitle;

    private String mUrl;

    private boolean mHasJumped = false;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_h5;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        StringUtils.getInstance().setText(mTitle, mTitleTv);
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mUrl = getIntent().getStringExtra(INTENT_URL);
            mTitle = getIntent().getStringExtra(INTENT_TITLE);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setDefaultHandler(new DefaultHandler());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(getFilesDir().getPath());

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        if (NetworkUtils.isNetworkAvailable(this)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);

        String cacheDirPath = getFilesDir().getAbsolutePath()
                + "/webcache";
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    return;
                }

                // TODO 下载
            }
        });

        mWebView.setCustomerWebViewClient(new CustomerWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!StringUtils.getInstance().isNullOrEmpty(url)
                        && url.contains("pay_ret://") && !mHasJumped) {
                    // 充值结果返回
                    mHasJumped = true;
                    int index = url.indexOf("pay_ret://");
                    String target = url.substring(index);

//                    支付失败：http://123.56.234.98/app/order/pay_ret://paypal?state=fail&orderId=9bb82fcb8a4d49a3b9cf60ca02189bd4
//
//                    支付成功：
//                    http://123.56.234.98/app/order/pay_ret://paypal?state=success&orderId=9bb82fcb8a4d49a3b9cf60ca02189bd4
//
//                    支付取消：
//                    http://123.56.234.98/app/order/pay_ret://paypal?state=cancel&orderId=9bb82fcb8a4d49a3b9cf60ca02189bd4
                    try {
                        Uri uri = Uri.parse(target);
                        String host = uri.getHost();
                        if (host.equals("paypal")) {
                            String state = uri.getQueryParameter("state");
                            String orderId = uri.getQueryParameter("orderId");
                            if (!StringUtils.getInstance().isNullOrEmpty(state)) {
                                if (state.equals("success")) {
                                    PromptUtils.getInstance().showShortPromptToast(H5Activity.this, R.string.pay_success);
                                    EventBus.getDefault().post(new ApplicationEvent(EventID.PAY_SUCCESS));
                                    OrderDetailActivity.invoke(H5Activity.this, orderId);
                                } else if (state.equals("fail")) {
                                    PromptUtils.getInstance().showShortPromptToast(H5Activity.this, R.string.pay_fail);
                                } else if (state.equals("cancel")) {
                                    PromptUtils.getInstance().showShortPromptToast(H5Activity.this, R.string.pay_cancel);
                                }

                                H5Activity.this.finish();
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                closeNetDialog();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.i("ApplyFormListActivity", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        if (!StringUtils.getInstance().isNullOrEmpty(mUrl)) {
            showNetDialog("");
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (null != mWebView && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            close();
        }
    }

    private void close() {
        H5Activity.this.finish();
    }

    @OnClick({R.id.layout_back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                H5Activity.this.finish();
                break;
            }

            default: {
                break;
            }
        }
    }

    public static void invoke(Context context, String title, String url) {
        if (null == context ||
                StringUtils.getInstance().isNullOrEmpty(url)) {
            return;
        }

        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra(INTENT_TITLE, title);
        intent.putExtra(INTENT_URL, url);
        context.startActivity(intent);
    }
}
