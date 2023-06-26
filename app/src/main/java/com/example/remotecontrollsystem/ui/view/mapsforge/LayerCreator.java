package com.example.remotecontrollsystem.ui.view.mapsforge;

import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.layers.LocationTextureLayer;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.util.ArrayList;

public class LayerCreator {
    private MapView mapView;
    private VectorTileLayer tileLayer;

    public LayerCreator(MapView mapView, VectorTileLayer tileLayer) {
        this.mapView = mapView;
        this.tileLayer = tileLayer;
    }

    public LocationTextureLayer createLocationLayer() {
        LocationTextureLayer locationLayer = new LocationTextureLayer(mapView.map());
        locationLayer.setEnabled(false);

        return locationLayer;
    }

    public ItemizedLayer createMarkerLayer(MarkerSymbol symbol, ItemizedLayer.OnItemGestureListener<MarkerInterface> listener) {
        return new ItemizedLayer(mapView.map(), new ArrayList<>(), symbol, listener);
    }

    public BuildingLayer createBuildingLayer() {
        return new BuildingLayer(mapView.map(), tileLayer);
    }

    public LabelLayer createLabelLayer() {
        return new LabelLayer(mapView.map(), tileLayer);
    }

    public MapScaleBarLayer createScaleBarLayer() {
        MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
        mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
        mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);

        return mapScaleBarLayer;
    }

    public PathLayer createPathLayer(int color) {
        return new PathLayer(mapView.map(), color);
    }
}
