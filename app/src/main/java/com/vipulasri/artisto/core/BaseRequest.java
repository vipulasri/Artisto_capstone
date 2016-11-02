package com.vipulasri.artisto.core;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * Created by HP-HP on 01-10-2016.
 */
public class BaseRequest<T> extends Request<T> {

    private GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");

    private Map<String, String> params;
    private Map<String, String> headers;
    private ResponseListener<T> listener;
    private TypeToken<T> typeToken;
    private Gson gson = gsonBuilder.create();

    public BaseRequest(int method, String url, Map<String, String> headers, Map<String, String> params, TypeToken<T> typeToken, ResponseListener<T> listener) {
        super(method, url, listener);
        this.headers = headers;
        this.params = params;
        this.typeToken = typeToken;
        this.listener = listener;

        setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonResponse = new String(networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers));
            T response = gson.fromJson(jsonResponse, typeToken.getType());
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void deliverResponse(T t) {
        if(listener != null) {
            listener.onResponse(t);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        //params.put("api_key", BuildConfig.MOVIEDB_API);
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
}
