package com.vipulasri.artisto.utils;

import android.content.Context;
import android.widget.Toast;


public class ViewUtils {

    public static void showToast(String message, Context context) {
        showMessageInToast(message, context);
    }

    public static void showToast(int message, Context context) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static void showMessageInToast(String message, Context ctx) {
        if (ctx != null)
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
