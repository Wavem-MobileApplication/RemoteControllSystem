package com.example.remotecontrollsystem.ui.view.mapsforge;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.ui.util.ToastMessage;

import org.apache.commons.io.FileUtils;
import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.LocationTextureLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LocationCallback;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GpsMapView extends FrameLayout {
    private static final String TAG = GpsMapView.class.getSimpleName();
    private static final double INITIAL_LATITUDE_DAIN = 37.230207;
    private static final double INITIAL_LONGITUDE_DAIN = 127.108412;

    private MapView mapView;
    private IRenderTheme theme;
    private LocationTextureLayer locationLayer;
    private Location location;

    public GpsMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public GpsMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(() -> {
            try {
                drawMap(getMapFileInputStreamDefault());
            } catch (IOException e) {
                e.printStackTrace();
                ToastMessage.showToast(getContext(), "맵 로딩에 실패하였습니다.");
            }
        });
    }

    private void init() {
        // Initialize location
        location = new Location("ugv_position");
        location.setLatitude(INITIAL_LATITUDE_DAIN);
        location.setLongitude(INITIAL_LONGITUDE_DAIN);
        location.setBearing(0f);
        location.setAccuracy(0f);

        // Initialize and add mapView to GpsMapView
        mapView = new MapView(getContext());
        addView(mapView);

        // Initialize and add locationLayer to mapView
        locationLayer = new LocationTextureLayer(mapView.map());

        // Setting bitmaps of locationLayer
        InputStream markerIs = getResources().openRawResource(R.raw.marker);
        InputStream arrowIs = getResources().openRawResource(R.raw.navigation);

        Bitmap markerBitmap = null;
        Bitmap arrowBitmap = null;

        try {
            markerBitmap = CanvasAdapter.decodeSvgBitmap(
                    markerIs,
                    (int) (48 * CanvasAdapter.getScale()),
                    (int) (48 * CanvasAdapter.getScale()),
                    100
            );
            arrowBitmap = CanvasAdapter.decodeSvgBitmap(
                    arrowIs,
                    (int) (48 * CanvasAdapter.getScale()),
                    (int) (48 * CanvasAdapter.getScale()),
                    100
            );
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(markerIs);
            IOUtils.closeQuietly(arrowIs);
        }

        locationLayer.locationRenderer.setBitmapMarker(markerBitmap);
        locationLayer.locationRenderer.setBitmapArrow(arrowBitmap);
        locationLayer.setEnabled(false); // locationLayer is enabled when mapView has map data.
        locationLayer.locationRenderer.setCallback(new LocationCallback() {
            @Override
            public boolean hasRotation() {
                return location != null && location.hasBearing();
            }

            @Override
            public float getRotation() {
                return location != null && location.hasBearing() ? location.getBearing() : 0;
            }
        });
    }

    public void drawMap(MapFileTileSource tileSource) throws IOException {
        clearLayers();

        // Vector layer
        VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

        // Building layer
        mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

        // Label layer
        mapView.map().layers().add(new LabelLayer(mapView.map(), tileLayer));

        // Render theme
        theme =  mapView.map().setTheme(VtmThemes.DEFAULT);

        // Scale bar
        MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
        mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
        mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);

        mapView.map().layers().add(mapScaleBarLayer);

        // Location Layer
        mapView.map().layers().add(locationLayer);
        locationLayer.setEnabled(true);
        locationLayer.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());

        // Initialize initial GPS position
        mapView.map().setMapPosition(INITIAL_LATITUDE_DAIN, INITIAL_LONGITUDE_DAIN, 1 << 18);
    }

    private void clearLayers() {
        // If MapView has layers except default mapLayer, start to remove another layers
        int layersSize = mapView.map().layers().size();
        if (layersSize > 1) {
            Log.d(TAG, "Start to clear mapsforge layers of MapView");

            for (int i = layersSize - 1; i > 0; i--) {
                Log.d("Remove Layer", mapView.map().layers().get(i).getClass().getSimpleName());
                mapView.map().layers().remove(i);
            }

            Log.d(TAG, "Removing layer process is finished. Remained layers num : " + mapView.map().layers().size());
        }
    }

    private MapFileTileSource getMapFileInputStreamDefault() throws IOException {
        MapFileTileSource tileSource = new MapFileTileSource();
        InputStream is = getResources().openRawResource(R.raw.south_korea);
        File file = convertInputStreamToFile(is);
        FileInputStream fis = new FileInputStream(file);
        tileSource.setMapFileInputStream(fis);

        return tileSource;
    }

    private MapFileTileSource getMapFileInputStreamFromFileInputStream(FileInputStream fis) {
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFileInputStream(fis);

        return tileSource;
    }

    public static File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile(String.valueOf(inputStream.hashCode()), ".tmp");
        tempFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }

    public void updatePosition(double latitude, double longitude, float bearing) {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setBearing(bearing);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (theme != null) {
            theme.dispose();
        }
    }
}
