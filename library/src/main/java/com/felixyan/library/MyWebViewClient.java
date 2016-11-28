package com.felixyan.library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.felixyan.library.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanfei on 2016/11/01.
 */

public class MyWebViewClient extends WebViewClient {
    private static final Map<String, LocalResource> sLocalResourceMap;

    static {
        sLocalResourceMap = new HashMap<>();
        // 本地资源
        sLocalResourceMap.put("zepto.js",
                new LocalResource("text/javascript", "UTF-8", "zepto-1.0rc1/zepto.js"));
    }

    private Activity mActivity;
    private String mSchemeAndHost;
    private boolean mIsLoadExternalResourceInBrowser;

    public MyWebViewClient(Activity activity, String schemeAndHost, boolean isLoadExternalResourceInBrowser) {
        mActivity = activity;
        mSchemeAndHost = schemeAndHost;
        mIsLoadExternalResourceInBrowser = isLoadExternalResourceInBrowser;
    }

    private String extractSchemeAndHost(String url) {
        if(url != null) {
            Uri initUri = Uri.parse(url);
            return initUri.getScheme() + "://" + initUri.getHost();
        }
        return null;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        view.loadData(StringUtil.getString(mActivity, R.string.web_view_error_message), "text/html; charset=UTF-8", null);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!mIsLoadExternalResourceInBrowser) {
            return false;
        }

        String schemeAndHost = extractSchemeAndHost(url);
        // 自己的url, 则在WebView内访问
        if (schemeAndHost.equals(mSchemeAndHost)) {
            return false;
        } else {
            if(mActivity != null) {
                // 其它url, 则调用外部浏览器
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(intent);
            }
            return true;
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        // 拦截广告
        String schemeAndHost = extractSchemeAndHost(url);
        if(!schemeAndHost.equals(mSchemeAndHost) && AdFilter.isAdUrl(mActivity, url.toLowerCase())) {
            return new WebResourceResponse(null, null, null); // 注意，不能返回null，返回null表示不拦截，正常从网络请求资源
        }

        int lastIndex = url.lastIndexOf('/');
        String assetName = url.substring(lastIndex + 1, url.length());

        if(sLocalResourceMap.containsKey(assetName)) {
            return getLocalResourceResponse(sLocalResourceMap.get(assetName));
        }
        return null;
    }

    /**
     * 获取本地资源
     * @param resource
     * @return
     */
    private WebResourceResponse getLocalResourceResponse(LocalResource resource) {
        if(resource != null) {
            return getLocalResourceResponse(resource.getMime(), resource.getEncoding(), resource.getAssetPath());
        }
        return null;
    }

    /**
     * 获取本地资源
     * @param mime
     * @param encoding
     * @param assetPath
     * @return
     */
    private WebResourceResponse getLocalResourceResponse(String mime, String encoding, String assetPath) {
        WebResourceResponse response = null;
        try {
            InputStream assetStream =
                    mActivity.getAssets().open("webview/" + assetPath);
            response = new WebResourceResponse(mime, encoding, assetStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    /**
     * 本地资源
     */
    private static class LocalResource {
        private String mime;
        private String encoding;
        private String assetPath;

        public LocalResource() {

        }

        public LocalResource(String mime, String encoding, String assetPath) {
            this.mime = mime;
            this.encoding = encoding;
            this.assetPath = assetPath;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getAssetPath() {
            return assetPath;
        }

        public void setAssetPath(String assetPath) {
            this.assetPath = assetPath;
        }
    }
}
