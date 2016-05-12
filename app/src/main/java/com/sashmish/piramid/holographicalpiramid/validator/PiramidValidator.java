package com.sashmish.piramid.holographicalpiramid.validator;

import android.content.Context;
import android.support.v4.util.Pair;
import android.webkit.URLUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.sashmish.piramid.holographicalpiramid.utils.ActivityUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class PiramidValidator {
    public static Pair<String, Boolean> validateUrl(String url, Context context) {
        FileValidator fileValidator = new FileValidator(url, context);
        if (!URLUtil.isValidUrl(url)) {
            return Pair.create("Invalid URL", false);
        }
        if(isUrlPresent(context, url)) {
            return Pair.create("This file already present", false);
        }
        return fileValidator.validate();
    }

    private static boolean isUrlPresent(Context context, String url) {
        File externalFilesDir = context.getExternalFilesDir(null);
        List<String> urls = ActivityUtils.getImageUrls(externalFilesDir);
        return urls.contains(url);
    }

    public static Collection<String> filterUrls(final Context context, Collection<String> imageUrls) {
        return Collections2.filter(imageUrls, new Predicate<String>() {
            @Override
            public boolean apply(String imageUrl) {
                FileValidator fileValidator = new FileValidator(imageUrl, context);
                return fileValidator.validate().second;
            }
        });
    }
}
