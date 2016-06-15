package in.foodtalk.android.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.R;
import in.foodtalk.android.adapter.newpost.SearchAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.SearchCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.object.SearchResultObj;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class SearchResult extends Fragment implements SearchCallback {

    View layout;
    public static final String ARG_PAGE = "page";
    private static final int TAB_DISH_SEARCH = 0;
    private static final int TAB_USER_SEARCH = 1;
    private static final int TAB_RESTAURANT_SEARCH = 2;

    RecyclerView recyclerView;
    DatabaseHandler db;
    Config config;
    String keyword;
    ImageView imgHolder;
    TextView txtHolder;

    private int mPageNumber;

    List<SearchResultObj> searchResultList = new ArrayList<>();

    String sessionId;
    JSONObject response;

    SearchAdapter searchAdapter;
    Boolean searchResultLoaded = false;

    static final String TAG = "searchDish";

    LinearLayoutManager linearLayoutManager;

    Activity activity;

    public static SearchResult create(int pageNumber) {
        SearchResult fragment = new SearchResult();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        Log.d("onCreate tab", getArguments().getInt(ARG_PAGE)+"");
    }

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.search_tab1, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        AppController.getInstance().recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_search_dish);

        AppController.getInstance().recyclerView.setLayoutManager(linearLayoutManager);

        imgHolder = (ImageView) layout.findViewById(R.id.img_holder_search);
        txtHolder = (TextView) layout.findViewById(R.id.txt_search);

        Log.d("onCreateview tab", mPageNumber+"");

        db = new DatabaseHandler(getActivity());

        sessionId = db.getUserDetails().get("sessionId");

        AppController.getInstance().sessionId = sessionId;

        Log.d("session id createView", sessionId);







        /*if (mPageNumber == TAB_DISH_SEARCH){
            imgHolder.setImageResource(R.drawable.ic_local_dining_black_48dp);
            txtHolder.setText("Find awesome dishes.");
        }
        if (mPageNumber == TAB_USER_SEARCH){
            imgHolder.setImageResource(R.drawable.ic_supervisor_account_black_48dp);
            txtHolder.setText("Food is fun with friends.");
        }
        if (mPageNumber == TAB_RESTAURANT_SEARCH){
            imgHolder.setImageResource(R.drawable.ic_store_mall_directory_black_48dp);
            txtHolder.setText("Find best restaurants.");
        }*/

        switch (mPageNumber){
            case TAB_DISH_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_local_dining_black_48dp);
                txtHolder.setText("Find awesome dishes.");
                break;
            case TAB_USER_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_supervisor_account_black_48dp);
                txtHolder.setText("Food is fun with friends.");
                break;
            case TAB_RESTAURANT_SEARCH:
                imgHolder.setImageResource(R.drawable.ic_store_mall_directory_black_48dp);
                txtHolder.setText("Find best restaurants.");
                break;
        }



        //if (mPageNumber == TAB_DISH_SEARCH)




        //Log.d("page no", mPageNumber+"");



        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d("onAttach","call");

        activity = getActivity();


        config = new Config();
    }

    @Override
    public void searchKey(String keyword, String searchType) {
        this.keyword = keyword;

        //AppController.getInstance().cancelPendingRequests(TAG);

        if (keyword.length()>2){
            try {
                getDishList(TAG, searchType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





        Log.d("search key", "keyword: "+ keyword+" searchType "+searchType + "session id: "+ sessionId);
    }

    public void getDishList(final String tag, String searchType) throws JSONException {

        Log.d("session Id global", AppController.getInstance().sessionId+"");
        //Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", AppController.getInstance().sessionId);
        obj.put("search", keyword);
        obj.put("region", "delhi");

        Log.d("obj", sessionId+" : "+obj+"");
        //obj.put("latitude","28.4820495");
        // obj.put("longitude","77.0832561");
        //obj.put("includeCount", "1");
        //obj.put("includeFollowed","1");
        // obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        //obj.put("page",Integer.toString(pageNo));
        // obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_DISH_LIST, obj,
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
                return headers;
            }
        };

        final int DEFAULT_TIMEOUT = 6000;
        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq,"gethomefeed");
    }
    private void loadDataIntoView(JSONObject response, String tag) throws JSONException {

        this.response = response;

        JSONArray rListArray = response.getJSONArray("result");
        // Log.d("rListArray", "total: "+ rListArray.length());
        for (int i=0;i<rListArray.length();i++){
            SearchResultObj current = new SearchResultObj();
            current.id = rListArray.getJSONObject(i).getString("id");
            current.dishName = rListArray.getJSONObject(i).getString("dishName");
            current.postCount = rListArray.getJSONObject(i).getString("postCount");
            searchResultList.add(current);
        }
        searchAdapter = new SearchAdapter(AppController.getInstance().context,searchResultList);
        AppController.getInstance().recyclerView.setAdapter(searchAdapter);
        //Log.d("send list", "total: "+restaurantList.size());
        if (getActivity() != null ){

        }else {
            Log.d("getActivity", "null");
        }

        searchResultLoaded = true;
    }
}
