package com.demo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.demo.classes.IOUtil;
import com.demo.classes.ImageLoadingUtils;
import com.demo.classes.ImagePath;
import com.demo.classes.ViewLargeImageActivity;
import com.demo.giphydemo.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuldeep Sakhiya on 06-10-2017.
 */

public class ImageUpload
{
    private Activity activity;
    private String giftImage = "";
    private Uri fileUri;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 100;
    private static final int SELECT_FILE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2;
    private static final int PICK_FROM_GALLERY_CROP = 3;

    public ImageUpload(Activity activity)
    {
        this.activity = activity;
    }

    private void checkStoragePermission()
    {
        try
        {
            int result;
            result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED)
            {
                openBottomSheetDialog();
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE_STORAGE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        openBottomSheetDialog();
                    }
                    else
                    {
                        AppUtils.showToast(activity, "Permissions Denied, You cannot access storage without it.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        try
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
                {
                    previewCapturedImage();
                }
                else if (requestCode == SELECT_FILE)
                {
                    Uri selectedImageUri = data.getData();
                    uploadImage(selectedImageUri);
                }
                else if (requestCode == PICK_FROM_GALLERY_CROP)
                {
                    try
                    {
                        giftImage = data.getStringExtra("single_path");

                        setGiftImage(false);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    private void openBottomSheetDialog()
    {
        try {
            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
            View sheetView = activity.getLayoutInflater().inflate(R.layout.calendar_bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

            View llView = sheetView.findViewById(R.id.llView);
            View llSelect = sheetView.findViewById(R.id.llSelect);
            View llCapture = sheetView.findViewById(R.id.llCapture);
            View llDelete = sheetView.findViewById(R.id.llDelete);
            View viewLineView = sheetView.findViewById(R.id.viewLineView);
            View viewLineDelete = sheetView.findViewById(R.id.viewLineDelete);

            if(giftImage != null && giftImage.length() > 0)
            {
                llView.setVisibility(View.VISIBLE);
                llDelete.setVisibility(View.VISIBLE);
                viewLineView.setVisibility(View.VISIBLE);
                viewLineDelete.setVisibility(View.VISIBLE);
            }
            else
            {
                llView.setVisibility(View.GONE);
                llDelete.setVisibility(View.GONE);
                viewLineView.setVisibility(View.GONE);
                viewLineDelete.setVisibility(View.GONE);
            }

            llView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        mBottomSheetDialog.dismiss();

                        Intent intent = new Intent(activity, ViewLargeImageActivity.class);
                        intent.putExtra("imagePath", giftImage);
                        intent.putExtra("isFromGift", true);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            llDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        mBottomSheetDialog.dismiss();

                        giftImage = "";

                        setGiftImage(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            llSelect.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        mBottomSheetDialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            llCapture.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        mBottomSheetDialog.dismiss();

                        File direct = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
                        if (!direct.exists())
                        {
                            direct.mkdirs();
                        }
                        String timestamp = new SimpleDateFormat("ddMMMyyyy-HHmmss").format(new Date());
                        File file = new File(Environment.getExternalStorageDirectory()+ IMAGE_DIRECTORY_NAME + "/IMG_" + timestamp+ ".jpg");

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        {
                            fileUri = FileProvider.getUriForFile(activity, "com.coronation.dentalapp.provider", file);
                            Log.e("URI" , fileUri.toString());
                            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList)
                            {
                                String packageName = resolveInfo.activityInfo.packageName;
                                activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                        }
                        else
                        {
                            fileUri = Uri.fromFile(file);
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        activity.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String IMAGE_DIRECTORY_NAME = "/GiphyDemo/Images/";
    private boolean imageSizePermitted = false;
    private void uploadImage(final Uri selectedImageUri)
    {
        String tempPath = "", name = "", type = "";
        Bitmap bm = null;
        try {
            Calendar cal = Calendar.getInstance();
            name = "IMG_"+cal.getTimeInMillis();

            Log.v("IMAGE PATH X", selectedImageUri.toString() + "**");

            if(selectedImageUri.toString().contains("com.google.android.apps.photos.contentprovider"))
            {
                if(selectedImageUri.toString().contains("com.google.android.apps.photos.contentprovider/0/1/mediaKey"))
                {
                    bm = generateBitmap(selectedImageUri);

                    type = ".png";

                    File dir =  new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
                    File f = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME + name + "." + type);

                    if(!dir.exists())
                    {
                        dir.mkdirs();
                    }

                    FileOutputStream fout = null;
                    try
                    {
                        fout = new FileOutputStream(f);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, fout); // bmp
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            if (fout != null)
                            {
                                fout.close();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    tempPath = f.getAbsolutePath();
                    Log.v("IMAGE PATH 1", tempPath + "**");

                    imageSizePermitted = checkFileSize(tempPath);
                }
                else
                {
                    tempPath = ImagePath.getPath(activity, selectedImageUri);

                    Log.v("IMAGE PATH 2", tempPath + "**");

                    imageSizePermitted = checkFileSize(tempPath);

                    if(imageSizePermitted)
                    {
                        //Getting the file extension type
                        String[] pathArray = tempPath.split("/");
                        String originalFileName = pathArray[pathArray.length - 1].replaceAll(" ", "-");
                        type = IOUtil.getExtension(IOUtil.getName(originalFileName));

                        bm = generateBitmap(tempPath, type);
                    }
                }
            }
            else
            {
                tempPath = ImagePath.getPath(activity, selectedImageUri);

                Log.v("IMAGE PATH 3", tempPath + "**");

                imageSizePermitted = checkFileSize(tempPath);

                if(imageSizePermitted)
                {
                    //Getting the file extension type
                    String[] pathArray = tempPath.split("/");
                    String originalFileName = pathArray[pathArray.length - 1].replaceAll(" ", "-");
                    type = IOUtil.getExtension(IOUtil.getName(originalFileName));

                    bm = generateBitmap(tempPath, type);
                }
            }

            if(imageSizePermitted)
            {
                if (bm != null)
                {
                    File directory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
                    File file = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME + name + "."+type);

                    if(!directory.exists())
                    {
                        directory.mkdirs();
                    }

                    if(bm.getWidth() > 1280 || bm.getHeight() > 960)
                    {
                        try
                        {
                            compressImage(tempPath, file);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        giftImage = file.getAbsolutePath();
                        setGiftImage(true);
                        /*setImageInList(file, type, name);*/
                        Log.e("calendar file upload", "path : " + file.getAbsolutePath());
                    }
                    else
                    {
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos); //bm is the bitmap object

                        giftImage = file.getAbsolutePath();
                        setGiftImage(true);
                        /*setImageInList(file, type, name);*/
                        Log.e("calendar file upload", "path : " + file.getAbsolutePath());
                    }
                }
                bm = null;
            }
            else
            {
                AppUtils.showToast(activity, "Image greater than 10 MB cannot be uploaded.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGiftImage(final boolean isForCrop)
    {
        /*try {
            if(giftImage.length() > 0)
            {
                Glide.with(activity)
                        .load(giftImage)
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.ic_gallery)
                        .centerCrop()
                        .dontAnimate()
                        .into(ivGift);
            }
            else
            {
                Glide.with(activity)
                        .load(R.drawable.ic_gallery)
                        .centerCrop()
                        .dontAnimate()
                        .into(ivGift);
            }

            if(isForCrop)
            {
                Intent i = new Intent(activity, CropActivity.class);
                i.putExtra("imagePath", giftImage);
                i.putExtra("outputSize", 800);
                activity.startActivityForResult(i, PICK_FROM_GALLERY_CROP);
                activity.overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void previewCapturedImage()
    {
        try
        {
            String[] pathArray = fileUri.getPath().split("/");
            final String originalFileName = pathArray[pathArray.length - 1].replaceAll(" ", "-");
            final String type = IOUtil.getExtension(IOUtil.getName(originalFileName));
            final String[] pathArrayName = originalFileName.split("\\.");
            final String name = pathArrayName[0];

            //new CompressImage(activity, fileUri.getPath(), name);
            File directory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME);
            File file = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY_NAME + name + "." + type);
            if(!directory.exists())
            {
                directory.mkdirs();
            }

            try
            {
                String path = fileUri.getPath().replace("external_storage_root", directory.getAbsolutePath());
                Log.v("path to store", path + "*");
                compressImage(path, file);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            giftImage = file.getAbsolutePath();
            setGiftImage(true);
            /*setImageInList(file, type, name);*/
            Log.e("calendar file upload", "path : " + file.getAbsolutePath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Compress Image
    ImageLoadingUtils utils;
    @SuppressWarnings("deprecation")
    public void compressImage(String filePath, File f)
    {
        try
        {
            utils = new ImageLoadingUtils(activity);
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            int actualHeight = 0;
            int actualWidth = 0;
            if(options.outWidth > 0 || options.outHeight > 0)
            {
                actualHeight = options.outHeight;
                actualWidth = options.outWidth;
            }
            else
            {
                if(bmp.getHeight() > bmp.getWidth())
                {
                    actualHeight = 1280;
                    actualWidth = 960;
                }
                else if(bmp.getHeight() < bmp.getWidth())
                {
                    actualHeight = 960;
                    actualWidth = 1280;
                }
                else if(bmp.getHeight() == bmp.getWidth())
                {
                    actualHeight = 800;
                    actualWidth = 800;
                }
            }
            float maxHeight = 1286.0f;
            float maxWidth = 972.0f;
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
            try
            {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
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


            ExifInterface exif;
            try
            {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6)
                {
                    matrix.postRotate(90);
                }
                else if (orientation == 3)
                {
                    matrix.postRotate(180);
                }
                else if (orientation == 8)
                {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FileOutputStream out = null;

            //String timestamp = new java.text.SimpleDateFormat("ddMMMyyyy-HHmmss").format(new Date());
            try
            {
                out = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean checkFileSize(String filePath)
    {
        boolean status = false;
        File sizeF;
        try
        {
            status = false;

            sizeF = new File(filePath);
            long file_size = sizeF.length();

            //Here 10485760 bytes means 10mb
            if(file_size < 10485760)
            {
                status = true;
            }
            else
            {
                status = false;
            }
        }
        catch (Exception e)
        {
            status = false;
            e.printStackTrace();
        }
        finally
        {
            sizeF = null;
        }

        return status;
    }

    public Bitmap generateBitmap(Uri uri)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(activity.getContentResolver().openInputStream(uri)));
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();

            File file = new File(uri.toString());
            InputStream is = null;
            try
            {
                is = new FileInputStream(file);
            }
            catch (FileNotFoundException e1)
            {
                e1.printStackTrace();
            }

            if(is != null)
            {
                bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap generateBitmap(String filePath, String fileType)
    {
        Bitmap bitmap = null;
        try
        {
            try
            {
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                if(fileType.replace(".", "").equalsIgnoreCase("jpg")
                        || fileType.replace(".", "").equalsIgnoreCase("jpeg"))
                {
                    btmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                }

                bitmap = BitmapFactory.decodeFile(filePath, btmapOptions);
            }
            catch (OutOfMemoryError e)
            {
                e.printStackTrace();

                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                btmapOptions.inSampleSize = 2;
                btmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

                bitmap = BitmapFactory.decodeFile(filePath, btmapOptions);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    static class FlushedInputStream extends FilterInputStream
    {
        public FlushedInputStream(InputStream inputStream)
        {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException
        {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bytesRead = read();
                    if (bytesRead < 0) {
                        break;
                    } else {
                        bytesSkipped = 1;
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    private void updateProfileImageAsync()
    {
        try
        {
            new AsyncTask<Void, Void, Void>()
            {
                private boolean isSuccess = false;
                private String message = "";

                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... params)
                {
                    /*try
                    {
                        String URL = AppAPIUtils.ACCOUNT_UPDATE_PROFILE_PICTURE;

                        HashMap<String, String> hashMap = new HashMap<String , String>();

                        hashMap.put("TokenId", AppAPIUtils.TOKEN_ID);
                        hashMap.put("UserId", sessionManager.getUserId());

                        Log.v("UPDATE IMAGE Params", hashMap + "**");

                        if(sessionManager.getProfileImage() != null && sessionManager.getProfileImage().length() > 0)
                        {
                            byte[] byteArray = null;
                            final File fl = new File(sessionManager.getProfileImage());
                            byteArray = IOUtil.readFile(fl);

                            String stringToStore = Base64.encodeToString(byteArray,Base64.DEFAULT);
                            byteArray = null;

                            String[] nameArr = fl.getAbsolutePath().split("/");
                            String fileName = nameArr[nameArr.length - 1];
                            String fileExtension = "." + IOUtil.getExtension(fileName);

                            hashMap.put("strBufferProfileImage", stringToStore);
                            hashMap.put("ImageExtension", fileExtension);
                        }
                        else
                        {
                            hashMap.put("strBufferProfileImage", "");
                            hashMap.put("ImageExtension", "");
                        }

                        String response = MitsUtils.readJSONServiceUsingPOST(URL, hashMap);

                        Log.v("UPDATE IMAGE RESPONSE", response + "**");

                        JSONObject jsonObject = new JSONObject(response);
                        String status = AppUtils.getValidAPIStringResponse(jsonObject.getString("status"));
                        message = AppUtils.getValidAPIStringResponse(jsonObject.getString("msg"));

                        if(status.equals("1"))
                        {
                            isSuccess = true;
                        }
                        else
                        {
                            isSuccess = false;
                            return null;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }*/
                    return null;
                }

                @Override
                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);

                    try
                    {
                        if(isSuccess)
                        {
                        }
                        else
                        {
                            if(message != null && message.trim().length() > 0)
                            {
                                AppUtils.showToast(activity, message);
                            }
                            else
                            {
                                AppUtils.showToast(activity, "Failed to upload image!");
                            }
                        }
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
