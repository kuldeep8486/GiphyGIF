package com.demo.classes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.giphydemo.R;

import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity
{
    private Activity activity;

    private ViewPager viewPager;
    private TextView txtCount;

    private ArrayList<String> listImages = new ArrayList<>();
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_image_slider);

        activity = ImageSliderActivity.this;

        listImages = getIntent().getStringArrayListExtra("list");
        selectedPosition = getIntent().getIntExtra("selectedPosition", 0);

        setUpViews();

        setupClickEvents();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpViews()
    {
        try {
            txtCount = (TextView) findViewById(R.id.txtCount);

            viewPager = (ViewPager) findViewById(R.id.viewpager);

            viewPager.setAdapter(new ViewPagerAdapter(listImages));
            viewPager.setOffscreenPageLimit(3);
            viewPager.setCurrentItem(selectedPosition);

            if(listImages.size() > 1)
            {
                txtCount.setVisibility(View.VISIBLE);
            }
            else
            {
                txtCount.setVisibility(View.GONE);
            }

            setTextCount(selectedPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextCount(int position)
    {
        txtCount.setText((position + 1) + " OF " + listImages.size());
    }

    private void setupClickEvents()
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position)
            {
                setTextCount(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewPagerAdapter extends PagerAdapter
    {
        private ArrayList<String> listImage;

        private ViewPagerAdapter(ArrayList<String> icon)
        {
            this.listImage = icon;
        }

        @Override
        public int getCount()
        {
            return listImage.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
            View itemView = getLayoutInflater().inflate(R.layout.activity_view_image, container, false);

            TouchImageView iconView = (TouchImageView) itemView.findViewById(R.id.imgFull);

            try {
                String imagepath = listImage.get(position);
                if (imagepath.length() > 0)
                {
                    Glide.with(activity)
                        .load(imagepath)
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.ic_gallery)
                        .crossFade()
                        .into(iconView);
                }
                else
                {
                    iconView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            try {
                activity.finish();
                activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
