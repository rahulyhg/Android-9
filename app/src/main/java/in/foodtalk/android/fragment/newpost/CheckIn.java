package in.foodtalk.android.fragment.newpost;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.CheckInAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.AddRestaurantCallback;
import in.foodtalk.android.communicator.CheckInCallback;
import in.foodtalk.android.communicator.LatLonCallback;
import in.foodtalk.android.constant.ConstantVar;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.GetLocation;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.RestaurantListObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class CheckIn extends Fragment implements SearchView.OnQueryTextListener, LatLonCallback {

    View layout;
    DatabaseHandler db;
    Config config;
    CheckInAdapter checkInAdapter;

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    List<RestaurantListObj> restaurantList = new ArrayList<>();

    JSONObject response;

    LatLonCallback latLonCallback;

    GetLocation getLocation;

    LinearLayout btnAddRestaurant;

    String lat;
    String lon;

    CheckInCallback checkInCallback;

    AddRestaurantCallback addRestaurantCallback;

    LinearLayout progressBarCheckin;

    LinearLayout tapToRetry;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.checkin_fragment, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_checkin);

        final SearchView searchView = (SearchView) layout.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);

        btnAddRestaurant = (LinearLayout) layout.findViewById(R.id.btn_add_restaurant);

        final TextView btnSkip = (TextView) layout.findViewById(R.id.btn_skip_checkin);

        progressBarCheckin = (LinearLayout) layout.findViewById(R.id.progress_bar_checkin);

        latLonCallback = this;

        addRestaurantCallback = (AddRestaurantCallback) getActivity();


        Fragment currentFragment = this.getFragmentManager().findFragmentById(R.id.container1);
        if (currentFragment == this){
            getLocation = new GetLocation(getActivity(), latLonCallback);
            getLocation.onStart();
        }


        Log.d("CheckIn", "getLocation");

        checkInCallback = (CheckInCallback) getActivity();

        //checkInCallback.checkInRestaurant("","");
        //Log.d("checkIn Fragment","call checkInREstaurant" );



        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarCheckin.setVisibility(View.VISIBLE);
                tapToRetry.setVisibility(View.GONE);
                try {
                    getRestaurantList("load");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnAddRestaurant.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()){
                    case R.id.btn_add_restaurant:
                        switch (event.getAction()){
                            case MotionEvent.ACTION_UP:
                                Log.d("onClick", "add restaurant");
                                addRestaurantCallback.addNewRestaurant();
                                break;
                        }
                        break;
                }
                return true;
            }
        });
        btnSkip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()){
                    case R.id.btn_skip_checkin:
                        switch (event.getAction()){
                            case MotionEvent.ACTION_UP:
                                Log.d("clicked","skip btn");
                                checkInCallback.checkInRestaurant("","");
                                break;
                        }
                    break;
                }
                return true;
            }
        });
        return layout;
    }
    @Override
    public void onDestroyView() {
        Log.d("Fragment","onDestryView");
        //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        getLocation.onStop();
        super.onDestroyView();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        linearLayoutManager = new LinearLayoutManager(getActivity());



        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String rId = bundle.getString("rId");
            Log.d("rId in checkin", rId);
            checkInCallback.checkInRestaurant(rId,"");
        }else {
            Log.d("bundle","null");
        }

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        config = new Config();
        db = new DatabaseHandler(getActivity().getApplicationContext());

        Log.d("get user info F", db.getUserDetails().get("sessionId"));
        Log.d("get user info F", db.getUserDetails().get("userId"));
        super.onActivityCreated(savedInstanceState);
    }
    public void getRestaurantList(final String tag) throws JSONException {

        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("latitude",lat);
        obj.put("longitude",lon);
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
       // obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_NEAR_BY_RESTAURANT, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, "After Sending JsongObj"+response.toString());
                        //msgResponse.setText(response.toString());
                        Log.d("Login Respond", response.toString());
                        try {
                            String status = response.getString("status");
                            if (!status.equals("error")){
                                //-- getAndSave(response);

                                loadDataIntoView(response , tag);
                            }else {
                                String errorCode = response.getString("errorCode");
                                if(errorCode.equals("6")){
                                    Log.d("Response error", "Session has expired");
                                    //logOut();
                                }else {
                                    Log.e("Response status", "some error");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Json Error", e+"");
                        }
                        //----------------------
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                //showToast("Please check your internet connection");

                progressBarCheckin.setVisibility(View.GONE);
                tapToRetry.setVisibility(View.VISIBLE);

                if(tag.equals("refresh")){
                    //swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    //remove(null);
                    //callScrollClass();
                    //pageNo--;
                }
                // hideProgressDialog();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                UserAgent userAgent = new UserAgent();
                if (userAgent.getUserAgent(getActivity()) != null ){
                    headers.put("User-agent", userAgent.getUserAgent(getActivity()));
                }
                return headers;
            }
        };
        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }

    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        progressBarCheckin.setVisibility(View.GONE);

        this.response = response;
        JSONArray rListArray = response.getJSONArray("restaurants");
       // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
            RestaurantListObj current = new RestaurantListObj();
            current.id = rListArray.getJSONObject(i).getString("id");
            current.area = rListArray.getJSONObject(i).getString("area");
            current.restaurantName = rListArray.getJSONObject(i).getString("restaurantName");
            current.restaurantIsActive = rListArray.getJSONObject(i).getString("restaurantIsActive");
            restaurantList.add(current);
        }
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null){
            checkInAdapter = new CheckInAdapter(getActivity(),restaurantList);
            recyclerView.setAdapter(checkInAdapter);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("onQueryTextSubmit",query);
        return false;
    }
    List<RestaurantListObj> tempList;

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("onTextchange search", newText);
        Log.d("restaurantList","size: "+ restaurantList.size());


        try {
            if (response != null){
                JSONArray rListArray = response.getJSONArray("restaurants");
                restaurantList.clear();
                for (int i=0;i<rListArray.length();i++){
                    RestaurantListObj current = new RestaurantListObj();
                    current.id = rListArray.getJSONObject(i).getString("id");
                    current.area = rListArray.getJSONObject(i).getString("area");
                    current.restaurantName = rListArray.getJSONObject(i).getString("restaurantName");
                    current.restaurantIsActive = rListArray.getJSONObject(i).getString("restaurantIsActive");
                    restaurantList.add(current);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Log.d("rListArray", "total: "+ rListArray.length());
       // tempList = new ArrayList<RestaurantListObj>(restaurantList);

        if (checkInAdapter != null){
            final List<RestaurantListObj> filteredModelList = filter(restaurantList, newText);
            checkInAdapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
        }
        return true;
    }

    private List<RestaurantListObj> filter(List<RestaurantListObj> models, String query) {
        query = query.toLowerCase();
        //this.postData =  new ArrayList<RestaurantPostObj>(postList);
        final List<RestaurantListObj> filteredModelList = new ArrayList<>();
        for (RestaurantListObj model : models) {
            final String text = model.restaurantName.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    @Override
    public void location(String gpsStatus, String lat, String lon) {
        //Log.d("location",lat+" : "+lon);
        if (gpsStatus.equals(ConstantVar.LOCATION_GOT)){
            FlurryAgent.setLocation((float)Double.parseDouble(lat), (float)Double.parseDouble(lon));
            this.lat = lat;
            this.lon = lon;
            try {
                getRestaurantList("load");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}