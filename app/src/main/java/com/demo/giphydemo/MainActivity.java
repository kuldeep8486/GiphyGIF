package com.demo.giphydemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.mit.mitsutils.MitsUtils;
import com.bumptech.glide.Glide;
import com.demo.MyApplication;
import com.demo.classes.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener
{
    private Activity activity;

    private View llLoading;
    private RecyclerView rvCategories;

    private ArrayList<ImagePojo> listImages = new ArrayList<>();

    private ImageRecyclerAdapter imageRecyclerAdapter;

    private int pageNum = 1;

    private boolean isLastPage = false;

    private static final String API_KEY = "64ff4a537c214c6dae5cb8c40b362dc1";

    public static boolean isForcefulUpdateDialogOpen = false;

    private boolean isLoading = false;
    private boolean isLoadingPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = this;

        pageNum = 1;

        setContentView(R.layout.activity_main);

        setupViews();

        loadImagesAsync(true);
    }

    private void setupViews()
    {
        llLoading = findViewById(R.id.llLoading);

        rvCategories = (RecyclerView) findViewById(R.id.rvCategories);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 2);
        rvCategories.setLayoutManager(layoutManager);

        rvCategories.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount)
            {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                try {
                    /*if(!isLoading && !isLastPage)
                    {
                        loadImagesAsync(false);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadImagesAsync(final boolean isFirstTime)
    {
        try
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected void onPreExecute()
                {
                    isLoading = true;
                    if(isFirstTime)
                    {
                        listImages = new ArrayList<>();
                        llLoading.setVisibility(View.VISIBLE);
                    }
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... params)
                {
                    try
                    {
                        String URL = "https://api.giphy.com/v1/gifs/trending?api_key=" + API_KEY + "&limit=25&rating=G";

                        String response = MitsUtils.readJSONServiceUsingGET(URL);

//                        Log.v("API RESPONSE", response + "***");

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        pageNum = pageNum + 1;

                        if(jsonArray.length() < 25)
                        {
                            isLastPage = true;
                        }

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            JSONObject jsonObjectChild = jsonArray.getJSONObject(i);
                            String imageId = Utils.getValidAPIStringResponse(jsonObjectChild.getString("id"));

                            JSONObject jsonObjectImage = jsonObjectChild.getJSONObject("images");

                            JSONObject jsonObjectThumb = jsonObjectImage.getJSONObject("preview_webp");
                            String thumbPath = Utils.getValidAPIStringResponse(jsonObjectThumb.getString("url"));

                            JSONObject jsonObjectGif = jsonObjectImage.getJSONObject("fixed_height_downsampled");
                            String imagePath = Utils.getValidAPIStringResponse(jsonObjectGif.getString("url"));

                            JSONObject jsonObjectStill = jsonObjectImage.getJSONObject("fixed_height_still");
                            String detailThumb = Utils.getValidAPIStringResponse(jsonObjectStill.getString("url"));

                            ImagePojo imagePojo = new ImagePojo();
                            imagePojo.setImageId(imageId);
                            imagePojo.setThumbPath(thumbPath);
                            imagePojo.setImagePath(imagePath);
                            imagePojo.setDetailThumb(detailThumb);
                            listImages.add(imagePojo);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);

                    try {
                        isLoading = false;

                        if(isFirstTime)
                        {
                            llLoading.setVisibility(View.GONE);

                            imageRecyclerAdapter = new ImageRecyclerAdapter(listImages);
                            rvCategories.setAdapter(imageRecyclerAdapter);
                        }
                        else
                        {
                            imageRecyclerAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>
    {
        public ArrayList<ImagePojo> items;

        ImageRecyclerAdapter(ArrayList<ImagePojo> list)
        {
            this.items = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowview_image, viewGroup, false);

            return new ViewHolder(v);
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            final ImageView ivImage;

            ViewHolder(View convertView)
            {
                super(convertView);
                ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            final ImagePojo giftPojo = items.get(position);

            if(giftPojo.getImagePath() != null && giftPojo.getImagePath().length() > 0)
            {
                Glide.with(activity)
                        .load(giftPojo.getImagePath())
                        .asGif()
                        .centerCrop()
                        .crossFade()
                        .into(holder.ivImage);
            }
            else
            {
                Glide.with(activity)
                        .load(R.mipmap.ic_launcher)
                        .into(holder.ivImage);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Intent intent = new Intent(activity, ImageSliderActivity.class);
                        intent.putExtra("imagePath", giftPojo.getImagePath());
                        intent.putExtra("imageId", giftPojo.getImageId());
                        activity.startActivity(intent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected)
    {
        try {
            if(isConnected)
            {
                if(isLoadingPending && !isLoading)
                {
                    loadImagesAsync(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
