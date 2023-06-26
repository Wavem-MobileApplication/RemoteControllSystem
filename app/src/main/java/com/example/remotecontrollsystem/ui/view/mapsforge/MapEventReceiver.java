package com.example.remotecontrollsystem.ui.view.mapsforge;

import android.util.Log;

import org.oscim.core.GeoPoint;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Point;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.map.Map;

public class MapEventReceiver extends Layer implements GestureListener {
    private OnMapLongClickListener listener;

    public MapEventReceiver(Map map) {
        super(map);

    }

    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {
        if (g instanceof Gesture.LongPress) {
            GeoPoint geoPoint = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
            Log.d("GeoPoint", geoPoint.toString());
            listener.onLongClick(geoPoint);

            return true;
        }
        return false;
    }

    public void setOnLongClickListener(OnMapLongClickListener listener) {
        this.listener = listener;
    }

    public interface OnMapLongClickListener {
        void onLongClick(GeoPoint geoPoint);
    }
}
