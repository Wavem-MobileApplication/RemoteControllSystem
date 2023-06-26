package com.example.remotecontrollsystem.ui.dialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
                View focusView = getCurrentFocus();
                if (focusView != null) {
                    Rect rect = new Rect();
                    focusView.getGlobalVisibleRect(rect);
                    int x = (int) ev.getX(), y = (int) ev.getY();
                    if (!rect.contains(x, y)) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null)
                            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                        focusView.clearFocus();
                    }
                }
                return super.dispatchTouchEvent(ev);
            }
        };
    }
}
