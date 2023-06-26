package com.example.remotecontrollsystem.mapsforge;

import android.content.res.Resources;
import android.util.Log;

import com.example.remotecontrollsystem.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MapFileManager {
    private Resources resources;

    public MapFileManager(Resources resources) {
        this.resources = resources;
    }

    public MapFileTileSource getMapFileInputStreamDefault() throws IOException {
        MapFileTileSource tileSource = new MapFileTileSource();
        InputStream is = resources.openRawResource(R.raw.south_korea);
        File file = convertInputStreamToFile(is);
        FileInputStream fis = new FileInputStream(file);
        tileSource.setMapFileInputStream(fis);

        IOUtils.closeQuietly(is);
        file.delete();

        return tileSource;
    }

    public MapFileTileSource getMapFileInputStreamFromFileInputStream(FileInputStream fis) {
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFileInputStream(fis);

        return tileSource;
    }

    public static File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile(String.valueOf(inputStream.hashCode()), ".tmp");
        FileUtils.copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }
}
