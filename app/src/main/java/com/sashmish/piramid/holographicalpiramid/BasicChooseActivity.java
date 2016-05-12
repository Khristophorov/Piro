package com.sashmish.piramid.holographicalpiramid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;
import com.sashmish.piramid.holographicalpiramid.validator.PiramidValidator;

import java.util.Objects;

public class BasicChooseActivity extends AbstractChooseActivity {
    private static final String DELETE_STRING = "Delete";
    private View.OnClickListener imageListener;

    EditText inputDialog;

    @Override
    protected void onResume() {
        super.onResume();
        initGrid();
    }

    @Override
    public void onPause() {
        super.onPause();
        clearGrid();
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
                Intent addPlayListIntent = new Intent(this, PlaylistActivity.class);
                startActivity(addPlayListIntent);
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
                                Context context = getApplicationContext();
                                Pair<String, Boolean> validationResults = PiramidValidator.validateUrl(url.toString(), context);
                                if (!validationResults.second) {
                                    Toast.makeText(context, validationResults.first, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ActivityUtils.appendNewUrl(directory, url.toString());
                                initPiramidActivity(url);
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

    protected void createImageListener() {
        imageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence url = view.getContentDescription();
                initPiramidActivity(url);
            }
        };
    }

    private void initPiramidActivity(CharSequence url) {
        Intent intent = new Intent(this, PiramidActivity.class);
        intent.putExtra("url", url.toString());
        startActivityForResult(intent, 0);
    }

    protected View createImageView(String url) {
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
        imageView.setOnClickListener(imageListener);
        registerForContextMenu(imageView);
        return imageView;
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

    private void deleteView(View view) {
        CharSequence url = view.getContentDescription();
        ActivityUtils.removeUrlFromFilesList(directory, url);
        refreshGrid();
    }
}