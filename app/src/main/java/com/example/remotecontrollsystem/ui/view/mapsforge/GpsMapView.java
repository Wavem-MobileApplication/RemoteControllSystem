package com.example.remotecontrollsystem.ui.view.mapsforge;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mapsforge.BitmapManager;
import com.example.remotecontrollsystem.mapsforge.MapFileManager;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.NavSatFix;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.PoseStamped;
import com.example.remotecontrollsystem.ui.dialog.LoadingDialog;
import com.example.remotecontrollsystem.ui.util.ToastMessage;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequences;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidCanvas;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.LocationTextureLayer;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.CircleDrawable;
import org.oscim.layers.vector.geometries.JtsDrawable;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.renderer.LocationCallback;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GpsMapView extends FrameLayout {
    private static final String TAG = GpsMapView.class.getSimpleName();
    private static final double INITIAL_LATITUDE_DAIN = 37.230207;
    private static final double INITIAL_LONGITUDE_DAIN = 127.108412;
    private static final double INITIAL_LATITUDE_SUNGNAM = 37.462609;
    private static final double INITIAL_LONGITUDE_SUNGNAM = 127.123519;

    private MapView mapView;
    private IRenderTheme theme;

    private LocationTextureLayer locationLayer;
    private PathLayer pathLayer;
    private PathLayer drivingPathLayer;
    private ItemizedLayer waypointMarkerLayer;
    private ItemizedLayer poseMarkerLayer;
    private ItemizedLayer drivingMarkerLayer;
    private Location location;

    private ImageButton cameraFixButton;

    private OnGpsMapLongClickListener onLongClickListener;
    private OnPoseClickListener onPoseClickListener;

    private boolean cameraFix = false;

    public GpsMapView(@NonNull Context context) {
        super(context);
        Log.d(TAG, "Initialize");

        init();
        addButtons();
    }

    public GpsMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Initialize");

        init();
        addButtons();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        LoadingDialog dialog = new LoadingDialog(getContext());
        dialog.setText("지도 로딩중...");

        Disposable backgroundTask = Observable.fromCallable(() -> {
                    post(() -> dialog.show());
                    try {
                        MapFileManager mapFileManager = new MapFileManager(getResources());
                        drawMap(mapFileManager.getMapFileInputStreamDefault());

                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    dialog.dismiss();

                    if (result) {
                        // Initialize initial GPS position
                        mapView.map().setMapPosition(INITIAL_LATITUDE_SUNGNAM, INITIAL_LONGITUDE_SUNGNAM, 1 << 18);
                    } else {
                        ToastMessage.showToast(getContext(), "오프라인 맵 불러오기에 실해하였습니다.");
                    }
                });
    }

    private void addButtons() {
        cameraFixButton = GpsMapViewUtil.createImageButton(getContext(), R.drawable.icon_home, v -> {
            if (cameraFix) {
                cameraFix = false;
            } else {
                cameraFix = true;
                MapPosition mapPosition = mapView.map().getMapPosition();
                mapPosition.setPosition(location.getLatitude(), location.getLongitude());
                mapView.map().animator().animateTo(100, mapPosition);
            }
        });

        post(() -> addView(cameraFixButton, GpsMapViewUtil.createLayoutParams(getWidth(), getHeight(), 10)));
    }

    private void init() {
        // Initialize location
        location = new Location("ugv_position");
        location.setLatitude(INITIAL_LATITUDE_SUNGNAM);
        location.setLongitude(INITIAL_LONGITUDE_SUNGNAM);
        location.setBearing(0f);
        location.setAccuracy(0f);

        // Initialize and add mapView to GpsMapView
        mapView = new MapView(getContext());
        addView(mapView);

        // Create Bitmaps
        BitmapManager bitmapManager = new BitmapManager(getResources());
        Bitmap markerBitmap = null;
        Bitmap arrowBitmap = null;
        Bitmap waypointBitmap = null;
        Bitmap poseBitmap = null;
        try {
            markerBitmap = bitmapManager.createBitmap(R.raw.marker, 100);
            arrowBitmap = bitmapManager.createBitmap(R.raw.navigation_2, 50);
            waypointBitmap = bitmapManager.createBitmap(R.raw.waypoint_marker, 100);
            poseBitmap = bitmapManager.createBitmap(R.raw.destination_marker, 70);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Initialize and add locationLayer to mapView
        locationLayer = new LocationTextureLayer(mapView.map());

        // Setting bitmaps of locationLayer
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

        // Initialize Marker layer
        MarkerSymbol markerSymbol = new MarkerSymbol(markerBitmap, MarkerSymbol.HotspotPlace.CENTER);
        MarkerSymbol waypointSymbol = new MarkerSymbol(waypointBitmap, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        MarkerSymbol poseSymbol = new MarkerSymbol(poseBitmap, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        waypointMarkerLayer = new ItemizedLayer(mapView.map(), new ArrayList<>(), waypointSymbol, new ItemizedLayer.OnItemGestureListener<MarkerInterface>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerInterface item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerInterface item) {
                return false;
            }
        });

        poseMarkerLayer = new ItemizedLayer(mapView.map(), new ArrayList<>(), poseSymbol, new ItemizedLayer.OnItemGestureListener<MarkerInterface>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerInterface item) {
                MarkerItem markerItem = (MarkerItem) item;
                if (markerItem.uid instanceof Pose) {
                    Pose pose = (Pose) markerItem.uid;
                    onPoseClickListener.onItemClick(pose);
                }
                return false;
            }
            @Override
            public boolean onItemLongPress(int index, MarkerInterface item) {
                return false;
            }
        });

        drivingMarkerLayer = new ItemizedLayer(mapView.map(), new ArrayList<>(), markerSymbol, new ItemizedLayer.OnItemGestureListener<>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerInterface item) {
                return false;
            }
            @Override
            public boolean onItemLongPress(int index, MarkerInterface item) {
                return false;
            }
        });

        pathLayer = new PathLayer(mapView.map(), Color.RED);
        drivingPathLayer = new PathLayer(mapView.map(), Color.GREEN);
    }

    public void drawMap(MapFileTileSource tileSource) throws IOException {
        mapView.map().clearMap();
        clearLayers();

        // Event receiver
        MapEventReceiver eventReceiver = new MapEventReceiver(mapView.map());
        eventReceiver.setOnLongClickListener(geoPoint -> {
            if (onLongClickListener != null) {
                onLongClickListener.onLongClick(geoPoint);
            }
        });

        mapView.map().layers().add(eventReceiver);

        // Vector layer
        VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

        // Layer Creator
        LayerCreator layerCreator = new LayerCreator(mapView, tileLayer);

        // Building layer
        mapView.map().layers().add(layerCreator.createBuildingLayer());

        // Label layer
        mapView.map().layers().add(layerCreator.createLabelLayer());

        // Render theme
        theme = mapView.map().setTheme(VtmThemes.DEFAULT);

        // Scale bar
        mapView.map().layers().add(layerCreator.createScaleBarLayer());

        // Location Layer
        mapView.map().layers().add(locationLayer);
        locationLayer.setEnabled(true);
        locationLayer.locationRenderer.setAnimate(true);
        locationLayer.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());

        // Path layer
        mapView.map().layers().add(pathLayer);
        mapView.map().layers().add(drivingPathLayer);

        // Marker layer
        mapView.map().layers().add(waypointMarkerLayer);
        mapView.map().layers().add(poseMarkerLayer);
        mapView.map().layers().add(drivingMarkerLayer);
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

    public RosObserver<NavSatFix> navSatFixObserver = new RosObserver<>(NavSatFix.class) {
        @Override
        public void onChange(NavSatFix navSatFix) {
            location.setLatitude(navSatFix.getLatitude());
            location.setLongitude(navSatFix.getLongitude());
            locationLayer.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());

            if (cameraFix) {
                MapPosition mapPosition = mapView.map().getMapPosition();
                mapPosition.setPosition(navSatFix.getLatitude(), navSatFix.getLongitude());
                mapView.map().animator().animateTo(100, mapPosition);
            }
        }
    };

    public RosObserver<PoseStamped> rttOdomObserver = new RosObserver<>(PoseStamped.class) {
        @Override
        public void onChange(PoseStamped pose) {
            location.setBearing((float) Math.toDegrees(-pose.getPose().getOrientation().getX()) + 90);
            locationLayer.map().updateMap(false);
        }
    };

    public Observer<Route> currentRouteObserver = new Observer<>() {
        @Override
        public void onChanged(Route route) {
            waypointMarkerLayer.removeAllItems();
            drivingMarkerLayer.removeAllItems();
            drivingPathLayer.clearPath();

            for (Waypoint waypoint : route.getWaypointList()) {
                int size = waypoint.getPoseList().size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Pose pose = waypoint.getPoseList().get(i);
                        GeoPoint geoPoint = new GeoPoint(pose.getPosition().getX(), pose.getPosition().getY());

                        if (i < size - 1) {
                            MarkerItem markerItem = new MarkerItem(geoPoint.toString(), geoPoint.toString(), geoPoint);
                            drivingMarkerLayer.addItem(markerItem);
                            Log.d("Driving Marker Layer", "Add Item:" + i);
                        } else {
                            MarkerItem markerItem = new MarkerItem(waypoint.getName(), geoPoint.toString(), geoPoint);
                            waypointMarkerLayer.addItem(markerItem);
                        }

                        drivingPathLayer.addPoint(geoPoint);
                    }
                }
            }

            poseMarkerLayer.map().updateMap(false);
            drivingMarkerLayer.map().updateMap(false);
            drivingPathLayer.map().updateMap(false);
        }
    };

    public Observer<Waypoint> currentWaypointObserver = new Observer<Waypoint>() {
        @Override
        public void onChanged(Waypoint waypoint) {
            // Clear all pose markers
            poseMarkerLayer.removeAllItems();

            // Clear all paths
            if (pathLayer != null) {
                pathLayer.clearPath();
            }

            for (Pose pose : waypoint.getPoseList()) {
                GeoPoint geoPoint = new GeoPoint(pose.getPosition().getX(), pose.getPosition().getY());
                MarkerItem markerItem = new MarkerItem(pose, geoPoint.toString(), geoPoint.toString(), geoPoint);
                poseMarkerLayer.addItem(markerItem);
                pathLayer.addPoint(geoPoint);
            }

            pathLayer.map().updateMap(false);
            poseMarkerLayer.map().updateMap(false);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");

        if (theme != null) {
            theme.dispose();
        }

        mapView.onDestroy();
    }

    public void setOnLongClickListener(OnGpsMapLongClickListener listener) {
        this.onLongClickListener = listener;
    }

    public void setOnPoseClickListener(OnPoseClickListener listener) {
        this.onPoseClickListener = listener;
    }

    public interface OnPoseClickListener {
        void onItemClick(Pose pose);
    }

    public interface OnGpsMapLongClickListener {
        void onLongClick(GeoPoint geoPoint);
    }
}
