package com.ufam.hiddenstories.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ufam.hiddenstories.BaseActivity;
import com.ufam.hiddenstories.PlaceListActivity;
import com.ufam.hiddenstories.R;
import com.ufam.hiddenstories.adapters.CommentaryListAdapter;
import com.ufam.hiddenstories.adapters.RatingListAdapter;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.interfaces.RecyclerViewOnClickListenerHack;
import com.ufam.hiddenstories.models.Commentary;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentaryListFragment extends Fragment implements  CustomVolleyCallbackInterface, RecyclerViewOnClickListenerHack {

    protected RecyclerView mRecyclerView;
    private List<Commentary> mList;
    private VolleyConnection mVolleyConnection;
    private Place mPlace;
    private int mCONTA_SNACK_ALERT; //garante q seja exibido apenas um snackalert no erro.
    private EditText edtCommentary;
    private ImageButton btSendCommentary;
    private final String TAG = CommentaryListFragment.this.getClass().getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseActivity)getActivity()).forceStartVolleyQueue();

        mVolleyConnection = new VolleyConnection(this);

        mCONTA_SNACK_ALERT=0;

        mPlace = getArguments().getParcelable("place");
    }

    public void callServer (){
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("id_place",mPlace.getId());
        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.GET_COMMENTARY_LIST, Request.Method.POST,false, params, "GET_COMMENTARY_LIST");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_commentary_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_commentary);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //llm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(llm);

        edtCommentary = (EditText) view.findViewById(R.id.edt_send_commentary);
        btSendCommentary = (ImageButton) view.findViewById(R.id.bt_send_commentary);

        edtCommentary.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        btSendCommentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentary();
            }
        });

        callServer();

        return view;
    }

    private void sendCommentary(){

        String commentary = edtCommentary.getText().toString().trim();

        if(!commentary.equals("")){
            ((BaseActivity)getActivity()).hideKeyboard();
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("id_user",((BaseActivity)getActivity()).getUserLoggedObj().getId());
            params.put("id_place",mPlace.getId());
            params.put("comment_text",commentary);

            mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.ACTION_SET_COMMENTARY, Request.Method.POST, false,params,"ACTION_SET_COMMENTARY");
        }
    }

    public void setList(ArrayList<Commentary> r){
        mList = r;
        CommentaryListAdapter adapter = new CommentaryListAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
    }


    public void setCardView(JSONObject jo){
        ArrayList<Commentary> commentaries = new ArrayList<Commentary>();

        try {
            boolean b = jo.getBoolean("success");
            if(b){
                JSONArray ja = jo.getJSONArray("commentaries");
                for(int i = 0, tam = ja.length(); i < tam; i++){
                    Commentary c = ((BaseActivity)getActivity()).popCommentaryObj(ja.getJSONObject(i));

                    commentaries.add(c);
                }
            }else{
                if(mCONTA_SNACK_ALERT==0){
                    mCONTA_SNACK_ALERT++;
                    Log.i(TAG, "---- Lançou o snak ----");
                    ((BaseActivity)getActivity()).showLongSnack(getResources().getString(R.string.list_empty));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if(mCONTA_SNACK_ALERT==0){
                mCONTA_SNACK_ALERT++;
                Log.i(TAG, "---- Lançou o snak ----");
                ((BaseActivity)getActivity()).showLongSnack(getResources().getString(R.string.list_empty));
            }
        }

        setList(commentaries);
    }



    @Override
    public void onClickListener(View view, int position) {

    }

    @Override
    public void onLongPressClickListener(View view, int position) {

        Log.w(TAG,"onLongClickListener()");
        User userLogged = ((BaseActivity)getActivity()).getUserLoggedObj();
        String idUser = mList.get(position).getUser().getId();
        final int pos = position;

        if(userLogged.getId().equals(idUser)){
            //((BaseActivity)getActivity()).showSnack("Excluir");

            Snackbar snackbar = Snackbar
                    .make(view, "Excluir comentário?", Snackbar.LENGTH_LONG)
                    .setAction("Sim", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callServerToDelete(pos);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callServerToDelete(int position){
        ((BaseActivity)getActivity()).showDialog("Aguartde.",true);
        HashMap<String,String> params = new HashMap<String,String >();
        params.put("id_commentary",mList.get(position).getId());

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.ACTION_DELETE_COMMENTARY, Request.Method.POST, false,params,"ACTION_DELETE_COMMENTARY");
    }

    private void parseSendResponse(JSONObject jo){
        try {
            boolean b = jo.getBoolean("success");
            if(b){
                edtCommentary.setText("");
                callServer();
            }else{
                ((BaseActivity)getActivity()).Alert("Algo deu errado");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryResponse(JSONArray response, String flag) {
        ((BaseActivity)getActivity()).hideDialog();
    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        ((BaseActivity)getActivity()).hideDialog();
        Log.i(TAG, "SUCESS: "+response.toString());
        if(flag.equals("ACTION_SET_COMMENTARY")){
            parseSendResponse(response);
        }else if(flag.equals("GET_COMMENTARY_LIST")){
            setCardView(response);
        }else if(flag.equals("ACTION_DELETE_COMMENTARY")){
            callServer();
        }
    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        Log.i(TAG, "ERROR: "+error );
        ((BaseActivity)getActivity()).hideDialog();
        ((BaseActivity)getActivity()).Alert("Algo deu errado");
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
        mCONTA_SNACK_ALERT=0;
        callServer();
    }
}
