/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vipulasri.artisto.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.vipulasri.artisto.R;
import com.vipulasri.artisto.database.ArtsContract;
import com.vipulasri.artisto.model.Artwork;
import com.vipulasri.artisto.utils.ParcelableUtil;

import java.io.File;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

        cursor = mContext.getContentResolver().query(
                ArtsContract.ArtsEntry.CONTENT_URI,null,
                null,
                null,
                null);
    }

    @Override
    public void onDataSetChanged() {

        cursor = mContext.getContentResolver().query(
                ArtsContract.ArtsEntry.CONTENT_URI,null,
                null,
                null,
                null);
    }

    @Override
    public void onDestroy() {
        if (this.cursor != null)
            this.cursor.close();
    }

    @Override
    public int getCount() {
        return (this.cursor != null) ? this.cursor.getCount() : 0;
    }

    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        Log.e("count","->"+getCount());

        Artwork artwork = getArtsFromCursorPosition(position);

        if(artwork!=null) {

            Bitmap bitmapArt = createBitmap(artwork.getThumbnailImage());

            if(bitmapArt!=null) {
                remoteViews.setImageViewBitmap(R.id.widgetArtImage, bitmapArt);
            }

            Log.e("Art Title","->"+artwork.getTitle());

            remoteViews.setTextViewText(R.id.widgetArtTitle, artwork.getTitle());

            Bundle extras = new Bundle();
            byte[] bytes = ParcelableUtil.marshall(artwork);
            extras.putByteArray(StackWidgetProvider.EXTRA_ART, bytes);

            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            remoteViews.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        }


        // Return the remote views object.
        return remoteViews;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    private Artwork getArtsFromCursorPosition(int position) {

        Artwork artwork = null;

        if(cursor.moveToPosition(position)) {

            String art_web_link = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_LINK));
            String art_id = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_ID));
            String art_object_number = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_OBJECT_NUMBER));
            String art_title = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_TITLE));
            boolean art_has_image = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_HAS_IMAGE))==1;
            String art_maker = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_MAKER));
            String art_long_title = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_lONG_TITLE));
            boolean art_show_image = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_SHOW_IMAGE))==1;
            boolean art_permit_download = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_PERMIT_DOWNLOAD))==1;
            String art_web_image = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE));
            String art_web_image_height = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE_HEIGHT));
            String art_web_image_width = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE_WIDTH));

            Artwork.Links links = new Artwork.Links("",art_web_link);
            Artwork.WebImage webImage = new Artwork.WebImage("", 0L, 0L, Long.valueOf(art_web_image_width), Long.valueOf(art_web_image_height), art_web_image);

            artwork = new Artwork(links, art_id, art_object_number, art_title, art_has_image, art_maker, art_long_title
                    , art_show_image, art_permit_download,webImage, null);

            artwork.setFavorite(true); //setting it favorite as the artwork in database will always be favorited ones.
        }


        return artwork;
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

}