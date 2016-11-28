package com.felixyan.webapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.felixyan.library.WebAppInterface;
import com.felixyan.library.WebViewWrapper;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private static final String CLASS_TAG = WebViewActivity.class.getSimpleName();
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_REFRESHABLE = "refreshable";
    public static final String EXTRA_IS_FULL_SCREEN = "is_full_screen";
    public static final String EXTRA_IS_NEED_LOGIN = "is_need_login";
    public static final String EXTRA_IS_ALL_URL_IN_WEB_VIEW = "is_all_url_in_web_view";
    public static final String EXTRA_NAVIGATE_IN_WEB_VIEW = "navigate_in_web_view";

    private WebViewWrapper wrapper;

    private String title;
    private String url;
    private boolean isRefreshable;
    private boolean isFullScreen;
    private boolean isNeedLogin;
    private boolean isAllUrlInWebView = true;
    private boolean navigateInWebView = true;

    private WebAppInterface webAppInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initData();
        initWebView();
    }

    private void initData() {
        final String TAG = CLASS_TAG + "-initData";

        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_TITLE);
        url = intent.getStringExtra(EXTRA_URL);
        isRefreshable = intent.getBooleanExtra(EXTRA_REFRESHABLE, false);
        isFullScreen = intent.getBooleanExtra(EXTRA_IS_FULL_SCREEN, false);
        isNeedLogin = intent.getBooleanExtra(EXTRA_IS_NEED_LOGIN, false);
        isAllUrlInWebView = intent.getBooleanExtra(EXTRA_IS_ALL_URL_IN_WEB_VIEW, true);
        navigateInWebView = intent.getBooleanExtra(EXTRA_NAVIGATE_IN_WEB_VIEW, true);

        webAppInterface = new WebAppInterface(this);

        // 非全屏，显示Toolbar
        if (!isFullScreen) {
            Toolbar tbToolbar = setupToolbar();
            if (tbToolbar != null) {
                tbToolbar.setVisibility(View.VISIBLE);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(title);
                }
            }
        }
    }

    /**
     * 设置默认Toolbar。展示title、返回图标
     * 要求布局文件中有id为tbToolBar的ToolBar控件
     *
     * @return
     */
    private Toolbar setupToolbar() {
        Toolbar tbToolbar = (Toolbar) findViewById(R.id.tbToolbar);
        if(tbToolbar != null) {
            setSupportActionBar(tbToolbar);
            setupActionBar();
        }
        return tbToolbar;
    }

    /**
     * 设置默认ActionBar。展示title、返回图标
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initWebView() {
        if (DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        wrapper = new WebViewWrapper.Builder(this)
                .addJavascriptInterface(webAppInterface, "Android")
                //.setRequireCookie(isNeedLogin)
                .setCookieList(isNeedLogin ? getCookieList() : null)
                .setInitialUrl(url)
                .setRefreshable(isRefreshable)
                .setIsLoadExternalResourceInBrowser(!isAllUrlInWebView)
                .setDialogTitle(title)
                .create();

        // 获取并设置WebView
        View contentView = wrapper.getContentView();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(layoutParams);

        // 将WebView添加到当前布局
        LinearLayout rootView = (LinearLayout) findViewById(R.id.llRoot);
        rootView.addView(contentView);

        // 若支持下拉刷新，则配置下拉刷新执行的操作
        if(isRefreshable) {
            wrapper.getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(!wrapper.getRefreshLayout().isRefreshing()) {
                        wrapper.getRefreshLayout().setRefreshing(true);
                    }
                    wrapper.loadUrl(url);
                }
            });
        }

        // 加载页面
        wrapper.loadUrl(url);
    }

    private List<HttpCookie> getCookieList() {
        // todo 示例，获取APP中的cookie
        return new ArrayList<>();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // back键返回时, 在WebView内导航。且针对加载失败的情况特殊处理
        if (navigateInWebView && (keyCode == KeyEvent.KEYCODE_BACK) && wrapper.canGoBack() && !wrapper.isReceivedError()) {
            wrapper.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}