package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

public class CameraView extends VLCVideoLayout {
    private static final String TAG = CameraView.class.getSimpleName();
    private static final String RTSP_URL = "rtsp://camera-shuttle:1234@192.168.0.56:9000/";

    private MediaPlayer mediaPlayer;
    private LibVLC libVlc;

    public CameraView(@NonNull Context context) {
        super(context);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        Log.d(TAG, "Init RTSP Camera View");
        libVlc = new LibVLC(getContext());
        mediaPlayer = new MediaPlayer(libVlc);

        settingRtspConnection();
    }

    private void settingRtspConnection() {
        Log.d(TAG, "Setting RTSP Camera Connection");

        mediaPlayer.attachViews(this, null, false, false);

        Media media = new Media(libVlc, Uri.parse(RTSP_URL));
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=600");

        mediaPlayer.setMedia(media);
        media.release(); // Release media. Because mediaPlayer was set media.
        mediaPlayer.play();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mediaPlayer.stop();
        mediaPlayer.detachViews();

        mediaPlayer.release();
        libVlc.release();
    }
}
