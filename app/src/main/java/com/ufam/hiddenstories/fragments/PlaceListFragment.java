package com.ufam.hiddenstories.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ufam.hiddenstories.BaseActivity;
import com.ufam.hiddenstories.PlaceActivity;
import com.ufam.hiddenstories.PlaceListActivity;
import com.ufam.hiddenstories.R;
import com.ufam.hiddenstories.adapters.CategoryListAdapter;
import com.ufam.hiddenstories.adapters.PlaceListAdapter;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.interfaces.RecyclerViewOnClickListenerHack;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.tools.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaceListFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener, CustomVolleyCallbackInterface {
    protected RecyclerView mRecyclerView;
    protected List<Place> mList;
    protected android.support.design.widget.FloatingActionButton fab;
    protected VolleyConnection mVolleyConnection;
    private Category mCategory;
    final String TAG = PlaceListFragment.this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"onCreate()");

        mVolleyConnection = new VolleyConnection(this);

        ((BaseActivity)getActivity()).forceStartVolleyQueue();

        mCategory = getArguments().getParcelable("category");

        callServer();
    }

    protected void callServer(){
        GPSTracker gpsTracker = ((BaseActivity)getActivity()).getGpsTracker();
        Double lat = gpsTracker.getLatitude();
        Double lng = gpsTracker.getLongitude();

        Integer radius = ((BaseActivity)getActivity()).getDistanceRadius();

        Log.i(TAG,"callServer()"+ mCategory.getId());
        HashMap<String, String> params = new  HashMap<String, String> ();
        params.put("id_cat", mCategory.getId());
        params.put("user_latitude", lat.toString());
        params.put("user_longitude", lng.toString());
        params.put("radius", radius.toString());

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.GET_PLACE_BY_CAT, Request.Method.POST,params,"GET_PLACE_BY_CAT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place, container, false);

        Log.i("PLACELISTFRAG","onCreateView()");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //llm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(llm);



        return view;
    }

    public void setList(ArrayList<Place> c){
        Log.i("PLACELISTFRAG","setList()");
        mList = c;
        PlaceListAdapter adapter = new PlaceListAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
    }


    public void setCardView(JSONObject jo){
        Log.i(TAG,"setCardView()");

        JSONArray ja = null;
        try {
            boolean b = jo.getBoolean("success");
            if(b){
                ja = jo.getJSONArray("places");
                ArrayList<Place> places = new ArrayList<Place>();
                for(int i = 0, tam = ja.length(); i < tam; i++){
                    Place place = new Place();
                    place = ((BaseActivity)getActivity()).popPlaceObj(ja.getJSONObject(i));
                    places.add(place);
                }
                setList(places);
            }else{
                ((BaseActivity)getActivity()).Alert("Algo deu errado");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ((BaseActivity)getActivity()).Alert("Algo deu errado");
        }


    }

    //public void setCategory(Category c){
    //    this.mCategory = c;
    //}

    @Override
    public void onClickListener(View view, int position) {
        Log.i(TAG,"onClickListener()");

        Intent intent = new Intent(getActivity(), PlaceActivity.class);
        intent.putExtra("place", mList.get(position));
        getActivity().startActivity(intent);


    }
    @Override
    public void onLongPressClickListener(View view, int position) {
        Log.i(TAG,"onLongPressClickListener()");
        Toast.makeText(getActivity(), "onLongPressClickListener(): " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {
        Log.i(TAG,"deliveryResponse(Array)");

    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        Log.i(TAG,"deliveryResponse(Object)");
        Log.i(TAG, response.toString());
        setCardView(response);

    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        Log.i(TAG,"deliveryError()");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG,"onStop()");
        mVolleyConnection.canceRequest();
    }


    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListenerHack rvoclh){
            mContext = c;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    if(cv != null && mRecyclerViewOnClickListenerHack != null){
                        mRecyclerViewOnClickListenerHack.onLongPressClickListener(cv,
                                rv.getChildPosition(cv) );
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    if(cv != null && mRecyclerViewOnClickListenerHack != null){
                        mRecyclerViewOnClickListenerHack.onClickListener(cv,
                                rv.getChildPosition(cv) );
                    }

                    return(true);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }


    @Override
    public void onClick(View v) {

        Log.i(TAG,"onClick()");
        String aux = "";


        Toast.makeText(getActivity(), aux, Toast.LENGTH_SHORT).show();
    }


    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("PLACELISTFRAG","onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("mList", (ArrayList<Car>) mList);
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        Log.i("PLACELISTFRAG","onResume()");
        mCategory = getArguments().getParcelable("category");
    }*/

    @Override
    public void onResume() {
        super.onResume();
        callServer();
    }
}
