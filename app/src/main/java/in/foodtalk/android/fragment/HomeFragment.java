package in.foodtalk.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.foodtalk.android.FbLogin;
import in.foodtalk.android.R;
import in.foodtalk.android.adapter.HomeFeedAdapter;
import in.foodtalk.android.app.AppController;
import in.foodtalk.android.app.Config;
import in.foodtalk.android.communicator.NewPostCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.EndlessRecyclerOnScrollListener;
import in.foodtalk.android.module.UserAgent;
import in.foodtalk.android.object.PostObj;

/**
 * Created by RetailAdmin on 21-04-2016.
 */
public class HomeFragment extends Fragment {
    View layout;
    DatabaseHandler db;
    Config config;
    PostObj postObj;
    HomeFeedAdapter homeFeedAdapter;
    List<PostObj> postData = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;

    PostLikeCallback likeCallback;

    RecyclerView recyclerView;

    PostLikeCallback postLikeCallback;

    SwipeRefreshLayout swipeRefreshHome;

    LinearLayoutManager linearLayoutManager;

    ProgressBar homeProgress;


    private int pageNo = 1;

    LinearLayout tapToRetry;

    Activity activity;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.home_fragment, container, false);
        //postLikeCallback = this;
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_home);
        swipeRefreshHome = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshHome);
        // use a linear layout manager

        tapToRetry = (LinearLayout) layout.findViewById(R.id.tap_to_retry);

        homeProgress = (ProgressBar) layout.findViewById(R.id.home_progress);

        if(postData != null){
            Log.d("postData","size: "+ postData);
        }else {
            Log.d("postData","null");
        }
        swipeRefreshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swip to refresh home", "Refreshing");
                try {
                    pageNo = 1;
                    getPostFeed("refresh");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pageNo = 1;
                    homeProgress.setVisibility(View.VISIBLE);
                    tapToRetry.setVisibility(View.GONE);
                    getPostFeed("load");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return layout;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        if (getActivity() != null){
            activity = getActivity();
        }

        linearLayoutManager = new LinearLayoutManager(activity);

        mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        config = new Config();
        db = new DatabaseHandler(activity.getApplicationContext());
        postObj = new PostObj();
       Log.d("get user info F", db.getUserDetails().get("sessionId"));
        Log.d("get user info F", db.getUserDetails().get("userId"));
        try {
            getPostFeed("load");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        Log.d("Fragment","onDestryView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("Fragment","onDestroy");
        super.onDestroy();
    }



    @Override
    public void onDetach() {
        Log.d("Fragment","onDetach");
        super.onDetach();
    }
    Parcelable mListState;

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
//        mListState = mLayoutManager.onSaveInstanceState();
      //  state.putParcelable("myState", mListState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Retrieve list state and list/item positions

       // if(savedInstanceState != null)
          //  mListState = savedInstanceState.getParcelable("myState");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    public void getPostFeed(final String tag) throws JSONException {



        Log.d("getPostFeed", "post data");
        JSONObject obj = new JSONObject();
        obj.put("sessionId", db.getUserDetails().get("sessionId"));
        obj.put("includeCount", "1");
        obj.put("includeFollowed","1");
        obj.put("postUserId",db.getUserDetails().get("userId"));
        //Log.d("getPostFeed","pageNo: "+pageNo);
        obj.put("page",Integer.toString(pageNo));
        obj.put("recordCount","10");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                config.URL_POST_LIST, obj,
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
                                    logOut();
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
                showToast("Please check your internet connection");

                if (tag.equals("load")){
                    tapToRetry.setVisibility(View.VISIBLE);
                    homeProgress.setVisibility(View.GONE);
                }


                if(tag.equals("refresh")){
                    swipeRefreshHome.setRefreshing(false);
                }
                if(tag.equals("loadMore")){
                    remove(null);
                    //callScrollClass();
                    pageNo--;
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
    private void loadDataIntoView(JSONObject response , String tag) throws JSONException {

        swipeRefreshHome.setRefreshing(false);

        homeProgress.setVisibility(View.GONE);

        JSONArray postArray = response.getJSONArray("posts");
        JSONObject postObject = postArray.getJSONObject(0);
        //String userName = postObject.getString("userName");
        //Log.d("user name from post", userName);
        Log.d("check list array", postData.size()+"");
        if (postData.size() > 0 && !tag.equals("loadMore")){
            postData.clear();
            Log.d("loadData: ","clear post data");
        }

        for (int i=0; postArray.length()>i;i++){
            //String userName = postArray.getJSONObject(i).getString("userName");
            //PostObj current = new PostObj();
            PostObj current = new PostObj();
            current.id = postArray.getJSONObject(i).getString("id");
            current.userName = postArray.getJSONObject(i).getString("userName");
            current.userId = postArray.getJSONObject(i).getString("userId");
            current.dishName = postArray.getJSONObject(i).getString("dishName");
            current.restaurantName = postArray.getJSONObject(i).getString("restaurantName");
            current.checkedInRestaurantId = postArray.getJSONObject(i).getString("checkedInRestaurantId");
            current.createDate = postArray.getJSONObject(i).getString("createDate");
            current.currentDate = postArray.getJSONObject(i).getString("currentDate");
            current.likeCount = postArray.getJSONObject(i).getString("likeCount");
            current.bookmarkCount = postArray.getJSONObject(i).getString("bookmarkCount");
            current.commentCount = postArray.getJSONObject(i).getString("commentCount");
            current.userThumb = postArray.getJSONObject(i).getString("userThumb")+"?type=large";
            current.userImage = postArray.getJSONObject(i).getString("userImage");
            current.postImage = postArray.getJSONObject(i).getString("postImage");
            current.postThumb = postArray.getJSONObject(i).getString("postThumb");
            current.iLikedIt = postArray.getJSONObject(i).getString("iLikedIt");
            current.iBookark = postArray.getJSONObject(i).getString("iBookark");
            current.rating = postArray.getJSONObject(i).getString("rating");
            current.restaurantIsActive = postArray.getJSONObject(i).getString("restaurantIsActive");
            current.region = postArray.getJSONObject(i).getString("region");
           // postData.clear();
            postData.add(current);
            Log.d("dish name", postData.get(i).userId);
        }
        //postData = (List<PostObj>) postObj;
        if (tag.equals("load")){

            if(getActivity() != null){
                homeFeedAdapter = new HomeFeedAdapter(getActivity(), postData, postLikeCallback);
                recyclerView.setAdapter(homeFeedAdapter);
            }
            else {
                Log.d("homefeedadapter call", "getActivity null");
            }

            // recyclerView.invalidate();

            callScrollClass();

            Log.d("Response LoadData", "Load");

        }else if (tag.equals("refresh")){
           // homeFeedAdapter.clear();
            //Log.d("on update","postData size: " +postData.size());
            //homeFeedAdapter.addAll(postData);
            callScrollClass();
            homeFeedAdapter.notifyDataSetChanged();
            Log.d("Response LoadData", "Refresh - size: "+ postData.size());
        } else if(tag.equals("loadMore")){
            //postData.add(postData);
            //recyclerView.addD
            //homeFeedAdapter.addAll(postData);
            //postData.remove(postData.size() - 1);
           // homeFeedAdapter.notifyItemRemoved(postData.size()-1);
            remove(null);
            loading = false;
            Log.d("Response LoadData", "LoadMore: postSize"+postData.size()+" dishname: "+postData.get(0).dishName);
           // homeFeedAdapter.notifyDataSetChanged();
            //homeFeedAdapter.notifyItemInserted(postData.size());
        }
        //homeFeedAdapter.notifyDataSetChanged();
    }
    public void remove(ContactsContract.Contacts.Data data) {
        int position = postData.indexOf(data);
        postData.remove(position);
        homeFeedAdapter.notifyItemRemoved(position);
    }
    Boolean loading = false;
    private void callScrollClass(){
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager, null) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("scroll listener", "current_page: "+ current_page);
                if(!loading){
                    pageNo++;
                    postData.add(null);
                    //recyclerView.addD
                    homeFeedAdapter.notifyItemInserted(postData.size()-1);
                    loading = true;
                    Log.d("loadMore", "call getPostFeed('loadMore')");
                    try {
                        getPostFeed("loadMore");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onScrolled1(int dx, int dy, int firstVisibleItem, int lastVisibleItem) {

            }
        });
    }
    private void logOut(){
        db.resetTables();
        LoginManager.getInstance().logOut();
        Intent i = new Intent(activity.getApplicationContext(), FbLogin.class);
        startActivity(i);
        activity.finish();
    }
    public void showToast(String msg){
        Toast toast= Toast.makeText(activity,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }

    public NewPostCallback newPostCallback = new NewPostCallback() {
       @Override
       public void onPostCreated(String status) {
            Log.d("onPostCreated", status);
           try {
               pageNo = 1;
               getPostFeed("refresh");

           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
    };
    public void scrollToTop(){
        Log.d("scroll position", recyclerView.getScrollY()+"");
        recyclerView.smoothScrollToPosition(0);
    }
}