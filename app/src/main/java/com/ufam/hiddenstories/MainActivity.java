package com.ufam.hiddenstories;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends BaseActivity implements CustomVolleyCallbackInterface {

    private final String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btLogin = (Button) findViewById(R.id.btn_login);
        Button btNewUser = (Button) findViewById(R.id.btn_new_user);

        final EditText edtLoginUser = (EditText) findViewById(R.id.edt_login_user);
        final EditText edtLoginPasswd = (EditText) findViewById(R.id.edt_login_passwd);

        edtLoginUser.setText("p1@h.com");
        edtLoginPasswd.setText("123");

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtLoginUser.getText().toString().trim();
                String passwd = edtLoginPasswd.getText().toString().trim();
                login(user,passwd);
            }
        });

        btNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });

        if (getSession().isLoggedIn()) {
            Log.i(TAG,"Já está logado.");
            showDialog("Aguarde.",false);
            startApp(getUserLoggedObj());
        }
    }

    private void login(String user, String passwd){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("user",user);
        params.put("passwd",passwd);
        VolleyConnection conn = new VolleyConnection(this);
        conn.callServerApiByJsonObjectRequest(ServerInfo.LOGIN, Request.Method.POST,false, params,"LOGIN");
    }

    private void newUser(){
        Intent intent = new Intent(this, FormUserActivity.class);
        startActivityForResult(intent,0);
    }

    private void savePrefers(JSONObject jo){
        hideKeyboard();

        try {
            boolean b = jo.getBoolean("success");
            if(b){
                User u = popUser(jo.getJSONObject("user"));

                getSession().setLogin(true);
                getEditorPref().putBoolean("ul", true); // Storing boolean - true/false

                Gson gson = new Gson();
                String jsonUserLogged = gson.toJson(u);

                getEditorPref().putString("ul-obj", jsonUserLogged); // Storing string

                getEditorPref().commit(); // commit changes
                hideDialog();

                getEditorPref().commit();
                startApp(u);
            }else{
                Alert("Erro no login ou senha.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Alert("Algo deu errado. Tente novamente.");
        }
    }

    private void startApp(User user){
        Intent intent = new Intent(this, CategoryListActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    /*public void openPlace(){
        Intent intent = new Intent(this, PlaceListActivity.class);
        //intent.putExtra("car", mList.get(position));

        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

            //View ivPlace = view.findViewById(R.id.);
            //View tvModel = view.findViewById(R.id.tv_model);
            //View tvBrand = view.findViewById(R.id.tv_brand);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                    //Pair.create(ivCar, "element1"),
                    //Pair.create( tvModel, "element2" ),
                    //Pair.create( tvBrand, "element3" ));

            this.startActivity( intent, options.toBundle() );
        }
        else{
            this.startActivity(intent);
        }
    }

    public void openCategory(){
        Intent intent = new Intent(this, CategoryListActivity.class);
        //intent.putExtra("car", mList.get(position));

        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

            //View ivPlace = view.findViewById(R.id.);
            //View tvModel = view.findViewById(R.id.tv_model);
            //View tvBrand = view.findViewById(R.id.tv_brand);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            //Pair.create(ivCar, "element1"),
            //Pair.create( tvModel, "element2" ),
            //Pair.create( tvBrand, "element3" ));

            this.startActivity( intent, options.toBundle() );
        }
        else{
            this.startActivity(intent);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }

    @Override
    public void deliveryResponse(JSONArray response, String TAG) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {
        hideDialog();
        savePrefers(response);
    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {

    }
}
