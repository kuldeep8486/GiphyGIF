package com.demo.classes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.demo.giphydemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ImagePath 
{
	public static String kitkatPath;
	Activity activity;
	
	public ImagePath(Activity activity)
	{
		this.activity = activity;
	}
	
	public static Bitmap fastblur(Bitmap sentBitmap, int radius)
	{
        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) 
        {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) 
            {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) 
                {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                }
                else 
                {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) 
            {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
	
	public static Bitmap getCroppedBitmap(Bitmap bitmap)
	{
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() /3, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
    }
	
	@TargetApi(19)
	public static String getPath(final Context context, final Uri uri)
	{
	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
	    {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) 
	        {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) 
	            {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }
	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) 
	        {
	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) 
	        {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            }
	            else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            }
	            else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }
	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
	{
	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = { column };

	    try 
	    {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
	        if (cursor != null && cursor.moveToFirst()) 
	        {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } 
	    finally 
	    {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	
	public String getRealPathFromURI(Uri contentUri)
	{
		//TODO: get realpath from uri
		String stringPath = null;
		try
		{
			if (contentUri.getScheme().toString().compareTo("content")==0)
			{      
				String[] proj = { MediaStore.Images.Media.DATA };
				CursorLoader loader = new CursorLoader(activity, contentUri, proj, null, null, null);
				Cursor cursor = loader.loadInBackground();
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				stringPath = cursor.getString(column_index);
				cursor.close();
			}
			else if (contentUri.getScheme().compareTo("file")==0)
			{
				stringPath = contentUri.getPath();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return stringPath;
	}
	
	public static Bitmap lessResolution(String filePath)
	{
		//TODO: compress bitmap
	    int reqHeight = 150;
	    int reqWidth = 100;
	    BitmapFactory.Options options = new BitmapFactory.Options();

	    // First decode with inJustDecodeBounds=true to check dimensions
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static Bitmap lessResolution(String filePath, int h , int w)
	{
		//TODO: compress bitmap
	    int reqHeight = h;
	    int reqWidth = w;
	    BitmapFactory.Options options = new BitmapFactory.Options();

	    // First decode with inJustDecodeBounds=true to check dimensions
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		//TODO: calculate bitmap size
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 4;

	    if (height > reqHeight || width > reqWidth) 
	    {
	        // Calculate ratios of height and width to requested height and
	        // width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will
	        // guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeFile(String file, int size)
	{
	    //Decode image size
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file, o);

	    //Find the correct scale value. It should be the power of 2.
	    int width_tmp = o.outWidth, height_tmp = o.outHeight;
	    int scale = 1;
	    scale = (int) Math.pow(2, (double)(scale-1));
	    while (true) 
	    {
	        if (width_tmp / 2 < size || height_tmp / 2 < size) 
	        {
	            break;
	        }
	        width_tmp /= 2;
	        height_tmp /= 2;
	        scale++;
	    }

	    //Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    return BitmapFactory.decodeFile(file, o2);
	}
	
	public static String getDataToView(String path, String name)
	{
		byte[] data = null;
		boolean isException = false;
		try
	    {
			File f = new File(path);
//	        data = org.apache.commons.io.FileUtils.readFileToByteArray(f); //Convert any file, image or video into byte array
	        data = IOUtil.readFile(f);
	        Log.v("file size", data.length+"");
//		    String strFile = Base64.encodeToString(data, Base64.NO_WRAP); //Convert byte array into string
	    } 
	    catch (IOException e)
	    {
	    	isException = true;
        	e.printStackTrace();
	    }
		
	    File direct = new File(Environment.getExternalStorageDirectory()+ "/Android/data/com.coronation.dentalapp/tempdata/"+name);
	    if(!direct.getParentFile().exists()) 
	    {
	    	Log.v("exists?", "file directory not exists");
	    	direct.getParentFile().mkdirs(); 
	    }
	    else
	    {
	    	Log.v("exists?", "file directory exists");
	    	deleteDir(direct.getParentFile());
	    	direct.getParentFile().mkdirs();
	    }
	    
		if(!isException)
		{
			try 
			{
				FileOutputStream fos = new FileOutputStream(direct);
				try
				{
					Log.v("data size", data.length+"");
					fos.write(data);
					fos.close();
					data = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			return direct.getAbsolutePath();
		}
		else
		{
			return "";
		}
	}
	
	@SuppressWarnings("deprecation")
	public static File compressImage(final Activity activity, final String filePath)
	{
		ImageLoadingUtils utils = new ImageLoadingUtils(activity);
		Bitmap scaledBitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
		
		if(bmp == null)
		{
			bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_gallery);
		}
		
		int actualHeight = 0;
		int actualWidth = 0;
		if(options.outWidth > 0 || options.outHeight > 0)
		{
			actualHeight = options.outHeight;
			actualWidth = options.outWidth;
		}
		else{
			actualHeight = 1920;
			actualWidth = 1080;
		}
//		1080 x 1920
//		1440 x 2560
		float maxHeight = 1920.0f;
		float maxWidth = 1080.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) 
		{
			if (imgRatio < maxRatio) 
			{
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			}
			else if (imgRatio > maxRatio) 
			{
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			}
			else
			{
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;     
			}
		}
				
		options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16*1024];
		
		try
		{
			bmp = BitmapFactory.decodeFile(filePath,options);
		}
		catch(OutOfMemoryError exception)
		{
			exception.printStackTrace();
		}
		
		if(bmp == null)
		{
			bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_gallery);
		}
		
		try
		{
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
		}
		catch(OutOfMemoryError exception)
		{
			exception.printStackTrace();
		}
						
		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float)options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;
			
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		File tostoreFile = null;
		ExifInterface exif;
		try 
		{
			exif = new ExifInterface(filePath);
		
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		
			File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/DentalApp/Images/");
			if(!dirtostoreFile.exists())
			{
				dirtostoreFile.mkdirs();
			}
			
			FileOutputStream out = null;
			String timestr = getCurrentDateString(Calendar.getInstance().getTimeInMillis());
			tostoreFile = new File(Environment.getExternalStorageDirectory() + "/DentalApp/Images/IMG_"+timestr+".jpg");
			out = new FileOutputStream(tostoreFile.getAbsolutePath());
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
			
			return tostoreFile;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static File cropAndCompressImage(final Activity activity, final String filePath, final int height, final float heightF)
	{
		ImageLoadingUtils utils = new ImageLoadingUtils(activity);
		Bitmap scaledBitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
		
		if(bmp == null)
		{
			bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_gallery);
		}
		
		int actualHeight = 0;
		int actualWidth = 0;
		if(options.outWidth > 0 || options.outHeight > 0)
		{
			actualHeight = options.outHeight;
			actualWidth = options.outWidth;
		}
		else
		{
			if(height == 0)
			{
				actualHeight = 400;
				actualWidth = 400;
			}
			else
			{
				actualHeight = height;
				actualWidth = height;
			}
		}
		float maxHeight = 0.0f;
		float maxWidth = 0.0f;
		
		if(heightF == 0.0)
		{
			maxHeight = 400.0f;
			maxWidth = 400.0f;
		}
		else
		{
			maxHeight = heightF;
			maxWidth = heightF;
		}
		
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) 
		{
			if (imgRatio < maxRatio) 
			{
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			}
			else if (imgRatio > maxRatio) 
			{
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			}
			else
			{
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;     
			}
		}
				
		options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16*1024];
		
		try
		{
			bmp = BitmapFactory.decodeFile(filePath,options);
		}
		catch(OutOfMemoryError exception)
		{
			exception.printStackTrace();
		}
		
		if(bmp == null)
		{
			bmp = BitmapFactory.decodeResource(activity.getResources(),R.drawable.ic_gallery);
		}
		
		try
		{
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
		}
		catch(OutOfMemoryError exception)
		{
			exception.printStackTrace();
		}
						
		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float)options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;
			
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		File tostoreFile = null;
		ExifInterface exif;
		try 
		{
			exif = new ExifInterface(filePath);
		
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		
			File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/DentalApp/Images/Cropped/");
			if(!dirtostoreFile.exists())
			{
				dirtostoreFile.mkdirs();
			}
			
			FileOutputStream out = null;
			String timestr = getCurrentDateString(Calendar.getInstance().getTimeInMillis());
			tostoreFile = new File(Environment.getExternalStorageDirectory() + "/DentalApp/Images/Cropped/IMG_"+timestr+".jpg");
			out = new FileOutputStream(tostoreFile.getAbsolutePath());
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			
			return tostoreFile;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateString(final long l)
	{
		String str = "";
		Date date = new Date(l);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		str = dateFormat.format(date);
		return str;
	}
	
	public static void deleteDir(File dir)
	{
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++)
            {
            	new File(dir, children[i]).delete();
            }
        }
        // The directory is now empty so delete it
        dir.delete();
    }
}