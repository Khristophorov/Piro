package com.sashmish.piramid.holographicalpiramid.validator;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.webkit.URLUtil.isHttpUrl;
import static android.webkit.URLUtil.isHttpsUrl;

class FileValidator implements Validator {
    private static final String IMAGE = "image";

    private String url;
    private ContentResolver contentResolver;

    FileValidator(String url, Context context) {
        this.url = url;
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public Pair<String, Boolean> validate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if (isHttpUrl(url) || isHttpsUrl(url)) {
                return validateWebResource();
            }
            if (url.startsWith("content")){
                validateContentResource();
            }
            return Pair.create("", true);
        } catch (MalformedURLException e) {
            return Pair.create("Malformed URL format of " + url, false);
        } catch (IOException e) {
            return Pair.create("Error reading content from " + url, false);
        }
    }

    private Pair<String,Boolean> validateContentResource() throws IOException {
        try {
            contentResolver.openInputStream(Uri.parse(url));
        } catch (FileNotFoundException e) {
            return Pair.create("File " + url + " doesn't exist", false);
        }
        return Pair.create("", true);
    }

    private Pair<String, Boolean> validateWebResource() throws IOException {
        String contentType = new URL(url).openConnection().getContentType();
        String invalidContent = "Current web page is not an image";
        Boolean isValid = !TextUtils.isEmpty(contentType) && contentType.startsWith(IMAGE);
        return Pair.create(isValid ? "" : invalidContent, isValid);
    }
}
