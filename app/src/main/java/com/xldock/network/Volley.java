package com.xldock.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by honey on 10/10/17.
 */

public class Volley {

    private static final String TAG = "Volley";
    private static Volley mInstance;
    private RequestQueue mRequestQueue;

    private Volley(Context context) {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network, 10);
            mRequestQueue.start();
        }
    }

    public static synchronized Volley getInstance(Context context) {
        if (mInstance == null)
            mInstance = new Volley(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        mRequestQueue.add(request);
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        //set default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        mRequestQueue.add(request);
    }
}
