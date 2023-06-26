package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kotlin.UInt;

public class EditableTextView extends FrameLayout {
    private TextView textView;
    private EditText editText;

    public EditableTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public EditableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textView = new TextView(getContext());
        textView.setVisibility(VISIBLE);
        textView.setOnClickListener(v -> setEditable(true));

        editText = new EditText(getContext());
        editText.setVisibility(INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                setEditable(false);
            } else {
                editText.setSelection(editText.getText().length());
            }
        });

        setTextColor(Color.WHITE);
        setTextSize(20);
        setGravity(Gravity.CENTER_VERTICAL);

        addView(textView);
        addView(editText);
    }

    public void setText(String text) {
        textView.setText(text);
        editText.setText(text);
    }

    public void setEditable(boolean editable) {
        if (editable) {
            textView.setVisibility(INVISIBLE);
            editText.setVisibility(VISIBLE);
            editText.requestFocus();
        } else {
            textView.setVisibility(VISIBLE);
            editText.setVisibility(INVISIBLE);
        }
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        editText.setTextColor(color);
    }

    public void setTextSize(float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setGravity(int gravity) {
        textView.setGravity(gravity);
        editText.setGravity(gravity);
    }

    public void addTextChangeWatcher(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }

    public String getText() {
        return textView.getText().toString();
    }
}
