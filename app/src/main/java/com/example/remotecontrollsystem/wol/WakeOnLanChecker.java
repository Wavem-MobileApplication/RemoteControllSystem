package com.example.remotecontrollsystem.wol;

import android.content.Context;

import com.example.remotecontrollsystem.ui.util.ToastMessage;

import java.io.IOException;
import java.net.InetAddress;

public class WakeOnLanChecker {
    private final Context mContext;

    private String ipAddress;
    private Thread checkPcAwakeThread;

    public WakeOnLanChecker(Context context) {
        this.mContext = context;
        checkPcAwakeThread = createThread(context);
    }

    public void startCheckPcAwake() {
        Thread.State state = checkPcAwakeThread.getState();
        switch (state) {
            case TERMINATED:
                checkPcAwakeThread = createThread(mContext);
                checkPcAwakeThread.start();
                break;
            case NEW:
                checkPcAwakeThread.start();
                break;
        }
    }

    private boolean checkPcAwake() throws IOException {
        InetAddress address = InetAddress.getByName(ipAddress);
        return address.isReachable(5000); // 응답 대기 시간 설정 (5초
    }

    private Thread createThread(Context context) {
        return new Thread(() -> {
            try {
                checkPcAwake();
                ToastMessage.showToast(context, "PC의 전원이 연결되어 있습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                ToastMessage.showToast(context, "PC의 전원 켜기에 실패하였습니다.");
            }
        });
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
