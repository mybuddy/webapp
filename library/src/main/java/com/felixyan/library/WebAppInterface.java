package com.felixyan.library;

import android.app.Activity;
import android.webkit.JavascriptInterface;

/**
 * Created by yanfei on 15/11/5.
 */
public class WebAppInterface {
    private Activity activity;

    public WebAppInterface(Activity a) {
        activity = a;
    }

    /**
     * 返回上一个Activity
     */
    @JavascriptInterface
    public void backToLastActivity() {
        if(activity != null) {
            activity.finish();
        }
    }
}
