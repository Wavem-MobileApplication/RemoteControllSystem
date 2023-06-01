package com.example.remotecontrollsystem.ui.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class MacAddressInputManager {
    private static final String MAC_ADDRESS_SEGMENT_SEPARATOR = ":";

    private final EditText editText;

    public MacAddressInputManager(EditText editText) {
        this.editText = editText;
    }

    private final TextWatcher macAddressTextWatcher = new TextWatcher() {
        private boolean isEditing;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isEditing) {
                return;
            }

            if (count > before) {
                isEditing = true;
                if (s.length() > 0 && !s.toString().endsWith(MAC_ADDRESS_SEGMENT_SEPARATOR)) {
                    int lastSegmentsLength = getLastSegmentsLength(s.toString());
                    Log.d("segments length", String.valueOf(lastSegmentsLength));

                    if (lastSegmentsLength == 2) {
                        editText.append(MAC_ADDRESS_SEGMENT_SEPARATOR);
                    }
                }
                isEditing = false;
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    private int getLastSegmentsLength(String input) {
        String[] segments = input.split(MAC_ADDRESS_SEGMENT_SEPARATOR);
        return segments[segments.length - 1].length();
    }

    public TextWatcher getMacAddressTextWatcher() {
        return macAddressTextWatcher;
    }
}
