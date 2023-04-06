package com.example.remotecontrollsystem.ui.util;

import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;

public class DialogUtil {

    public static void settingNoNavigationBarScreen(Dialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        int uiOption = decorView.getSystemUiVisibility();

        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        decorView.setSystemUiVisibility(uiOption);
    }
}
