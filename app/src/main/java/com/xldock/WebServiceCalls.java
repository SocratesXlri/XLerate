package com.xldock;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xldock.network.NetworkUtility;
import com.xldock.network.Volley;
import com.xldock.network.VolleyNetworkRequest;
import com.xldock.utils.PreferenceUtility;
import com.xldock.model.Generic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by honey on 10/10/17.
 */

public class WebServiceCalls {

    public interface WebServiceResponse {

        void volleyError();

        <T> void volleySuccessResponse(Generic<T> response, String from);

        void volleyDisplayGeneralizedMessage();

        void volleyDisplaySpecifiedMessage(String message);
    }

    private String TAG;
    private WebServiceResponse listener;
    private PreferenceUtility preferenceUtility;

    public WebServiceCalls(WebServiceResponse webServiceResponseListener) {
        listener = webServiceResponseListener;
    }


    /**
     * call Login web service
     *
     * @param context
     */
    public void callLoginWebService(String userId, String password, Context context) {

        TAG = "login ws:";
        preferenceUtility = PreferenceUtility.getInstance(context);

        final Response.Listener mCallLoginWSResponseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {

                String stringResponse = (String) response;
                Generic<String> generic=new Generic<>();

                try {
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    String loginResponse = jsonObject.getString(NetworkUtility.TAGS.response);
                    generic.set(loginResponse);

                    listener.volleySuccessResponse(generic,NetworkUtility.URLS.LOGIN);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };


        //body params
        Map<String, String> mBodyParams = new HashMap<>();
        mBodyParams.put(NetworkUtility.TAGS.uid, userId);
        mBodyParams.put(NetworkUtility.TAGS.pwd, password);


        @SuppressWarnings("unchecked")
        VolleyNetworkRequest mVolleyNetworkRequest = new VolleyNetworkRequest(NetworkUtility.URLS.LOGIN
                , mCallWSErrorListener
                , mCallLoginWSResponseListener
                , null
                , mBodyParams
                , null);
        Volley.getInstance(context).addToRequestQueue(mVolleyNetworkRequest, NetworkUtility.URLS.LOGIN);
    }
    private final Response.ErrorListener mCallWSErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
            listener.volleyError();
        }
    };

}