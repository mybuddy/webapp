package com.felixyan.library.util;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by yanfei on 2016/11/23.
 */

public class StringUtil {
    public static String getString(Context context, @StringRes int resId) {
        return context.getString(resId);
    }

    public static String getString(String stringFormat, Object... values) {
        return String.format(stringFormat, values);
    }

    public static String getString(@StringRes int stringFormatResId, Object... values) {
        return getString(getString(stringFormatResId), values);
    }
}
