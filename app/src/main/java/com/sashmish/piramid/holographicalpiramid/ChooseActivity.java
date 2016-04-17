package com.sashmish.piramid.holographicalpiramid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.common.io.Files;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityService;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class ChooseActivity extends AppCompatActivity {

    private static final int COLUMNS = 3;
    private static final String DELETE_STRING = "Delete";

    private int columnNum = 0;
    private int rowNum = 0;
    private View.OnClickListener imageListener;
    private File directory;
    EditText inputDialog;

    @Override
    protected void onResume() {
        super.onResume();
        initGrid();
    }

    private void initGrid() {
        setContentView(R.layout.activity_choose);
        directory = getExternalFilesDir(null);
        makeFilesList();
        GridLayout grid = (GridLayout) findViewById(R.id.chooseLayout);
        Objects.requireNonNull(grid);
        List<String> imageUrls = ActivityUtils.getImageUrls(directory);
        createImageListener();
        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            ImageView imageView = createImageView(url);
            grid.addView(imageView);
        }
        grid.setColumnCount(COLUMNS);
        grid.setRowCount(rowNum + 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        clearGrid();
    }

    private void clearGrid() {
        ScrollView scrollVeiew = (ScrollView) findViewById(R.id.scrollView);
        Objects.requireNonNull(scrollVeiew);
        scrollVeiew.removeAllViews();
        rowNum = 0;
        columnNum = 0;
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId) {
            case R.id.addImage:
                addNewImage();
                break;
            case R.id.addPlayList:
                System.out.println("сорян эта кнопочка в раз-ке");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void addNewImage() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(R.layout.input_source)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CharSequence url = inputDialog.getText();
                                ActivityUtils.appendNewUrl(directory, url.toString());
                                initPiramidActivity(inputDialog, url);
                            }
                        });
        AlertDialog inputUrlDialog = alertBuilder.create();
        inputUrlDialog.show();
        Button addFromGalleryButton = (Button) inputUrlDialog.findViewById(R.id.addFromGallery);
        Objects.requireNonNull(addFromGalleryButton);
        inputDialog = (EditText) inputUrlDialog.findViewById(R.id.inputSource);
        addFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    private void createImageListener() {
        imageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence url = view.getContentDescription();
                initPiramidActivity(view, url);
            }
        };
    }

    private void initPiramidActivity(View view, CharSequence url) {
        ActivityService.currentImage = url;
        Context currentContext = view.getContext();
        Intent intent = new Intent(currentContext, PiramidActivity.class);
        startActivityForResult(intent, 0);
    }

    private ImageView createImageView(String url) {
        ImageView imageView = new ImageView(this);
        int size = ActivityUtils.calculateImageSize(getWindowManager(), COLUMNS);
        int viewId = Math.abs(url.hashCode());
        Ion.with(imageView).animateGif(AnimateGifMode.NO_ANIMATE).load(url);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        layoutParams.columnSpec = GridLayout.spec(getNextColumn());
        layoutParams.rowSpec = GridLayout.spec(rowNum);
        imageView.setContentDescription(url);
        imageView.setId(viewId);
        imageView.setLayoutParams(layoutParams);
        imageView.setClickable(true);
        imageView.setOnClickListener(imageListener);
        registerForContextMenu(imageView);
        return imageView;
    }

    private int getNextColumn() {
        int currentColumn = columnNum++;
        if (columnNum > 3) {
            columnNum = 1;
            currentColumn = 0;
            rowNum++;
        }
        return currentColumn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String url = data.getDataString();
            inputDialog.setText(url);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = view.getId();
        menu.add(0, viewId, 0, DELETE_STRING);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        int viewId = item.getItemId();
        View view = findViewById(viewId);
        switch (title) {
            case DELETE_STRING:
                deleteView(view);
        }
        return super.onContextItemSelected(item);
    }

    private void refreshGrid() {
        clearGrid();
        initGrid();
    }

    private void deleteView(View view) {
        CharSequence url = view.getContentDescription();
        ActivityUtils.removeUrlFromFilesList(directory, url);
        refreshGrid();
    }
}