package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.ui.dialog.LoadingDialog;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class CameraView extends VLCVideoLayout {
    private static final String TAG = CameraView.class.getSimpleName();
    private static final String RTSP_URL = "rtsp://camera-shuttle:1234@192.168.0.56:9000/";

    private MediaPlayer mediaPlayer;
    private LibVLC libVlc;
    private TextView textView;
    private ProgressBar progressBar;


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

        if (progressBar == null) {
            progressBar = new ProgressBar(getContext());
            progressBar.post(() -> {
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = getWidth() / 3;
                layoutParams.height = getHeight() / 3;
                progressBar.setLayoutParams(layoutParams);
            });
        }

        if (textView == null) {
            textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(Color.WHITE);
        }
    }

    private void init() {
        Log.d(TAG, "Try to connect RTSP Camera View");
        libVlc = new LibVLC(getContext());
        mediaPlayer = new MediaPlayer(libVlc);
        mediaPlayer.attachViews(this, null, false, false);
    }

    public void settingRtspConnection(String url) {
        Log.d(TAG, "Setting RTSP Camera Connection");

        Media media = new Media(libVlc, Uri.parse(url));
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=300");

        addView(progressBar);

        mediaPlayer.setMedia(media);
        mediaPlayer.setEventListener(event -> {
            switch (event.type) {
                case MediaPlayer.Event.Vout:
                    removeView(textView);
                    removeView(progressBar);
                    break;
                case MediaPlayer.Event.EncounteredError:
                    textView.setText("연결에 실패하였습니다.\n네트워크를 확인해주세요.");
                    removeView(progressBar);
                    removeView(textView);
                    addView(textView);
                    break;
                case MediaPlayer.Event.EndReached:
                    Log.d("연결", "EndReached");
                    removeAllViews();
                    post(() -> {
                        textView.setText("카메라와의 연결이 끊겼습니다.\n네트워크를 확인해주세요.");
                        addView(textView);
                    });
                    break;
            }
        });

        media.release(); // Release media. Because mediaPlayer was set media.
        mediaPlayer.play();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        LoadingDialog dialog = new LoadingDialog(getContext());
        dialog.setText("리소스 정리중...");

        Disposable backgroundTask = Observable.fromCallable(() -> {
                    try {
                        if (mediaPlayer != null && !mediaPlayer.isReleased()) {
                            mediaPlayer.stop();
                            mediaPlayer.detachViews();
                            mediaPlayer.release();
                        }

                        Log.d(TAG, "Finish to release media player");
                        if (libVlc != null && !libVlc.isReleased()) {
                            libVlc.release();
                        }

                        Log.d(TAG, "Finish to release libVlc");

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();

                        Log.e(TAG, "Failed to release resources");

                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    dialog.dismiss();
                });
    }
}
