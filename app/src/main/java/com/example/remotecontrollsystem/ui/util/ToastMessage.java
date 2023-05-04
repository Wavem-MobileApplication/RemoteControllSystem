package com.example.remotecontrollsystem.ui.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastMessage {
    private static Toast toast;
    public static void showToast(Context context, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        Log.d("Toast", context.getClass().getSimpleName());
    }
}
