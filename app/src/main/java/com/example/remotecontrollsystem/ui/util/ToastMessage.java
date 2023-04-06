package com.example.remotecontrollsystem.ui.util;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {
    private static Toast toast;
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void setContext(Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }
}
