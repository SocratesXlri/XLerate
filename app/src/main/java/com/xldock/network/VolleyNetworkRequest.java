package com.xldock.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by honey on 10/10/17.
 */

public class VolleyNetworkRequest<T> extends Request<T> {

    public static final String TAG = "--MultipartRequest--";
    private static final int DEFAULT_TIMEOUT_MS = 20000;
    private static final int DEFAULT_MAX_RETRIES = 0;

    private final MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();

    private final Response.Listener<T> mResponseListener;
    private final Map<String, String> mHeaderData;
    private final Map<String, String> mBodyData;
    private final HashMap<String, File> mFilePart;

    public VolleyNetworkRequest(String url,
                                Response.ErrorListener errorListener,
                                Response.Listener<T> listener,
                                Map<String, String> headers,
                                Map<String, String> stringData,
                                HashMap<String, File> fileParam) {
        super(Method.POST, url, errorListener);

        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mResponseListener = listener;
        mHeaderData = headers;
//        try {
//            for (Map.Entry<String, String> entry : mHeaderData.entrySet()) {
//                Log.d(TAG, "VolleyNetworkRequest: " + entry.getKey() + " -- " + entry.getValue());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "buildMultipartEntity: =" + e.getMessage() + "\n" + e);
//        }
        mBodyData = stringData;
        mFilePart = fileParam;
        buildMultipartEntity();
    }

    public VolleyNetworkRequest(String url,
                                Response.ErrorListener errorListener,
                                Response.Listener<T> listener,
                                Map<String, String> headers,
                                Map<String, String> stringData,
                                HashMap<String, File> fileParam,
                                int method) {
        super(method, url, errorListener);

        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mResponseListener = listener;
        mHeaderData = headers;
        mBodyData = stringData;
        mFilePart = fileParam;
        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaderData == null) {
            return new HashMap<>();
        }
        return mHeaderData;
    }

    private void buildMultipartEntity() {
        if (null != mFilePart) {
            for (Map.Entry<String, File> entry : mFilePart.entrySet()) {
                mBuilder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.create("image/jpeg"), entry.getValue().getName());
            }
        }

        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));

        if (null != mBodyData) {
            try {
                for (Map.Entry<String, String> entry : mBodyData.entrySet()) {
                    Log.d(TAG, "buildMultipartEntity() called :: " + entry.getKey() + " -- " + entry.getValue());
                    //entity.addPart(postEntityModel.getName(), new StringBody(postEntityModel.getValue(), ContentType.TEXT_PLAIN));
                    mBuilder.addTextBody(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                Log.e(TAG, "buildMultipartEntity: =" + e.getMessage() + "\n" + e);
            }
        }

    }

    @Override
    public String getBodyContentType() {
        return mBuilder.build().getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        return bos.toByteArray();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.e(TAG, "parseNetworkResponse: =" + json);
            return (Response<T>) Response.success(json, HttpHeaderParser.parseCacheHeaders(response)); // it will return String
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mResponseListener.onResponse(response);
    }
}
