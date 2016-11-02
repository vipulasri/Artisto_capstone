package com.vipulasri.artisto.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by HP-HP on 18-10-2016.
 */

public abstract class ImageSaveAsync extends AsyncTask<String, Void, String> {

    private Context context;

    protected ImageSaveAsync(Context context) {
        this.context=context;
    }

    protected abstract void onPreExecute();

    @Override
    protected String doInBackground(String... strings) {
        if(strings.length == 0 || strings[0] == null)
            return null;

        return saveImage(strings[0]);
    }

    protected abstract void onPostExecute(String message) ;

    private String saveImage(String url) {

        String extension = ".jpeg";

        Bitmap bitmap = createBitmap(url);

        if(bitmap==null) {
            return "Image Not Loaded";
        }

        File pictureFile = getOutputMediaFile(context, extension, "save");
        if (pictureFile == null) {
            Log.d("", "Error creating media file, check storage permissions: ");// e.getMessage());
            return "Error creating media file, check storage permissions: ";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
            fos.flush();
            fos.close();

            bitmap.recycle();

            // in order to show images in gallery it should have some meta data to be displayed
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, pictureFile.getName().toLowerCase(Locale.US));
            values.put(MediaStore.Images.Media.DESCRIPTION, "");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, pictureFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, pictureFile.getName().toLowerCase(Locale.US));
            values.put("_data", pictureFile.getAbsolutePath());

            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return "Image Saved";

        } catch (FileNotFoundException e) {

            Log.d("", "File not found: " + e.getMessage());
            return "File not found: " + e.getMessage();

        } catch (IOException e) {

            Log.d("", "Error accessing file: " + e.getMessage());
            return "Error accessing file: " + e.getMessage();
        }

    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(Context context, String extension, String type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir;

        if(type.equals("save")) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Pictures/Artisto");
        }
        else {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + context.getApplicationContext().getPackageName()
                    + "/cache");
        }

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date());
        File mediaFile;

        String mImageName = "IMG_" + timeStamp + extension;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        //path = mediaStorageDir.getPath() + File.separator + mImageName;

        return mediaFile;
    }


    private static File getCacheImage(String url) {
        ImageRequest imageRequest=ImageRequest.fromUri(url);

        CacheKey cacheKey= DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, null);

        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainDiskStorageCache().getResource(cacheKey);

        File file= null;
        try {
            file = ((FileBinaryResource)resource).getFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Bitmap createBitmap(String url) {
        File file_cacheimage = getCacheImage(url);

        if (file_cacheimage==null) {
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inSampleSize = 1;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inDither = false;
        bmOptions.inPurgeable = true;
        bmOptions.inInputShareable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file_cacheimage.getAbsolutePath(), bmOptions);

        return bitmap!=null ? Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true) : null;
    }
}

