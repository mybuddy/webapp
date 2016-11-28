package com.felixyan.library;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.HttpCookie;
import java.util.List;

/**
 * Created by yanfei on 2016/11/01.
 */

public class WebViewCookieUtil {
    public static void syncCookie(Context context, List<HttpCookie> cookieList) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for(HttpCookie cookie : cookieList){
            cookieManager.setCookie(cookie.getDomain(), cookie.getName() + "=" + cookie.getValue());
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }

    public static void removeCookie(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
            cookieManager.removeAllCookies(null);
        } else {
            // 调用CookieManager.removeAllCookie()清除cookie时，可能会导致app crash
            // 需要在清除cookie的代码前实例化CookieSyncManager
            // 参考：http://blog.csdn.net/hengyunabc/article/details/36691887
            // http://stackoverflow.com/questions/32284642/how-to-handle-an-uncatched-exception
            @SuppressWarnings("unused")
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieManager.removeAllCookie();
        }
    }
}
