package com.ufam.hiddenstories.conn;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.services.CustomJsonArrayRequest;
import com.ufam.hiddenstories.services.CustomJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rli on 20/10/2015.
 */
public class VolleyConnection {

    private CustomVolleyCallbackInterface mCustomVolleyCallbackInterface;
    private Map<String, String> params;
    private String mVOLLEYTAG;

    public VolleyConnection(CustomVolleyCallbackInterface cvci){
        VolleyConnectionQueue.getINSTANCE(); //inicia a fila de requisições
        mCustomVolleyCallbackInterface = cvci;
    }

    protected void setVolleyTag(String tag){
        Log.i("APP", "setVolleyTag(" + tag + ")");
        this.mVOLLEYTAG = tag;
    }

    public void cancelRequest(){
        if(mVOLLEYTAG!=null){
            VolleyConnectionQueue.getINSTANCE().cancelRequest(this.mVOLLEYTAG);
        }
    }

    public void callServerApiByJsonArrayRequest(final String url, int requestMethod, boolean retyrPolicyForUpload, HashMap<String, String> data, final String TAG){

        params = new HashMap<String, String>();
        Gson gson = new Gson();
        params.put("json", gson.toJson(data));

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE URL","["+activityName+"] : "+url);
        if(TAG!=null){
            Log.i("SEND MESSAGE TAG","["+activityName+"] : "+TAG);
        }
        /*if(data!=null){
            Log.i("SEND MESSAGE DATA","["+activityName+"] : "+data);
            Log.i("SEND MESSAGE JSON", "[" + activityName + "] : " + new JSONObject(data).toString());
        }*/

        Log.i("SEND MESSAGE PARAMS", "[" + activityName + "] : " + params.toString());
        //Request.Method.POST
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(requestMethod,
                url,
                params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Script", "TAG: "+TAG+" | SUCCESS: " + response);
                        mCustomVolleyCallbackInterface.deliveryResponse(response,TAG);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Script", "TAG: "+TAG+" | ERROR: " + error);
                        mCustomVolleyCallbackInterface.deliveryError(error,TAG);
                    }
                });


        if(retyrPolicyForUpload){
            request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
        }else{
            request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            5000,
                            50,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
        }
        this.setVolleyTag(activityName);
        request.setTag(activityName);
        request.setShouldCache(true);
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }

    //METODO PARA ENVIO E RECEBIMENTO DE JSONOBJECTS
    public void callServerApiByJsonObjectRequest(final String url, int requestMethod, boolean retyrPolicyForUpload, HashMap<String, String> data, final String TAG){

        params = new HashMap<String, String>();
        Gson gson = new Gson();
        params.put("json", gson.toJson(data));

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE URL","["+activityName+"] : "+url);
        if(TAG!=null){
            Log.i("SEND MESSAGE TAG","["+activityName+"] : "+TAG);
        }
        /*if(data!=null){
            Log.i("SEND MESSAGE DATA","["+activityName+"] : "+data);
            Log.i("SEND MESSAGE JSON", "[" + activityName + "] : " + new JSONObject(data).toString());
        }*/

        Log.i("SEND MESSAGE PARAMS", "[" + activityName + "] : " + params.toString());
        //Request.Method.POST
        CustomJsonObjectRequest request = new CustomJsonObjectRequest(requestMethod,
                url,
                params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        //envia a resposta de sucesso para a activity
                        Log.i("Script", "TAG: "+TAG+" | SUCCESS: " + response);
                        mCustomVolleyCallbackInterface.deliveryResponse(response, TAG);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //envia a mensagem de erro para a activity
                        Log.i("Script", "TAG: "+TAG+" | ERROR: " + error);
                        mCustomVolleyCallbackInterface.deliveryError(error, TAG);
                    }
                });


        if(retyrPolicyForUpload){
            request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
        }else{
            request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            5000,
                            50,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
        }
        this.setVolleyTag(activityName);
        request.setTag(activityName);
        request.setShouldCache(true);
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }
}
