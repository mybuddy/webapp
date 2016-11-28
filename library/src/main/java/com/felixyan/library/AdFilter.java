package com.felixyan.library;

import android.content.Context;

/**
 * Created by yanfei on 2016/11/06.
 */

public class AdFilter {
    private static String[] sAdUrlArray;

    public static boolean isAdUrl(Context context, String url) {
        if(context == null || url == null) {
            return false;
        }

        if(sAdUrlArray == null) {
            sAdUrlArray = context.getResources().getStringArray(R.array.ad_block_url);
        }
        for(String adUrl : sAdUrlArray) {
            if(url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}
