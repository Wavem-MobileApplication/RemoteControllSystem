package com.example.remotecontrollsystem.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.databinding.DialogDefaultBinding;

public class DefaultDialog extends Dialog {
    private DialogDefaultBinding binding;
    private OnPositiveButtonClickListener positiveButtonClickListener;
    private OnNegativeButtonClickListener negativeButtonClickListener;
    private String text;

    public DefaultDialog(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DialogDefaultBinding.inflate(inflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        adjustDialogSize();

        binding.btnPositive.setOnClickListener(view -> {
            if (positiveButtonClickListener != null) {
                positiveButtonClickListener.onClick();
            }
        });

        binding.btnNegative.setOnClickListener(view -> {
            if (negativeButtonClickListener != null) {
                negativeButtonClickListener.onClick();
            }
            dismiss();
        });

        if (text != null) {
            binding.tvDialogText.setText(text);
        }
    }

    private void adjustDialogSize() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        // Adjust dialog size
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (displaySize.x * 0.5);
        params.height = (int) (displaySize.y * 0.5);
        getWindow().setAttributes(params);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener positiveButtonClickListener) {
        this.positiveButtonClickListener = positiveButtonClickListener;
    }

    public void setOnNegativeButtonClickListener(OnNegativeButtonClickListener negativeButtonClickListener) {
        this.negativeButtonClickListener = negativeButtonClickListener;
    }

    public interface OnPositiveButtonClickListener {
        void onClick();
    }

    public interface OnNegativeButtonClickListener {
        void onClick();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        binding = null;
    }
}
