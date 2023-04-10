package com.savvy.hrmsnewapp.helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.savvy.hrmsnewapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class DownloadImageTask extends AsyncTask<String, Void, Drawable> {

    ImageView imageView;


    public DownloadImageTask(ImageView bmImage) {
        this.imageView = bmImage;
    }

    @Override
    protected Drawable doInBackground(String... strings) {
        String url = strings[0];
        Drawable d = null;
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            d = Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return d;

    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setImageResource(R.drawable.trans_profile_image);
        }
    }
}
