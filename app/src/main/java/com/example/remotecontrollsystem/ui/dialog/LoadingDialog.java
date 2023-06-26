package com.example.remotecontrollsystem.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.databinding.DialogLoadingBinding;
import com.example.remotecontrollsystem.ui.util.DialogUtil;

public class LoadingDialog extends Dialog {
    private DialogLoadingBinding b;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        b = DialogLoadingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        DialogUtil.settingNoNavigationBarScreen(this);
    }

    public void setText(String text) {
        b.tvLoadingMsg.setText(text);
    }
}
