package com.ufam.hiddenstories;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.ufam.hiddenstories.fragments.CategoryListFragment;
import com.ufam.hiddenstories.fragments.PlaceListFragment;
import com.ufam.hiddenstories.models.User;
import com.ufam.hiddenstories.tools.DataUrl;

public class CategoryListActivity extends BaseActivity {

    private CategoryListFragment mFrag;
    private SearchView searchView;
    private Menu menuSearch;
    private Drawer navigationDrawerLeft;
    private AccountHeader headerNavigationLeft;
    private User userLogged;
    private int mPositionClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias");
        setSupportActionBar(toolbar);

        mFrag = (CategoryListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new CategoryListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.category_list_frag_container, mFrag, "mainFrag");
            ft.commit();
        }

        userLogged = getUserFromPrefers();

        //LIB PARA CARREGAR A FOTO DO DRAWER
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).error(R.drawable.n_perfil).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        });

        setDrawerUser(savedInstanceState,toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);

        menuSearch = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView;
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

    private void setDrawerUser(Bundle bundle, Toolbar toolbar){

        headerNavigationLeft = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(bundle)
                .withThreeSmallProfileImages(true)
                .withHeaderBackground(R.drawable.header)
                //.withTextColor(R.color.colorPrimarytext)
                .addProfiles(
                        //new ProfileDrawerItem().withName(name).withEmail(email).withIcon(getResources().getDrawable(R.drawable.user))
                        //new ProfileDrawerItem().withName(name).withEmail(email).withIcon(uriPicProf)
                        new ProfileDrawerItem().withName(userLogged.getName()).withIcon(userLogged.getPictureProfile())
                        //new ProfileDrawerItem().withName("Person Two").withEmail("person2@gmail.com").withIcon(getResources().getDrawable(R.drawable.person_2))
                        //new ProfileDrawerItem().withName("Person Three").withEmail("person3@gmail.com").withIcon(getResources().getDrawable(R.drawable.person_3)),
                        //new ProfileDrawerItem().withName("Person Four").withEmail("person4@gmail.com").withIcon(getResources().getDrawable(R.drawable.person_4))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        //Toast.makeText(CategoryActivity.this, "onProfileChanged: ", Toast.LENGTH_SHORT).show();
                        //headerNavigationLeft.setBackgroundRes(R.drawable.camaro);
                        //goToProfileUser();
                        return false;
                    }
                })

                .build();
        navigationDrawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                //.withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(bundle)
                .withSelectedItem(-1)
                //.withFullscreen(true)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerNavigationLeft)
                    /*.withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                        @Override
                        public boolean onNavigationClickListener(View view) {
                            return false;
                        }
                    })*/
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {


                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Alert("teste"+position);
                        //return false;

                        //for (int count = 0, tam = navigationDrawerLeft.getDrawerItems().size(); count < tam; count++) {
                            //if (count == mPositionClicked && mPositionClicked <= 6) {
                                if (position == 1) {
                                    //AJSUTES
                                    //Toast.makeText(MainActivity.this, "click: " + i, Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(CategoryListActivity.this, AdjustActivity.class);
                                    startActivityForResult(myIntent, 0);
                                }
                                else if (position == 2) {
                                    //SAIR
                                    //Toast.makeText(MainActivity.this, "click: " + i, Toast.LENGTH_SHORT).show();
                                    logoutUser();
                                } else if (position == 3) {
                                    //SAIR
                                    //Toast.makeText(MainActivity.this, "click: " + i, Toast.LENGTH_SHORT).show();
                                    clearSearchHistory();
                                    Alert("Suas buscas recentes foram apagadas");
                                }
                                //PrimaryDrawerItem aux = (PrimaryDrawerItem) navigationDrawerLeft.getDrawerItems().get(count);
                                //aux.setIcon(getResources().getDrawable( getCorretcDrawerIcon( count, false ) ));

                               // break;
                            //}
                        //}

                        /*if(i <= 3){
                            ((PrimaryDrawerItem) iDrawerItem).setIcon(getResources().getDrawable( getCorretcDrawerIcon( i, true ) ));
                        }*/

                        mPositionClicked = position;
                        navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                        return true;
                    }


                })
                /*.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        //Toast.makeText(CategoryActivity.this, "onItemLongClick: " + i, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })*/
                /*.addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("Configurações").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(0),
                        //new SecondaryDrawerItem().withName("teste").withIcon(FontAwesome.Icon.faw_github)
                )*/
                .build();

        /*if(getSession().isLoggedIn()){
            navigationDrawerLeft.addItem(new SectionDrawerItem().withName(name));
        }*/

        //navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_find).withIcon(getResources().getDrawable(R.drawable.magnify)));
        //navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_favorites).withIcon(getResources().getDrawable(R.drawable.star)));
        //navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_find_in_map).withIcon(getResources().getDrawable(R.drawable.map)));
        //navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_popular).withIcon(getResources().getDrawable(R.drawable.magnify_plus)));
        //navigationDrawerLeft.addItem(new DividerDrawerItem());
        //navigationDrawerLeft.addItem(new SecondaryDrawerItem().withName(R.string.drawer_configuration).withIcon(getResources().getDrawable(R.drawable.settings)));
        //navigationDrawerLeft.addItem(new SecondaryDrawerItem().withName(R.string.drawer_configuration).withIcon(getResources().getDrawable(R.drawable.settings)));
        navigationDrawerLeft.addItem(new SecondaryDrawerItem().withName(R.string.drawer_adjust).withIcon(getResources().getDrawable(R.drawable.settings)));
        navigationDrawerLeft.addItem(new SecondaryDrawerItem().withName(R.string.drawer_logout).withIcon(getResources().getDrawable(R.drawable.logout)));
        navigationDrawerLeft.addItem(new SecondaryDrawerItem().withName(R.string.drawer_clear_search_history).withIcon(getResources().getDrawable(R.drawable.ic_delete_variant)));
        //navigationDrawerLeft.addItem(new SectionDrawerItem().withName("Configurações"));
        //navigationDrawerLeft.addItem(new SwitchDrawerItem().withName("Notificação").withChecked(true).withOnCheckedChangeListener(mOnCheckedChangeListener));
        //navigationDrawerLeft.addItem(new ToggleDrawerItem().withName("News").withChecked(true).withOnCheckedChangeListener(mOnCheckedChangeListener));


        //rq = Volley.newRequestQueue(MainActivity.this);
        //callByJsonArrayRequest(url,"get-all-pro","");

        hideDialog();
    }

}
