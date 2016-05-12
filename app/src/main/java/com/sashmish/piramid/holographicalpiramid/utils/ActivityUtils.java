package com.sashmish.piramid.holographicalpiramid.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.sashmish.piramid.holographicalpiramid.R;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ActivityUtils {
    private static final float SELECTED_LEVEL = 0.25F;

    public static final String R_PATH = "android.resource://com.sashmish.piramid.holographicalpiramid/";
    public static final String FILES_LIST = "FilesList";
    public static final String DEFAULT_RESOURCES = ""
            + R_PATH + R.drawable.cat + "\n"
            + R_PATH + R.drawable.clownfish + "\n"
            + R_PATH + R.drawable.spiral;
    public static final int RESULT_TIMEOUT = 5;

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
        url = url.trim();
        File filesList = getFilesList(directory);
        try {
            Files.append("\n" + url, filesList, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error writing url to file", e);
        }
    }

    public static void removeUrlFromFilesList(File directory, CharSequence url) {
        File filesList = getFilesList(directory);
        try {
            String filesListContent = Files.toString(filesList, Charset.defaultCharset());
            String clearedFilesListContent = filesListContent.replace(url, "")
                    .trim()
                    .replaceAll("\n\n", "\n");
            Files.write(clearedFilesListContent, filesList, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting url " + url + " from files list", e);
        }
    }

    public static boolean isViewSelected(View view) {
        float alpha = view.getAlpha();
        return alpha == SELECTED_LEVEL;
    }

    public static int getNewPosition(@NonNull List<?> list, int position) {
        if (++position >= list.size()) {
            return 0;
        }
        return position;
    }
}
