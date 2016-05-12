package com.sashmish.piramid.holographicalpiramid;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ScrollView;

import com.google.common.io.Files;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;
import com.sashmish.piramid.holographicalpiramid.validator.PiramidValidator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractChooseActivity extends AppCompatActivity {
    protected static final int COLUMNS = 3;
    protected int columnNum = 0;
    protected int rowNum = 0;
    protected File directory;

    protected void initGrid() {
        setContentView(R.layout.activity_choose);
        directory = getExternalFilesDir(null);
        makeFilesList();
        GridLayout grid = (GridLayout) findViewById(R.id.chooseLayout);
        Objects.requireNonNull(grid);
        List<String> imageUrls = ActivityUtils.getImageUrls(directory);
        imageUrls = new ArrayList<>(PiramidValidator.filterUrls(getApplicationContext(), imageUrls));
        createImageListener();
        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            View imageView = createImageView(url);
            grid.addView(imageView);
        }
        grid.setColumnCount(COLUMNS);
        grid.setRowCount(rowNum + 1);
    }

    protected abstract void createImageListener();

    private void makeFilesList() {
        File filesList = new File(directory, ActivityUtils.FILES_LIST);
        boolean fileCreated;
        try {
            fileCreated = filesList.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("FileList wasn't created due to: ", e);
        }
        if (fileCreated) {
            try {
                Files.write(ActivityUtils.DEFAULT_RESOURCES, filesList, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException("FileList is not writable: ", e);
            }
        }
    }

    protected void clearGrid() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        Objects.requireNonNull(scrollView);
        scrollView.removeAllViews();
        rowNum = 0;
        columnNum = 0;
    }

    protected abstract View createImageView(String url);

    protected int getNextColumn() {
        int currentColumn = columnNum++;
        if (columnNum > 3) {
            columnNum = 1;
            currentColumn = 0;
            rowNum++;
        }
        return currentColumn;
    }

    protected void refreshGrid() {
        clearGrid();
        initGrid();
    }
}
