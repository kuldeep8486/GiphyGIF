package com.demo.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.giphydemo.R;
import com.demo.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CropActivity extends AppCompatActivity
{
	private Activity activity;
	
	private CropImageView mCropView;
	private ImageView result_image;
	private TextView txtCrop;
	
	private String imagePath = "";
	private boolean isForSave = false, isForLandscape = false;
	
	private Bitmap cropped = null;
	private int outputSize = 400;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try {
			super.onCreate(savedInstanceState);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setContentView(R.layout.activity_crop_main);
		
		activity = this;
		isForLandscape = getIntent().getBooleanExtra("isForLandscape", false);
		imagePath = AppUtils.getValidAPIStringResponse(getIntent().getStringExtra("imagePath"));
		outputSize = getIntent().getIntExtra("outputSize", 400);
		File file = new File(imagePath);
        
		initView();
		
		setupToolbar();
		
		onclickEvents();

		if(file == null || !file.exists())
		{
			/*AppUtils.showToast(activity, "File does not exists!");*/
			activity.finish();
			activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        }
		else
		{
			setCropImage(file.getAbsolutePath());
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
	
	private void setCropImage(final String path)
	{
		mCropView.setImageBitmap(BitmapFactory.decodeFile(path));
		if(!isForLandscape)
		{
			mCropView.setCropMode(CropImageView.CropMode.RATIO_1_1);
		}
		else
		{
			mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
		}
	}

	private void initView() 
	{
		mCropView = (CropImageView) findViewById(R.id.cropImageView);
		result_image = (ImageView) findViewById(R.id.result_image);
		
		txtCrop = (TextView) findViewById(R.id.txtCrop);
    }
	
	protected void setupToolbar() 
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        
        final TextView txtTitle = (TextView) toolbar.findViewById(R.id.txtTitle);
        txtTitle.setText("Crop Image");

		final View llBack = toolbar.findViewById(R.id.llBack);
		llBack.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					activity.finish();
					activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		final View llUndo = toolbar.findViewById(R.id.llUndo);
		llUndo.setVisibility(View.VISIBLE);
		llUndo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					AlertDialog builder = new AlertDialog.Builder(activity)
							.setTitle("Undo")
							.setMessage("Are you sure you want to revert all changes?")
							.setPositiveButton("Undo", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int which)
								{
									try {
										dialog.dismiss();
										dialog.cancel();

										isForSave = false;
										cropped = null;
										mCropView.setVisibility(View.VISIBLE);
										txtCrop.setText("CROP");
										result_image.setVisibility(View.GONE);

										setCropImage(imagePath);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int which)
								{
									try {
										dialog.dismiss();
										dialog.cancel();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							})
							.create();
					builder.show();

					TextView textView = (TextView) builder.findViewById(android.R.id.message);
					textView.setTypeface(AppUtils.getTypefaceRegular(activity));

					Button button = builder.getButton(DialogInterface.BUTTON_POSITIVE);
					button.setTypeface(AppUtils.getTypefaceButton(activity));

					Button button1 = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
					button1.setTypeface(AppUtils.getTypefaceButton(activity));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private void onclickEvents()
	{
		txtCrop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!isForSave)
				{
					isForSave = true;
					cropped = mCropView.getCroppedBitmap();
					mCropView.setVisibility(View.GONE);
					txtCrop.setText("DONE");
					result_image.setVisibility(View.VISIBLE);
					result_image.setImageBitmap(cropped);
				}
				else
				{
					cropAsync();
				}
			}
		});
	}

	private static String IMAGE_DIRECTORY_NAME = "/GiphyDemo/Images/";
	private void cropAsync() 
	{
		(new AsyncTask<Void, Void, Void>()
		{
			private ProgressDialog pd;
			private String path = "";
			
			@Override
			protected void onPreExecute() 
			{
				try 
				{
					pd = new ProgressDialog(activity);
					pd.setCancelable(true);
					pd.setCanceledOnTouchOutside(false);
					pd.setMessage("Loading...");
	                pd.show();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					File tostoreFile = null;
					FileOutputStream out = null;
					try 
					{
						File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
						if(!dirtostoreFile.exists())
						{
							dirtostoreFile.mkdirs();
						}
						String timestr = AppUtils.convertDateToString(Calendar.getInstance().getTimeInMillis());
						tostoreFile = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME + "/IMG_"+timestr+".jpg");
						
					    out = new FileOutputStream(tostoreFile.getAbsolutePath());
					    cropped.compress(Bitmap.CompressFormat.JPEG, 80, out);
					}
					catch (Exception e)
					{
					    e.printStackTrace();
					}
					finally 
					{
					    try 
					    {
					        if (out != null) {
					            out.close();
					        }
					    }
					    catch (IOException e)
					    {
					        e.printStackTrace();
					    }
					}
					
					File file = ImagePath.cropAndCompressImage(activity, tostoreFile.getAbsolutePath(), outputSize, (float) outputSize);

					if(file != null && file.exists())
					{
						path = file.getAbsolutePath();
						System.out.println(file.getAbsolutePath()+"");
					}
					else
					{
						path = "";
						System.out.println("error saving file!!");
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
				
				try 
				{
					if(path != null && path.trim().length() > 0)
					{
						try 
						{
							if(pd != null)
							{
								pd.dismiss();
								pd.cancel();
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						
						Intent intentData = new Intent().putExtra("single_path", path);
						setResult(RESULT_OK, intentData);
						finish();
					}
					else
					{
						AppUtils.showSnackBar(activity, mCropView, "Failed to save!");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);;
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