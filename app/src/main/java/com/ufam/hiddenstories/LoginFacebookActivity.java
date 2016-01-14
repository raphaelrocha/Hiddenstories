package com.ufam.hiddenstories;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginFacebookActivity extends BaseActivity implements CustomVolleyCallbackInterface {

    private Profile mOldProfile, mNewProfile;
    private VolleyConnection mVolleyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login_facebook);

        //setCustomVolleyCallbackInterface(this);
        mVolleyConnection = new VolleyConnection(this);

        showDialog("Configurando tudo para você, Aguarde..",false);
        /*mOldProfile = AppController.getINSTANCE().getOldProfile();
        mNewProfile = AppController.getINSTANCE().getNewProfile();*/

        mNewProfile = getIntent().getParcelableExtra("newProfile");

        if(mNewProfile!=null){
            String name, email, birth, sex, picture_profile,socialnet,passwd1;
            User newUser = new User();

            name = mNewProfile.getName();
            email = mNewProfile.getId()+"@userfacebook.com";
            birth = "2015/01/01";
            sex = "0";
            picture_profile = mNewProfile.getProfilePictureUri(300,300).toString();
            socialnet = mNewProfile.getLinkUri().toString();
            passwd1 = "tweghcewuy98378@hisgad87";

            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPictureProfile(picture_profile);
            newUser.setPasswd(passwd1);

            Log.i("LOGIN fb", picture_profile);
            callServer(newUser);
        }else{
            finish();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_facebook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void setLogin(JSONObject jo){

        User userLogged = null;
        userLogged = popUser(jo);
        getSession().setLogin(true);
        getEditorPref().putBoolean("ul", true); // Storing boolean - true/false

        Gson gson = new Gson();
        String jsonUserLogged = gson.toJson(userLogged);

        getEditorPref().putString("ul-obj", jsonUserLogged); // Storing string

        getEditorPref().commit(); // commit changes
        hideDialog();

        Intent intent = new Intent(LoginFacebookActivity.this, CategoryListActivity.class);
        intent.putExtra("user",userLogged);
        startActivity(intent);
        hideDialog();
        finish();
    }

    public void callServer(User newUser) {
        showDialog("Estamos preparando tudo para você. Aguarde...",false);
        //JSONObject jou = creteJSONObjectUser(name, birth, email, sex, picture_profile, socialnet, passwd1, "0", ServerInfo.fileImageExt);

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name",newUser.getName());
        params.put("email",newUser.getEmail());
        params.put("passwd",newUser.getPasswd());
        params.put("picture_profile",newUser.getPictureProfile());

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.CREATE_USER, Request.Method.POST, false, params, "CRIA_USUARIO");
    }

    @Override
    public void onStop(){
        super.onStop();
        mVolleyConnection.cancelRequest();
        mNewProfile=null;
    }

    //TRATA RESPOSTAS DE SUCESSO
    public void deliveryResponse(JSONArray response, String flag){

    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        hideDialog();

        try {
            setLogin(response.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //TRATA RESPOSTAS DE ERRO
    public void deliveryError(VolleyError error, String flag){
        Alert("Problemas com a internet.");
    }
}
