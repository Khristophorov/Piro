package com.sashmish.piramid.holographicalpiramid;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MultiChooseActivity extends AbstractChooseActivity {

    private View.OnClickListener imageListener;

    List<CharSequence> urls = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        createImageListener();
        initGrid();
        addApplyButton();
    }

    private void addApplyButton() {
        LinearLayout mainChooseLayout = (LinearLayout) findViewById(R.id.mainChooseLayout);
        Objects.requireNonNull(mainChooseLayout);
        Button applyButton = new Button(this);
        applyButton.setText(R.string.apply);
        applyButton.setTextSize(24);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        applyButton.setLayoutParams(layoutParams);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CharSequence> urls = new ArrayList<>();
                GridLayout grid = (GridLayout) findViewById(R.id.chooseLayout);
                Objects.requireNonNull(grid);
                for (int childNumber = 0; childNumber < grid.getChildCount(); childNumber++) {
                    View child = grid.getChildAt(childNumber);
                    if (ActivityUtils.isViewSelected(child)) {
                        CharSequence url = child.getContentDescription();
                        urls.add(url);
                    }
                }
                if (urls.isEmpty()) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Click on images to choose", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putCharSequenceArrayListExtra("urls", urls);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mainChooseLayout.addView(applyButton, 0);
    }

    @Override
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
        return imageView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    protected void createImageListener() {
        imageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence url = view.getContentDescription();
                if (urls.contains(url)) {
                    urls.remove(url);
                    view.setAlpha(1);
                } else {
                    urls.add(url);
                    view.setAlpha(0.25F);
                }
            }
        };
    }
}
