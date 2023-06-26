package com.example.remotecontrollsystem.mapsforge;

import android.content.res.Resources;

import com.example.remotecontrollsystem.R;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class BitmapManager {
    private final Resources resources;

    public BitmapManager(Resources resources) {
        this.resources = resources;
    }

    public Bitmap createBitmap(int id, int percent) throws IOException {
        InputStream is = resources.openRawResource(id);
        Bitmap bitmap = CanvasAdapter.decodeSvgBitmap(
                is,
                (int) (48 * CanvasAdapter.getScale()),
                (int) (48 * CanvasAdapter.getScale()),
                percent
        );

        IOUtils.closeQuietly(is);

        return bitmap;
    }

    public Bitmap createDefaultMarkerBitmap() throws IOException {
        return createBitmap(R.raw.marker, 100);
    }

    public Bitmap createDefaultArrowBitmap() throws IOException {
        return createBitmap(R.raw.navigation_2, 50);
    }

    public Bitmap createDefaultWaypointMarker() throws IOException {
        return createBitmap(R.raw.waypoint_marker, 50);
    }
}
