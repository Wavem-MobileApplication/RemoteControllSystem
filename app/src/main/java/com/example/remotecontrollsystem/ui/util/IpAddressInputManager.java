package com.example.remotecontrollsystem.ui.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class IpAddressInputManager {
    private static final String IP_ADDRESS_SEGMENT_SEPARATOR = ".";
    private static final int MAX_IP_ADDRESS_LENGTH = 15;

    private final EditText editText;

    public IpAddressInputManager(EditText editText) {
        this.editText = editText;
    }

    private TextWatcher ipAddressTextWatcher = new TextWatcher() {
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

                if (s.length() > 0 && !s.toString().endsWith(IP_ADDRESS_SEGMENT_SEPARATOR)) {
                    if (s.length() <= MAX_IP_ADDRESS_LENGTH) {
                        int lastSegmentsLength = getLastSegmentsLength(s.toString());
                        int segmentCount = getSegmentCount(s.toString());
                        if (lastSegmentsLength >= 3 && segmentCount < 4) {
                            editText.append(IP_ADDRESS_SEGMENT_SEPARATOR);
                        }
                    }
                }

                isEditing = false;
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    private int getLastSegmentsLength(String input) {
        String[] segments = input.split("\\" + IP_ADDRESS_SEGMENT_SEPARATOR);

        return segments[segments.length - 1].length();
    }

    private int getSegmentCount(String input) {
        return input.split("\\" + IP_ADDRESS_SEGMENT_SEPARATOR).length;
    }

    public TextWatcher getIpAddressTextWatcher() {
        return ipAddressTextWatcher;
    }
}
