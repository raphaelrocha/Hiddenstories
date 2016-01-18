package com.ufam.hiddenstories;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ProfileManagerActivity extends BaseActivity implements CustomVolleyCallbackInterface {

    private EditText edtName;
    private EditText edtPasswd;
    private EditText edtPasswd2;
    private VolleyConnection mVolleyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);
        Toolbar toolbar = setUpToolbar("Dados da conta", true, false);

        mVolleyConnection = new VolleyConnection(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final User user = getUserLoggedObj();
        TextView tvEmail = (TextView) findViewById(R.id.txtView_email);
        TextView labelPasswd = (TextView) findViewById(R.id.txt_passwd);
        TextView labelPasswd2 = (TextView) findViewById(R.id.txt_passwd2);
        edtPasswd = (EditText) findViewById(R.id.txtView_passwd);
        edtPasswd2 = (EditText) findViewById(R.id.txtView_passwd2);

        Button btnUpdate = (Button) findViewById(R.id.btn_updateUser);
        Button btnDelete = (Button) findViewById(R.id.btn_deleteUser);

        edtName = (EditText) findViewById(R.id.txtView_username);
        edtName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtName.setText(user.getName());
        edtPasswd.setText(user.getPasswd());
        edtPasswd2.setText(user.getPasswd());

        if(user.getEmail().contains("facebook.com")){
            tvEmail.setText("Esta é uma conta Facebook");
            labelPasswd.setVisibility(View.INVISIBLE);
            labelPasswd2.setVisibility(View.INVISIBLE);
            edtPasswd.setVisibility(View.INVISIBLE);
            edtPasswd2.setVisibility(View.INVISIBLE);
        }else{
            tvEmail.setText(user.getEmail());
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUSer(user);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validade(user);
            }
        });

        edtName.requestFocus();
        hideKeyboard();
    }

    private void validade(User u){
        String name = edtName.getText().toString().trim();
        String passwd = edtPasswd.getText().toString().trim();
        String passwd2 = edtPasswd2.getText().toString().trim();

        boolean valid = true;
        String msg = "";

        if(name.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"\"Informe seu nome.";
            }else{
                msg = msg+"\n\"Informe seu nome.";
            }
        }

        if(passwd.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"\"Informe sua senha.";
            }else{
                msg = msg+"\n\"Informe sua senha.";
            }
        }

        if(passwd2.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"\"Repita sua senha.";
            }else{
                msg = msg+"\n\"Repita sua senha.";
            }
        }

        if(!passwd.equals(passwd2)){
            valid = false;
            if(msg.equals("")){
                msg = msg+"\"As senhas devem ser iguais.";
            }else{
                msg = msg+"\n\"As senhas devem ser iguais.";
            }
        }

        if(valid){
            u.setName(name);
            u.setPasswd(passwd);
            saveUser(u);
        }else{
            longAlert(msg);
        }
    }

    private void saveUser(User u){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("id", u.getId());
        params.put("name", u.getName());
        params.put("passwd", u.getPasswd());

        showDialog("Aguarde enquanto atualizamos seus dados.", false);
        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.UPDATE_USER, Request.Method.POST,false,params,"UPDATE_USER");
    }

    private void deleteUSer(User u){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("id", u.getId());

        showDialog("Aguarde enquanto excluímos a sua conta.", false);
        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.DELETE_USER, Request.Method.POST,false,params,"DELETE_USER");
    }

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

        Intent intent = new Intent(ProfileManagerActivity.this, CategoryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user",userLogged);
        startActivity(intent);
        finish();
    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        hideDialog();
        if(flag.equals("DELETE_USER")){
            logoutUser();
        }else if(flag.equals("UPDATE_USER")){
            try {
                setLogin(response.getJSONObject("user"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        hideDialog();
    }

    @Override
    public void onStop(){
        super.onStop();
        mVolleyConnection.cancelRequest();
    }
}
