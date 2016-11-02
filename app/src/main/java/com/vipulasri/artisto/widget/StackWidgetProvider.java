package com.vipulasri.artisto.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.vipulasri.artisto.R;
import com.vipulasri.artisto.activity.arts_details.ArtsDetailsActivity;
import com.vipulasri.artisto.model.Artwork;
import com.vipulasri.artisto.utils.ParcelableUtil;

public class StackWidgetProvider extends AppWidgetProvider {

    public static final String INTENT_ACTION = "com.vipulasri.artisto.widget.INTENT_ACTION";
    public static final String EXTRA_ART = "com.vipulasri.artisto.widget.EXTRA_ART";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(INTENT_ACTION)){

            byte[] bytes = intent.getByteArrayExtra(EXTRA_ART);
            Artwork artwork = ParcelableUtil.unmarshall(bytes, Artwork.CREATOR);

            Log.e("Intent","->"+artwork.getTitle());
            Intent showArtDetail = new Intent(context, ArtsDetailsActivity.class);
            showArtDetail.putExtra(ArtsDetailsActivity.TAG_ARTWORK, artwork);
            showArtDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(showArtDetail);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            Intent artIntent = new Intent(context, StackWidgetProvider.class);
            artIntent.setAction(StackWidgetProvider.INTENT_ACTION);
            artIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, artIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}