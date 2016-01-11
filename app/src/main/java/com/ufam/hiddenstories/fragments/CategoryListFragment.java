package com.ufam.hiddenstories.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.ufam.hiddenstories.CategoryListActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener, CustomVolleyCallbackInterface {

    protected RecyclerView mRecyclerView;
    private List<Category> mList;
    private VolleyConnection mVolleyConnection;
    final String TAG = CategoryListFragment.this.getClass().getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseActivity)getActivity()).forceStartVolleyQueue();

        mVolleyConnection = new VolleyConnection(this);

        callServer();
    }

    private void callServer (){
        mVolleyConnection.callServerApiByJsonArrayRequest(ServerInfo.CATEGORY_LIST, Request.Method.GET,false, null, "CATEGORY_LIST");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_category);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        //LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //llm.setOrientation(LinearLayoutManager.VERTICAL);
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0){
                    //Log.i(TAG, "scroll up!");
                    ((CategoryListActivity)getContext()).hideFab();;
                }
                else{
                    //Log.i(TAG, "scroll down!");
                    ((CategoryListActivity)getContext()).showFab();;
                }



            }
        });

        //llm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(llm);


        return view;
    }



    public void setList(ArrayList<Category> c){
        mList = c;
        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
    }


    public void setCardView(JSONArray ja){
        ArrayList<Category> categories = new ArrayList<Category>();
        try {
            for(int i = 0, tam = ja.length(); i < tam; i++){
                Category category = new Category();
                category = ((BaseActivity)getActivity()).popCategoryObj(ja.getJSONObject(i));
                categories.add(category);
            }
        }catch (JSONException e){}

        setList(categories);
    }



    @Override
    public void onClickListener(View view, int position) {

        Intent intent = new Intent(getActivity(), PlaceListActivity.class);
        intent.putExtra("category", mList.get(position));
        getActivity().startActivity(intent);

    }

    @Override
    public void deliveryResponse(JSONArray response, String TAG) {
        Log.i(TAG, "SUCESS: "+response.toString());
        setCardView(response);
    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {
    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {
        Log.i(TAG, "ERROR: "+error );
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onLongPressClickListener(View view, int position) {

    }


    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListenerHack rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    if (cv != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onLongPressClickListener(cv,
                                rv.getChildPosition(cv));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    if (cv != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onClickListener(cv,
                                rv.getChildPosition(cv));
                    }

                    return (true);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mVolleyConnection.cancelRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        callServer();
    }
}
