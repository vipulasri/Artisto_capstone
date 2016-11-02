package com.vipulasri.artisto;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.vipulasri.artisto.core.OkHttpStack;

/**
 * Created by HP-HP on 01-10-2016.
 */
public class ArtistoApplication extends Application {

    public static final String TAG = ArtistoApplication.class.getSimpleName();

    private static ArtistoApplication _instance;
    private RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        _instance = this;
    }

    public static ArtistoApplication getInstance() {
        return _instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
}
