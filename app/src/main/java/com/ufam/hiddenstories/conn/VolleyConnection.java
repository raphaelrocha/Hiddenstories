package com.ufam.hiddenstories.conn;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

    public void canceRequest(){
        if(mVOLLEYTAG!=null){
            VolleyConnectionQueue.getINSTANCE().cancelRequest(this.mVOLLEYTAG);
        }
    }

    public void callServerApiByJsonArrayRequest(final String url, String method, String data, final String flag){

        params = new HashMap<String, String>();
        if(data!=null){
            params.put("data", data);
        }
        if(method!=null){
            params.put("method", method);
        }

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE", "[" + activityName + "] url: " + url + " data: " + data);
        Log.i("SEND MESSAGE FlAG", "[" + activityName + "] flag: " + flag);

        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Script", "FLAG: " + flag + " | SUCCESS: " + response);
                        mCustomVolleyCallbackInterface.deliveryResponse(response,flag);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Script", "FLAG: " + flag + " | ERROR: " + error);
                        mCustomVolleyCallbackInterface.deliveryError(error,flag);
                    }
                });

        request.setTag(activityName);
        this.setVolleyTag(activityName);
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }

    //METODO PARA ENVIO E RECEBIMENTO DE JSONOBJECTS
    public void callServerApiByJsonObjectRequest(final String url, String method, String data, final String flag){

        params = new HashMap<String, String>();
        if(data!=null){
            params.put("data", data);
        }
        if(method!=null){
            params.put("method", method);
        }

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE", "[" + activityName + "] url: " + url + " data: " + data);
        Log.i("SEND MESSAGE FlAG", "[" + activityName + "] flag: " + flag);

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        //envia a resposta de sucesso para a activity
                        Log.i("Script", "FLAG: " + flag + " | SUCCESS: " + response);
                        mCustomVolleyCallbackInterface.deliveryResponse(response, flag);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //envia a mensagem de erro para a activity
                        Log.i("Script", "FLAG: " + flag + " | ERROR: " + error);
                        mCustomVolleyCallbackInterface.deliveryError(error, flag);
                    }
                });

        request.setTag("tagPhotoActivity");
        this.setVolleyTag("tagPhotoActivity");
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);
    }
}
