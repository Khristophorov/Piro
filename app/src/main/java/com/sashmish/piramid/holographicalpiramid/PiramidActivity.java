package com.sashmish.piramid.holographicalpiramid;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PiramidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int timeout = getIntent().getIntExtra("timeout", 0);
        setContentView(R.layout.activity_piramid);
        fillViewsToScreenResolution();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new CountDownTimer(TimeUnit.SECONDS.toMillis(timeout), 500L) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (timeout != 0) {
                    setResult(ActivityUtils.RESULT_TIMEOUT);
                    finish();
                }
            }
        }.start();
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
            String url = getIntent().getStringExtra("url");
            Ion.with(view).load(url);
        }
    }
}
