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

public class FormUserActivity extends BaseActivity implements CustomVolleyCallbackInterface{


    private Toolbar mToolbar;
    private EditText edtName, edtLogin, edtPasswd, edtPasswd2;
    private VolleyConnection mVolleyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);

        mToolbar  = setUpToolbar("Criar conta",true,false);

        mVolleyConnection = new VolleyConnection(this);

        Button btnSave = (Button) findViewById(R.id.btn_save);

        edtLogin = (EditText) findViewById(R.id.edt_login_user);
        edtPasswd = (EditText) findViewById(R.id.edt_login_passwd);
        edtPasswd2 = (EditText) findViewById(R.id.edt_login_passwd_2);

        edtName = (EditText) findViewById(R.id.edt_user_name);
        edtName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtName.requestFocus();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validade();
            }
        });

        hideKeyboard();
    }

    private void validade(){
        String name = edtName.getText().toString().trim();
        String email = edtLogin.getText().toString().trim();
        String passwd = edtPasswd.getText().toString().trim();
        String passwd2 = edtPasswd2.getText().toString().trim();

        boolean valid = true;
        String msg = "";

        if(name.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"Informe seu nome.";
            }else{
                msg = msg+"\nInforme seu nome.";
            }
        }

        if(email.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"Informe seu e-mail.";
            }else{
                msg = msg+"\nInforme seu e-mail.";
            }
        }

        if(passwd.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"Informe sua senha.";
            }else{
                msg = msg+"\nInforme sua senha.";
            }
        }

        if(passwd2.equals("")){
            valid = false;
            if(msg.equals("")){
                msg = msg+"Repita sua senha.";
            }else{
                msg = msg+"\nRepita sua senha.";
            }
        }

        if(!passwd.equals(passwd2)){
            valid = false;
            if(msg.equals("")){
                msg = msg+"As senhas devem ser iguais.";
            }else{
                msg = msg+"\nAs senhas devem ser iguais.";
            }
        }

        if(valid){
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPasswd(passwd);
            u.setPictureProfile("user.jpg");
            save(u);
        }else{
            longAlert(msg);
        }
    }

    private void save(User u){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name",u.getName());
        params.put("email",u.getEmail());
        params.put("passwd",u.getPasswd());
        params.put("picture_profile",u.getPictureProfile());

        showLongSnack("Salvando novo usuário...");

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.CREATE_USER, Request.Method.POST, false, params, "CRIA_USUARIO");
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

        Intent intent = new Intent(FormUserActivity.this, CategoryListActivity.class);
        intent.putExtra("user",userLogged);
        startActivity(intent);
        hideDialog();
        finish();
    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {

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

    @Override
    public void deliveryError(VolleyError error, String flag) {

    }
}
