package com.demo.classes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.bumptech.glide.Glide;
import com.demo.giphydemo.R;
import com.demo.utils.AppUtils;

import java.io.File;

public class ViewLargeImageActivity extends AppCompatActivity
{
	private Activity activity;
	private TouchImageView imgFull;
    private String imagepath = "";
    private boolean isFromProfile = false, isFromSelfie = false, isFromGift = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try {
			super.onCreate(savedInstanceState);
		} catch (Exception e) {
			e.printStackTrace();
		}

		activity = this;

		setContentView(R.layout.activity_view_image);

		isFromSelfie = getIntent().getBooleanExtra("isFromSelfie", false);
		isFromGift = getIntent().getBooleanExtra("isFromGift", false);
		imagepath = AppUtils.getValidAPIStringResponse(getIntent().getStringExtra("imagePath"));
		if(imagepath.startsWith("http"))
		{
			isFromProfile = false;
		}
		else
		{
			isFromProfile = true;
		}
		
		setupViews();
		
		try {
			File file = null;
			if(isFromProfile)
			{
				file = new File(imagepath);
				imagepath = file.getAbsolutePath();
			}

			int resId = R.drawable.ic_gallery;
			/*if(isFromSelfie)
			{
				resId = R.drawable.icon_brushing;
			}
			else if(isFromGift)
			{
				resId = R.drawable.icon_gift_broad;
			}*/

			if(imagepath != null && imagepath.length() > 0)
			{
				System.out.println(imagepath + " ");

				Glide.with(activity)
					.load(imagepath)
					.placeholder(resId)
					.crossFade(500)
					.error(resId)
					.into(imgFull);
			}
			else
			{
				imgFull.setImageResource(resId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			super.onRestoreInstanceState(savedInstanceState);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void setupViews()
	{
		imgFull = (TouchImageView) findViewById(R.id.imgFull);
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