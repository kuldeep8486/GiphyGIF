package com.demo.giphydemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ImageSliderActivity extends AppCompatActivity
{
    private Activity activity;
    private GifImageView ivFull;
    private Button btnShare;
    private View llLoading;

    private String imagePath = "", filename = "", imageId = "", appName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_full);

        activity = this;

        appName = activity.getResources().getString(R.string.app_name);

        imagePath = getIntent().getStringExtra("imagePath");
        Log.v("imagePath", imagePath + " ***");

        imageId = getIntent().getStringExtra("imageId");
        filename = imageId + ".gif";
        Log.v("filename", imageId + " ***");

        setUpViews();

        checkStoragePermission();
    }

    private void setUpViews()
    {
        ivFull = (GifImageView) findViewById(R.id.imgFull);
        btnShare = (Button) findViewById(R.id.btnShare);
        llLoading = findViewById(R.id.llLoading);

        btnShare.setVisibility(View.GONE);

        /*Glide.with(activity)
                .load(imagePath)
                .asGif()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(iconView);*/
    }

    private void loadImage()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/");
        if(!file.exists())
        {
            file.mkdirs();
        }

        loadImageAsync();
    }

    private void shareImage(final Uri uri)
    {
        try {
            if(uri == null)
            {
                Toast.makeText(activity, "Failed to share!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared via " + appName);
            startActivity(Intent.createChooser(shareIntent, "Share"));
        }
        catch (Exception e)
        {
            Toast.makeText(activity, "Failed to share!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void onclickEvents()
    {
        btnShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/" + filename);
                    if(file.exists())
                    {
                        shareImage(Uri.fromFile(file));
                    }
                    else
                    {
                        loadImageAsync();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadImageAsync()
    {
        new AsyncTask<Void, Void, Void>()
        {
            private boolean isSuccess = false;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                llLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params)
            {
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/" + filename);
                    if(!file.exists())
                    {
                        download();
                    }
                    else
                    {
                        isSuccess = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            private void download()
            {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(imagePath);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    {
                        isSuccess = false;
                        return;
                    }

                    // download the file
                    input = connection.getInputStream();

                    File file = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/" + filename);

                    output = new FileOutputStream(file.getAbsolutePath());

                    byte data[] = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1)
                    {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return;
                        }
                        output.write(data, 0, count);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }

                isSuccess = true;
            }

            @Override
            protected void onPostExecute(Void dummy)
            {
                try {
                    llLoading.setVisibility(View.GONE);

                    if(isSuccess)
                    {
                        btnShare.setVisibility(View.VISIBLE);

                        File file = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/" + filename);

                        /*Glide.with(activity)
                                .load(file)
//                                .asGif()
                                .crossFade()
                                .into(ivFull);*/

                        GifDrawable gifFromFile = new GifDrawable(file);
                        ivFull.setImageDrawable(gifFromFile);

                        onclickEvents();
                    }
                    else
                    {
                        btnShare.setVisibility(View.GONE);

                        Toast.makeText(activity, "Failed to load file!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
    }

    private static final int PERMISSION_REQUEST_CODE_STORAGE = 2;
    private void checkStoragePermission()
    {
        try
        {
            int result;
            result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED)
            {
                loadImage();
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE_STORAGE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        loadImage();
                    }
                    else
                    {
                        Toast.makeText(activity, "Permissions Denied, we cannot proceed without it.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
