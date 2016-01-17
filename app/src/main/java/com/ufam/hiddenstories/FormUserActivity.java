package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;

import org.json.JSONArray;
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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Novo usuário");//texto temporário com o nome do local
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mVolleyConnection = new VolleyConnection(this);

        Button btnSave = (Button) findViewById(R.id.btn_save);
        edtName = (EditText) findViewById(R.id.edt_user_name);
        edtLogin = (EditText) findViewById(R.id.edt_login_user);
        edtPasswd = (EditText) findViewById(R.id.edt_login_passwd);
        edtPasswd2 = (EditText) findViewById(R.id.edt_login_passwd_2);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        hideKeyboard();
    }

    private void save(){
        String name = edtName.getText().toString().trim();
        String login = edtLogin.getText().toString().trim();
        String passwd = edtPasswd.getText().toString().trim();
        String passwd2 = edtPasswd2.getText().toString().trim();

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name",name);
        params.put("login",login);
        params.put("passwd",passwd);
        params.put("passwd2",passwd2);

        showLongSnack("Salvando novo usuário...");

        //mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.NEW_USER, Request.Method.POST, params, "NEW_USER");

    }

    @Override
    public void deliveryResponse(JSONArray response, String TAG) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {

    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {

    }
}
