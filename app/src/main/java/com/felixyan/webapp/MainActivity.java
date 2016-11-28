package com.felixyan.webapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.felixyan.library.WebViewWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mBtnWebViewDialog;
    private Button mBtnWebViewWebView;
    private Button mBtnRefreshableWebViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mBtnWebViewDialog = (Button) findViewById(R.id.btnWebViewDialog);
        mBtnWebViewWebView = (Button) findViewById(R.id.btnWebViewActivity);
        mBtnRefreshableWebViewActivity = (Button) findViewById(R.id.btnRefreshableWebViewActivity);

        mBtnWebViewDialog.setOnClickListener(this);
        mBtnWebViewWebView.setOnClickListener(this);
        mBtnRefreshableWebViewActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url = "http://www.baidu.com";
        switch (v.getId()) {
            case R.id.btnWebViewDialog:
                WebViewWrapper wrapper = new WebViewWrapper.Builder(this)
                        .setInitialUrl(url)
                        .setRefreshable(false)
                        .setIsLoadExternalResourceInBrowser(false)
                        .create();
                wrapper.loadUrl(url);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(wrapper.getContentView())
                        .setTitle("PAGE TITLE")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.btnWebViewActivity:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, url);
                intent.putExtra(WebViewActivity.EXTRA_IS_FULL_SCREEN, true);
                intent.putExtra(WebViewActivity.EXTRA_IS_ALL_URL_IN_WEB_VIEW, true);
                intent.putExtra(WebViewActivity.EXTRA_NAVIGATE_IN_WEB_VIEW, true);
                startActivity(intent);
                break;
            case R.id.btnRefreshableWebViewActivity:
                Intent intent2 = new Intent(this, WebViewActivity.class);
                intent2.putExtra(WebViewActivity.EXTRA_URL, url);
                intent2.putExtra(WebViewActivity.EXTRA_REFRESHABLE, true);
                intent2.putExtra(WebViewActivity.EXTRA_IS_FULL_SCREEN, true);
                intent2.putExtra(WebViewActivity.EXTRA_IS_ALL_URL_IN_WEB_VIEW, true);
                intent2.putExtra(WebViewActivity.EXTRA_NAVIGATE_IN_WEB_VIEW, true);
                startActivity(intent2);
                break;
        }
    }
}
