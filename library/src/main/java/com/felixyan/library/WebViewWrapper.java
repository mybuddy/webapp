package com.felixyan.library;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * Created by yanfei on 2016/11/02.
 */

public class WebViewWrapper {
    private View mContentView; // 根布局
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean mIsReceivedError = false; // 是否遇到错误

    private WebViewWrapper() {

    }

    public View getContentView() {
        return mContentView;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack() {
        if(mWebView != null) {
            mWebView.goBack();
        }
    }

    public boolean isReceivedError() {
        return mIsReceivedError;
    }

    private void setReceivedError(boolean isReceivedError) {
        mIsReceivedError = isReceivedError;
    }

    public void setBackgroundColor(int color) {
        if(mWebView != null) {
            mWebView.setBackgroundColor(color);
        }
    }

    public void loadUrl(String url) {
        loadUrl(url, null);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        loadUrl(url, true, true, additionalHttpHeaders);
    }

    public void loadUrl(String url, boolean addDefaultParams, boolean addDefaultHeaders, Map<String, String> additionalHttpHeaders) {
        if(mWebView != null) {
            if(addDefaultParams) {
                // 添加默认url参数
                //url = HttpRequest.buildUrl(url, HttpRequest.getDefaultParams());
            }
            if(addDefaultHeaders) {
                // 默认header
                /*Map<String, String> defaultHeaders = HttpRequest.getDefaultHeaders();
                if(additionalHttpHeaders != null) {
                    additionalHttpHeaders.putAll(defaultHeaders);
                } else {
                    additionalHttpHeaders = defaultHeaders;
                }*/
            }

            mWebView.loadUrl(url, additionalHttpHeaders);
        }
    }

    public void loadJavascript(String javascript) {
        if(mWebView != null) {
            mWebView.loadUrl(javascript);
        }
    }

    public static class Builder {
        private WebViewWrapper wrapper;

        private Activity mActivity;
        private OverrideUrlLoading mOverrideUrlLoading;

        private boolean mRefreshable = false;
        private boolean mEnableJavascript = true;
        private Object mJavascriptInterface;
        private String mJavascriptInterfaceName;
        //private boolean mRequireCookie = true;
        private List<HttpCookie> mCookieList;
        private String mSchemeAndHost;
        private boolean mIsLoadExternalResourceInBrowser = false;
        private String mDialogTitle;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        /**
         * 页面是否可以下拉刷新
         * @param refreshable 默认false
         * @return
         */
        public Builder setRefreshable(boolean refreshable) {
            mRefreshable = refreshable;
            return this;
        }

        /**
         * 是否启用Javascript
         * @param enableJavascript 默认true
         * @return
         */
        public Builder setEnableJavascript(boolean enableJavascript) {
            mEnableJavascript = enableJavascript;
            return this;
        }

        /**
         * 添加供Javascript调用的native接口
         * @param object
         * @param name
         * @return
         */
        public Builder addJavascriptInterface(Object object, String name) {
            mJavascriptInterface = object;
            mJavascriptInterfaceName = name;
            return this;
        }

        /**
         * 是否需要cookie
         * @param requireCookie 默认true
         * @return
         */
        /*public Builder setRequireCookie(boolean requireCookie) {
            mRequireCookie = requireCookie;
            return this;
        }*/

        /**
         * 设置cookie
         * @param cookieList
         * @return
         */
        public Builder setCookieList(List<HttpCookie> cookieList) {
            mCookieList = cookieList;
            return this;
        }

        /**
         * 设置url的scheme+host，用于判断加载的资源是否为外部资源。
         * 仅当isLoadExternalResourceInBrowser设置为true时需要设置
         * @param initialUrl 如“http://www.google.com”
         * @return
         */
        public Builder setInitialUrl(String initialUrl) {
            if(initialUrl != null) {
                Uri uri = Uri.parse(initialUrl);
                mSchemeAndHost = uri.getScheme() + "://" + uri.getHost();
            }

            return this;
        }

        /**
         * 是否使用浏览器加载外部资源
         * @param isLoadExternalResourceInBrowser 默认false
         * @return
         */
        public Builder setIsLoadExternalResourceInBrowser(boolean isLoadExternalResourceInBrowser) {
            mIsLoadExternalResourceInBrowser = isLoadExternalResourceInBrowser;
            return this;
        }

        /**
         * 设置WebView内部dialog标题
         * @param dialogTitle
         * @return
         */
        public Builder setDialogTitle(String dialogTitle) {
            mDialogTitle = dialogTitle;
            return this;
        }

        public Builder setOverrideUrlLoading(OverrideUrlLoading overrideUrlLoading) {
            mOverrideUrlLoading = overrideUrlLoading;
            return this;
        }

        public WebViewWrapper create() {
            wrapper = new WebViewWrapper();
            // layout
            if(mRefreshable) {
                wrapper.mContentView = View.inflate(mActivity, R.layout.webview_layout_refreshable, null);
                wrapper.mRefreshLayout = (SwipeRefreshLayout) wrapper.mContentView.findViewById(R.id.refreshLayout);
                wrapper.mRefreshLayout.setColorSchemeColors(
                        Color.RED, Color.GREEN, Color.BLUE, Color.rgb(255, 153, 0));
            } else {
                wrapper.mContentView = View.inflate(mActivity, R.layout.webview_layout, null);
                wrapper.mProgressBar = (ProgressBar) wrapper.mContentView.findViewById(R.id.progressBar);
            }
            wrapper.mWebView = (WebView) wrapper.mContentView.findViewById(R.id.webView);

            // js enable
            WebSettings settings = wrapper.mWebView.getSettings();
            settings.setJavaScriptEnabled(mEnableJavascript);

            // js interface
            if(mJavascriptInterface != null && mJavascriptInterfaceName != null) {
                wrapper.mWebView.addJavascriptInterface(mJavascriptInterface, mJavascriptInterfaceName);
            }

            // attach cookie
            //if(mRequireCookie) {
            if(mCookieList != null) {
                WebViewCookieUtil.syncCookie(mActivity, mCookieList);
            }

            // webView client
            wrapper.mWebView.setWebViewClient(new MyWebViewClient(mActivity, mSchemeAndHost,
                    mIsLoadExternalResourceInBrowser) {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(mOverrideUrlLoading != null) {
                        return mOverrideUrlLoading.shouldOverrideUrlLoading(view, url)
                                || super.shouldOverrideUrlLoading(view, url);
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description,
                                            String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    if(mRefreshable) {
                        wrapper.mRefreshLayout.setRefreshing(false);
                    } else {
                        wrapper.mProgressBar.setVisibility(View.GONE);
                    }
                    wrapper.setReceivedError(true);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if(!mRefreshable) {
                        wrapper.mProgressBar.setVisibility(View.GONE);
                    }
                }
            });

            // webChrome client
            wrapper.mWebView.setWebChromeClient(new MyWebChromeClient(mActivity, mDialogTitle) {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //super.onProgressChanged(view, newProgress);
                    if(mRefreshable) {
                        if(newProgress == 100) {
                            wrapper.mRefreshLayout.setRefreshing(false);
                        } else if(!wrapper.mRefreshLayout.isRefreshing()) {
                            wrapper.mRefreshLayout.setRefreshing(true);
                        }
                    } else {
                        wrapper.mProgressBar.setProgress(newProgress);
                    }
                }
            });

            return wrapper;
        }
    }

    public interface OverrideUrlLoading {
        boolean shouldOverrideUrlLoading(WebView webView, String url);
    }
}
