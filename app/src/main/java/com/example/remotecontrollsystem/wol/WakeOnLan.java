package com.example.remotecontrollsystem.wol;

import android.content.Context;
import android.util.Log;

import com.example.remotecontrollsystem.ui.util.ToastMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class WakeOnLan {
    private static final String TAG = WakeOnLan.class.getSimpleName();

    private Context mContext;

    private String macAddress;
    private String ipAddress;

    private WakeOnLanChecker wakeOnLanChecker;
    private Thread wakeOnLanThread;

    public WakeOnLan(Context context) {
        this.mContext = context;
        wakeOnLanChecker = new WakeOnLanChecker(context);
        wakeOnLanThread = createNewThread(context);
    }

    public void startCheckWakeOnLan() {
        wakeOnLanChecker.startCheckPcAwake();
    }

    public void startWakeOnLan() {
        Thread.State state = wakeOnLanThread.getState();
        Log.d(TAG, "State -> " + state.name());
        switch (state) {
            case TERMINATED:
                wakeOnLanThread = createNewThread(mContext);
                wakeOnLanThread.start();
                startCheckWakeOnLan();
                break;
            case NEW:
                wakeOnLanThread.start();
                startCheckWakeOnLan();
                break;
        }
    }

    public void sendWakeOnLan(String macAddress, String ipAddress) throws IOException {
            byte[] macBytes = getMacBytes(macAddress);
            byte[] magicPacket = createMagicPacket(macBytes);
            InetAddress address = InetAddress.getByName(ipAddress);

            DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, 9);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
    }

    private byte[] getMacBytes(String macAddress) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macAddress.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address format");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address");
        }
        return bytes;
    }

    private byte[] createMagicPacket(byte[] macBytes) {
        byte[] magicPacket = new byte[102];
        for (int i = 0; i < 6; i++) {
            magicPacket[i] = (byte) 0xff;
        }

        for (int i = 6; i < magicPacket.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, magicPacket, i, macBytes.length);
        }

        return magicPacket;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private Thread createNewThread(Context context) {
        return new Thread(() -> {
            try {
                sendWakeOnLan(macAddress, ipAddress);
            } catch (IOException e) {
                e.printStackTrace();
                ToastMessage.showToast(context, "전송실패: 네트워크를 확인해주세요.");
            }
        });
    }
}
