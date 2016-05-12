package com.sashmish.piramid.holographicalpiramid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import static com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils.RESULT_TIMEOUT;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.ContextMenu.ContextMenuInfo;

public class PlaylistActivity extends AppCompatActivity {
    private static final int COLUMNS = 3;
    private static final String DELETE_STRING = "Delete";
    private int rowNum = 0;
    private int columnNum = 0;
    private GridLayout grid;
    private List<CharSequence> urls = new ArrayList<>();
    private int position;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        adoptViewToScreen();
        configureButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                List<CharSequence> newUrls = data.getCharSequenceArrayListExtra("urls");
                urls.addAll(newUrls);
                initGrid();
                break;
            case RESULT_TIMEOUT:
                startDuratedPiramidActivity();
        }
    }

    private void initGrid() {
        grid = (GridLayout) findViewById(R.id.chosenViews);
        Objects.requireNonNull(grid);
        grid.setColumnCount(COLUMNS);
        int rowCount = (int) Math.ceil(urls.size());
        grid.setRowCount(rowCount);
        refreshGrid();
    }

    private void fillGrid() {
        for (CharSequence url : urls) {
            ImageView imageView = new ImageView(this);
            int size = ActivityUtils.calculateImageSize(getWindowManager(), COLUMNS);
            int viewId = Math.abs(url.hashCode());
            Ion.with(imageView).animateGif(AnimateGifMode.NO_ANIMATE).load(url.toString());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;
            layoutParams.columnSpec = GridLayout.spec(getNextColumn());
            layoutParams.rowSpec = GridLayout.spec(rowNum);
            imageView.setContentDescription(url);
            imageView.setId(viewId);
            imageView.setLayoutParams(layoutParams);
            registerForContextMenu(imageView);
            grid.addView(imageView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
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
        urls.remove(url);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.removeAllViews();
        rowNum = 0;
        columnNum = 0;
        fillGrid();
    }

    protected int getNextColumn() {
        int currentColumn = columnNum++;
        if (columnNum > 3) {
            columnNum = 1;
            currentColumn = 0;
            rowNum++;
        }
        return currentColumn;
    }

    private void configureButtons() {
        View addImagesButton = findViewById(R.id.addImages);
        View runSlideShowButton = findViewById(R.id.slideshow);
        Objects.requireNonNull(addImagesButton);
        Objects.requireNonNull(runSlideShowButton);
        addImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent multiChooseIntent = new Intent(view.getContext(), MultiChooseActivity.class);
                startActivityForResult(multiChooseIntent, 0);
            }
        });
        runSlideShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urls == null || urls.isEmpty()) {
                    return;
                }
                EditText durationInput = (EditText) findViewById(R.id.durationInput);
                Objects.requireNonNull(durationInput);
                CharSequence durationString = durationInput.getText();
                if (TextUtils.isEmpty(durationString) || !TextUtils.isDigitsOnly(durationString)) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Please, set valid duration", Toast.LENGTH_LONG).show();
                    return;
                }
                duration = Integer.parseInt(durationString.toString());
                startDuratedPiramidActivity();
            }
        });
    }

    private void startDuratedPiramidActivity() {
        Intent intent = new Intent(this, PiramidActivity.class);
        String url = urls.get(position).toString();
        position = ActivityUtils.getNewPosition(urls, position);
        intent.putExtra("url", url);
        intent.putExtra("timeout", duration);
        startActivityForResult(intent, 0);
    }

    private void adoptViewToScreen() {
        View addImagesButton = findViewById(R.id.addImages);
        View slideshowButton = findViewById(R.id.slideshow);
        View duration = findViewById(R.id.durationLayout);
        Objects.requireNonNull(duration);
        ViewGroup.LayoutParams durationParams = duration.getLayoutParams();
        int size = ActivityUtils.calculateImageSize(getWindowManager(), 2);
        setViewWidth(addImagesButton, size, durationParams);
        setViewWidth(slideshowButton, size, durationParams);
    }

    private void setViewWidth(View view, int size, ViewGroup.LayoutParams otherViewsParams) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = size - (otherViewsParams.width / 2);
    }
}
