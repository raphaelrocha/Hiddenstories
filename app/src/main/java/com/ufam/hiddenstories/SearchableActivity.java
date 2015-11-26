package com.ufam.hiddenstories;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.fragments.PlaceListFragment;
import com.ufam.hiddenstories.fragments.PlaceSearchListFragment;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.provider.SearchableProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SearchableActivity extends BaseActivity implements CustomVolleyCallbackInterface{

    private Toolbar mToolbar;
    private PlaceSearchListFragment mFrag;
    private SearchView searchView;
    private Menu menuSearch;
    private String queryToShowInSearchView;
    private VolleyConnection mVolleyConnection;
    private String query;
    private int mCONTA_SNACK_ALERT; //garante q seja exibido apenas um snackalert no erro.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mVolleyConnection = new VolleyConnection(this);

        mCONTA_SNACK_ALERT=0;
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar_layout);
        mToolbar.setTitle("Buscar");//texto temporário com o nome do local
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });*/

        mToolbar = setUpToolbar("Procurar",true,false);;

        // FRAGMENT
        mFrag = (PlaceSearchListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new PlaceSearchListFragment();
            //mFrag.setCategory(category);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.place_search_list_frag_containert, mFrag, "mainFrag");
            ft.commit();
        }

        hendleSearch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        hendleSearch(intent);
    }

    public void hendleSearch( Intent intent ){

        if(Intent.ACTION_SEARCH.equalsIgnoreCase( intent.getAction() )){
            String q = intent.getStringExtra( SearchManager.QUERY );

            queryToShowInSearchView = q;
            filter(q);

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(q, null);
        }
    }

    public void filter(String q){
        //mListAux.clear();
        //buscar no servidor
        Log.i("SearchableActivity","String to query: "+q);
        showQueryOnSearchView();
        callServer(q);
    }

    @Override
    public void onStop(){
        super.onStop();
        mVolleyConnection.canceRequest();
    }

    public void showQueryOnSearchView(){
        //EXIBE O TEXTO BUSCADO NA SEARCHVIEW
        if(searchView!=null && menuSearch!=null){
            MenuItem item = menuSearch.findItem(R.id.action_searchable_activity);
            MenuItemCompat.expandActionView(item);
            Log.i("SearchableActivity", "showQueryOnSearchView(" + queryToShowInSearchView + ")");
            //searchView.setFocusable(false);
            //searchView.setFocusableInTouchMode(false);
            searchView.setQuery(queryToShowInSearchView, false);
            searchView.clearFocus();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        menuSearch = menu;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //Do whatever you want
                Log.i("Expand", "click");

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Do whatever you want
                Log.i("Collapse", "click");

                return true;
            }
        });

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ){
            searchView = (SearchView) item.getActionView();
        }
        else{
            searchView = (SearchView) MenuItemCompat.getActionView( item );
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(Html.fromHtml("<font color = #DCDCDC>" + getResources().getString(R.string.search_hint) + "</font>"));
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        theTextArea.setTextColor(Color.WHITE);//or any color that you want

        return true;
    }

    public void callServer(String query){

        showDialog("Estamos procurando pra você. Aguarde.");

        HashMap<String, String> params = new  HashMap<String, String> ();
        params.put("query", query);

        mVolleyConnection.callServerApiByJsonArrayRequest(ServerInfo.FIND_PLACE,params,null);
    }

    @Override
    public void deliveryResponse(JSONArray response, String TAG) {
        ((BaseActivity)this).hideDialog();
        showQueryOnSearchView();

        Log.i("PLACELISTA_FRAG", response.toString());

        try {
            String id = response.getJSONObject(0).getString("id");
            if(!id.equals("not_found")){
                mFrag.setCardView(response,null);
            }else{
                if(mCONTA_SNACK_ALERT==0){
                    mCONTA_SNACK_ALERT++;
                    Log.i("SNAK", "---- Lançou o snak ----");
                    ((BaseActivity)this).showLongSnack("Nenhum resultado encontrado.");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {

    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {

        ((BaseActivity)this).hideDialog();

        Log.i("APP", "error conn: " + error);
        ((BaseActivity)this).Alert("Problemas com a internet.");

        ((BaseActivity)this).finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("SearchableActivity","onResume()");
        hendleSearch(getIntent());
        mCONTA_SNACK_ALERT=0;
    }
}
