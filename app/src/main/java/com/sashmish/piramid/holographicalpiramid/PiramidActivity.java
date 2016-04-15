package com.sashmish.piramid.holographicalpiramid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityService;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import java.util.Objects;

public class PiramidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piramid);
        fillViewsToScreenResolution();
    }

    private void fillViewsToScreenResolution() {
        int size = ActivityUtils.calculateImageSize(getWindowManager(), 3);

        ViewGroup layout = (ViewGroup) findViewById(R.id.piramidLayout);
        Objects.requireNonNull(layout);
        for (int childNumber = 0; childNumber < layout.getChildCount(); childNumber++) {
            View view = layout.getChildAt(childNumber);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            setImageToView((ImageView) view);
            layoutParams.height = size;
            layoutParams.width = size;
        }
    }

    private void setImageToView(ImageView view) {
        CharSequence viewContentDescription = view.getContentDescription();
        CharSequence imageContentDescription = getResources().getString(R.string.imageAltText);
        if (imageContentDescription.equals(viewContentDescription)) {
            Ion.with(view).load(ActivityService.currentImage.toString());
        }
    }
}
