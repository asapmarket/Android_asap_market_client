package com.github.lzyzsd.jsbridge;

import android.webkit.WebView;

/**
 * 文件描述
 * Date: 15/10/19
 *
 * @author xusheng
 */
public interface CustomerWebViewClient {

    boolean shouldOverrideUrlLoading(WebView view, String url);

    void onPageFinished(WebView view, String url);
}
