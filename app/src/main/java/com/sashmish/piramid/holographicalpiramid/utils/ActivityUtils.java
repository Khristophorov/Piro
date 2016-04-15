package com.sashmish.piramid.holographicalpiramid.utils;

import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.sashmish.piramid.holographicalpiramid.R;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ActivityUtils {
    public static final String R_PATH = "android.resource://com.sashmish.piramid.holographicalpiramid/";
    public static final String FILES_LIST = "FilesList";
    public static final String DEFAULT_RESOURCES = ""
            + R_PATH + R.drawable.cat + "\n"
            + R_PATH + R.drawable.clownfish + "\n"
            + R_PATH + R.drawable.spiral;

    public static int calculateImageSize(WindowManager window, int divider) {
        Display display = window.getDefaultDisplay();
        Point resolution = new Point();
        display.getSize(resolution);
        int width = resolution.x / divider;
        int height = resolution.y / divider;

        return Math.min(width, height);
    }

    public static List<String> getImageUrls(File directory) {
        File filesList = getFilesList(directory);
        try {
            return Files.readLines(filesList, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error reading data from file: " + filesList.getName(), e);
        }
    }

    private static File getFilesList(File directory) {
        return new File(directory, FILES_LIST);
    }

    public static void appendNewUrl(File directory, String url) {
        if (Strings.isNullOrEmpty(url)) {
            return;
        }
        File filesList = getFilesList(directory);
        try {
            Files.append("\n" + url, filesList, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error writing url to file", e);
        }
    }
}
