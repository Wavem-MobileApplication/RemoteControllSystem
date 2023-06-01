package com.example.remotecontrollsystem.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {
    public void sendWakeOnLan(String macAddress, String ipAddress) {
        try {
            byte[] macBytes = getMacBytes(macAddress);
            byte[] magicPacket = createMagicPacket(macBytes);
            InetAddress address = InetAddress.getByName(ipAddress);

            DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, 9);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            // WOL 전송 성공 처리
        } catch (Exception e) {
            e.printStackTrace();
            // WOL 전송 실패 처리
        }
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
}
