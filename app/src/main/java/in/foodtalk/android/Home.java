package in.foodtalk.android;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import in.foodtalk.android.apicall.PostBookmarkApi;
import in.foodtalk.android.apicall.PostLikeApi;
import in.foodtalk.android.apicall.PostReportApi;
import in.foodtalk.android.communicator.HeadSpannableCallback;
import in.foodtalk.android.communicator.MoreBtnCallback;
import in.foodtalk.android.communicator.PhoneCallback;
import in.foodtalk.android.communicator.PostBookmarkCallback;
import in.foodtalk.android.communicator.PostDeleteCallback;
import in.foodtalk.android.communicator.PostLikeCallback;
import in.foodtalk.android.communicator.PostOptionCallback;
import in.foodtalk.android.communicator.ProfilePostOpenCallback;
import in.foodtalk.android.communicator.ProfileRPostOpenCallback;
import in.foodtalk.android.communicator.UserProfileCallback;
import in.foodtalk.android.communicator.UserThumbCallback;
import in.foodtalk.android.fragment.DiscoverFragment;
import in.foodtalk.android.fragment.FavouritesFragment;
import in.foodtalk.android.fragment.HomeFragment;
import in.foodtalk.android.fragment.MoreFragment;
import in.foodtalk.android.fragment.NewpostFragment;
import in.foodtalk.android.fragment.NotiFragment;
import in.foodtalk.android.fragment.OpenPostFragment;
import in.foodtalk.android.fragment.OpenRPostFragment;
import in.foodtalk.android.fragment.OptionsFragment;
import in.foodtalk.android.fragment.RestaurantProfileFragment;
import in.foodtalk.android.fragment.UserProfile;
import in.foodtalk.android.fragment.WebViewFragment;
import in.foodtalk.android.module.DatabaseHandler;
import in.foodtalk.android.module.Login;
import in.foodtalk.android.object.RestaurantPostObj;
import in.foodtalk.android.object.UserPostObj;

public class Home extends AppCompatActivity implements View.OnClickListener,
        PostLikeCallback, PostBookmarkCallback, PostOptionCallback, PostDeleteCallback,
        MoreBtnCallback, UserProfileCallback, ProfilePostOpenCallback, FragmentManager.OnBackStackChangedListener,
        HeadSpannableCallback, UserThumbCallback, ProfileRPostOpenCallback, PhoneCallback {

    DatabaseHandler db;
    LinearLayout btnHome, btnDiscover, btnNewPost, btnNotifications, btnMore;
    ImageView homeIcon, discoverIcon, newpostIcon, notiIcon, moreIcon;
    TextView txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon;

    private ImageView[] icons;
    private TextView[] txtIcons;
    private int[] imgR;
    private int[] imgRA;
    private LinearLayout btnLogout;


    HomeFragment homeFragment;
    DiscoverFragment discoverFragment;
    NewpostFragment newpostFragment;
    NotiFragment notiFragment;
    MoreFragment moreFragment;
    OpenPostFragment openPostFragment;
    OptionsFragment optionsFragment;
    WebViewFragment webViewFragment;
    FavouritesFragment favouritesFragment;
    RestaurantProfileFragment restaurantProfileFragment;
    OpenRPostFragment openRPostFragment;

    UserProfile userProfile;

    PostLikeApi postLikeApi;
    PostBookmarkApi postBookmarkApi;
    PostReportApi postReportApi;

    Dialog dialogPost;
    LinearLayout alertReport;
    LinearLayout alertDelete;
    LinearLayout actionBtns;
    TextView btnReportAlertNo;
    TextView btnReportAlertYes;
    TextView btnDeleteAlertNo;
    TextView btnDeleteAlertYes;

    String sessionId;
    String userId;
    String currentPostUserId;
    String currentPostId;

    TextView titleHome;
    TextView subTitleHome;
    TextView titleHome1;

    RelativeLayout header;
    RelativeLayout header1;
    int pageNo;

    private final int USER_PROFILE = 1;
    private final int DISH = 2;
    private final int RESTAURANT_PROFILE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getApplicationContext());

        //-------api init--------------------------
        postLikeApi = new PostLikeApi(this);
        postBookmarkApi = new PostBookmarkApi(this);
        postReportApi = new PostReportApi(this);
        //-----------------------------------------


        setContentView(R.layout.activity_home);

        header = (RelativeLayout) findViewById(R.id.header);
        header1 = (RelativeLayout) findViewById(R.id.header1);


        subTitleHome = (TextView) findViewById(R.id.subtitle);
        titleHome1 = (TextView) findViewById(R.id.title_home1);

        btnLogout = (LinearLayout) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        btnHome = (LinearLayout) findViewById(R.id.btn_home);
        btnDiscover = (LinearLayout) findViewById(R.id.btn_discover);
        btnNewPost = (LinearLayout) findViewById(R.id.btn_newpost);
        btnNotifications = (LinearLayout) findViewById(R.id.btn_notification);
        btnMore = (LinearLayout) findViewById(R.id.btn_more);

        homeIcon = (ImageView) findViewById(R.id.home_icon);
        discoverIcon = (ImageView) findViewById(R.id.discover_icon);
        newpostIcon = (ImageView) findViewById(R.id.newpost_icon);
        notiIcon = (ImageView) findViewById(R.id.noti_icon);
        moreIcon = (ImageView) findViewById(R.id.more_icon);

        txtHomeIcon = (TextView) findViewById(R.id.home_txt_icon);
        txtDiscoverIcon = (TextView) findViewById(R.id.discover_txt_icon);
        txtNewpostIcon = (TextView) findViewById(R.id.newpost_txt_icon);
        txtNotiIcon = (TextView) findViewById(R.id.noti_txt_icon);
        txtMoreIcon = (TextView) findViewById(R.id.more_txt_icon);

        titleHome = (TextView) findViewById(R.id.title_home);

        //-----------
        icons = new ImageView[]{homeIcon, discoverIcon, newpostIcon, notiIcon, moreIcon};
        txtIcons = new TextView[]{txtHomeIcon, txtDiscoverIcon, txtNewpostIcon, txtNotiIcon, txtMoreIcon};
        imgR = new int[]{R.drawable.home, R.drawable.discover, R.drawable.newpost, R.drawable.notifications, R.drawable.more};
        imgRA = new int[]{R.drawable.home_active, R.drawable.discover_active, R.drawable.newpost_active, R.drawable.notifications_active, R.drawable.more_active};
        //----

        btnDiscover.setOnClickListener(this);
        btnNewPost.setOnClickListener(this);
        btnNotifications.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        // Log.d("getInfo",db.getRowCount()+"");
        // Log.d("get user info", db.getUserDetails().get("userName")+"");

        // Log.d("get user info", "session id: "+db.getUserDetails().get("sessionId"));
        // Log.d("get user info", "user id: "+db.getUserDetails().get("userId"));
        //Log.d("get user info", "full name: "+db.getUserDetails().get("fullName"));
        //Log.d("get user info", "user name: "+db.getUserDetails().get("userName"));

        userId = db.getUserDetails().get("userId");
        sessionId = db.getUserDetails().get("sessionId");

        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        newpostFragment = new NewpostFragment();
        notiFragment = new NotiFragment();
        moreFragment = new MoreFragment();

        optionsFragment = new OptionsFragment();
        favouritesFragment = new FavouritesFragment();


        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment).commit();
        pageNo = 0;

        getFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                Log.d("onClick", "btn home");

                if (pageNo != 0) {
                    setFragmentView(homeFragment, R.id.container, 0, false);
                    titleHome.setText("Home");
                    pageNo = 0;
                }
                break;
            case R.id.btn_discover:
                Log.d("onClick", "btn discover");
                if (pageNo != 1) {
                    setFragmentView(discoverFragment, R.id.container, 1, false);
                    titleHome.setText("Discover");
                    pageNo = 1;
                }
                break;
            case R.id.btn_newpost:
                if (pageNo != 2) {
                    setFragmentView(newpostFragment, R.id.container, 2, false);
                    titleHome.setText("New Post");
                    pageNo = 2;
                }
                Log.d("onClick", "btn newpost");
                break;
            case R.id.btn_notification:
                if (pageNo != 3) {
                    setFragmentView(notiFragment, R.id.container, 3, false);
                    titleHome.setText("Notification");
                    pageNo = 3;
                }

                Log.d("onClick", "btn notification");
                break;
            case R.id.btn_more:
                if (pageNo != 4) {
                    setFragmentView(moreFragment, R.id.container, 4, false);
                    titleHome.setText("More");
                    pageNo = 4;
                }
                Log.d("onClick", "btn more");
                break;
            case R.id.btn_logout:
                Log.d("btn clicked", "logout");
                logOut();
                break;
        }
    }

    private void setFragmentView(Fragment newFragment, int container, int pageN, boolean bStack) {
        String backStateName = newFragment.getClass().getName();

        if (newFragment == userProfile) {
            header.setVisibility(View.GONE);
            header1.setVisibility(View.VISIBLE);
        } else if (newFragment == openPostFragment) {
            header.setVisibility(View.GONE);
            header1.setVisibility(View.VISIBLE);
        } else {
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
        }
        //-------set Header-------
        /*if (backStateName.equals("userProfile")){
            header.setVisibility(View.GONE);
            header1.setVisibility(View.VISIBLE);
        }else {
            header1.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
        }*/
        //---------------------------


        Log.d("backStatename", backStateName);
        icons[pageNo].setImageResource(imgR[pageNo]);
        icons[pageN].setImageResource(imgRA[pageN]);
        txtIcons[pageN].setTextColor(getResources().getColor(R.color.icon_txt_active));
        txtIcons[pageNo].setTextColor(getResources().getColor(R.color.icon_txt));
        //icons[pageN].setImageResource(R.drawable.home);
        // Create new fragment and transaction
        Fragment discoverF = new DiscoverFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed

        transaction.replace(container, newFragment);
        if (bStack) {
            transaction.addToBackStack(backStateName);
        }
        // Commit the transaction
        transaction.commit();


    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
            String fName = f.getClass().getSimpleName();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        Fragment f = this.getFragmentManager().findFragmentById(R.id.container);
        String fName = f.getClass().getSimpleName();
        Log.d("onBackStackChanged", f.getClass().getSimpleName());
        if (!fName.equals("UserProfile")) {
            header.setVisibility(View.VISIBLE);
            header1.setVisibility(View.GONE);
        }
        switch (fName) {
            case "UserProfile":
                header.setVisibility(View.VISIBLE);
                header1.setVisibility(View.GONE);
                break;
            case "MoreFragment":
                titleHome.setText("More");
                break;
            case "FavouritesFragment":
                titleHome.setText("Favourites");
                break;
        }
    }


    private void logOut() {
        db.resetTables();
        Intent i = new Intent(this, FbLogin.class);
        startActivity(i);
        finish();
    }

    @Override
    public void like(int position, String postId, Boolean like) {
        Log.d("likeResponse", position + " postid: " + postId);
        try {
            postLikeApi.postLike(postId, like);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // discoverFragment.recyclerView.smoothScrollToPosition(5);
    }

    @Override
    public void bookmark(int position, String postId, Boolean bookmark) {
        //Log.d("bookmark", "position"+ position);
        try {
            postBookmarkApi.postBookmark(postId, bookmark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void option(int position, String postId, String userId) {
        Log.d("option callback", "post id: " + postId);
        showDialog(postId, userId);
    }

    //String phone1;
    // String phone2;
    private void callDialog(final String phone1, final String phone2) {
        final Dialog dialogCall = new Dialog(this);
        dialogCall.setContentView(R.layout.dialog_call);

        //this.phone1 = phone1;
        // this.phone2 = phone2;

        final TextView txtPhone1 = (TextView) dialogCall.findViewById(R.id.txt_phone1);
        TextView txtPhone2 = (TextView) dialogCall.findViewById(R.id.txt_phone2);

        TextView btnClose = (TextView) dialogCall.findViewById(R.id.btn_call_dialog_close);


        if (!phone1.equals("")) {
            txtPhone1.setText(phone1);
        } else {
            txtPhone1.setVisibility(View.GONE);
        }
        if (!phone2.equals("")) {
            txtPhone2.setText(phone2);
        } else {
            txtPhone2.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCall.dismiss();
            }
        });

        txtPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call", phone1);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone1));
                //startActivity(callIntent);
            }
        });
        txtPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call", phone2);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone2));
               // startActivity(callIntent);
            }
        });
        dialogCall.show();
    }
    private void showDialog(String postId, final String userId){

        currentPostUserId = userId;
        currentPostId = postId;
        dialogPost = new Dialog(this);
        dialogPost.setContentView(R.layout.dialog_post);

        TextView btnReport = (TextView) dialogPost.findViewById(R.id.btn_report_post);
        TextView btnCancel = (TextView) dialogPost.findViewById(R.id.btn_cancel_post);
        TextView btnDelete = (TextView) dialogPost.findViewById(R.id.btn_delete_post);
        alertReport = (LinearLayout) dialogPost.findViewById(R.id.alert_report);
        alertDelete = (LinearLayout) dialogPost.findViewById(R.id.alert_delete);
        actionBtns = (LinearLayout) dialogPost.findViewById(R.id.action_btns_dialog);

        btnReportAlertNo = (TextView) dialogPost.findViewById(R.id.btn_report_alert_no);
        btnReportAlertYes = (TextView) dialogPost.findViewById(R.id.btn_report_alert_yes);

        btnDeleteAlertNo = (TextView) dialogPost.findViewById(R.id.btn_delete_alert_no);
        btnDeleteAlertYes = (TextView) dialogPost.findViewById(R.id.btn_delete_alert_yes);

        alertReport.setVisibility(View.GONE);
        alertDelete.setVisibility(View.GONE);

        if(this.userId.equals(userId)){
            btnDelete.setVisibility(View.VISIBLE);
            btnReport.setVisibility(View.GONE);
        }else {
            btnDelete.setVisibility(View.GONE);
            btnReport.setVisibility(View.VISIBLE);
        }

        dialogPost.show();
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","btnReport "+currentPostUserId);
                actionBtns.setVisibility(View.GONE);
                alertReport.setVisibility(View.VISIBLE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("dialog","btnReport");
                dialogPost.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","btn delete click");
                actionBtns.setVisibility(View.GONE);
                alertDelete.setVisibility(View.VISIBLE);
            }
        });
        btnReportAlertNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick", "reportAlertNo");
                dialogPost.dismiss();
            }
        });
        btnReportAlertYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick", "reportAlertYes");
                dialogPost.dismiss();
                try {
                    postReportApi.postReport(sessionId ,currentPostId, "report");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnDeleteAlertNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnClick","DeleteAlertNo");
                dialogPost.dismiss();
            }
        });
        btnDeleteAlertYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPost.dismiss();
                try {
                    postReportApi.postReport(sessionId ,currentPostId, "delete");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("btnClick", "DeleteAlertYes");
            }
        });
    }
    @Override
    public void postDelete() {
        Log.d("postDelete","update recyclerview");
        try {
            homeFragment.getPostFeed("refresh");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void btnClick(String type, int position) {
        Log.d("more btn clicked", type+" position: "+ position);
        /*if(type.equals("profile")){
            setFragmentView(userProfile, R.id.container, 4, true);
        }
        if(type.equals("options")){
            setFragmentView(optionsFragment, R.id.container, 4, true);
        }*/
       switch (type){
           case "profile":
               userProfile = new UserProfile(userId);
               setFragmentView(userProfile, R.id.container, 4, true);
               break;
           case "options":
               setFragmentView(optionsFragment, R.id.container, 4, true);
               break;
           case "legal":
               Log.d("btn click","setFragment webview");
               webViewFragment = new WebViewFragment("http://www.foodtalkindia.com/document.html");
               setFragmentView (webViewFragment, R.id.container, 4, true);
               titleHome.setText("Legal");
               break;
           case "favourites":
               setFragmentView(favouritesFragment, R.id.container, 4, true);
               break;
       }

    }
    @Override
    public void getUserInfo(String points, String userName) {
        //subTitleHome.setText(points);
        subTitleHome.setText(points+" Points");
        titleHome1.setText(userName);
        //Log.d("points", points);
    }
    @Override
    public void postOpen(List<UserPostObj> postObj, String postId, String userId) {
        openPostFragment = new OpenPostFragment(postObj, postId, userId);
        setFragmentView (openPostFragment, R.id.container1, 4, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }

    @Override
    public void spannableTxt(String userId, String checkinRestaurantId, String dishName, int viewType) {
        switch (viewType){
            case USER_PROFILE:
                userProfile = new UserProfile(userId);
                setFragmentView(userProfile, R.id.container, 0, true);
                break;
            case RESTAURANT_PROFILE:
                restaurantProfileFragment = new RestaurantProfileFragment(checkinRestaurantId);
                setFragmentView(restaurantProfileFragment, R.id.container, 0, true);
                Log.d("clicked","for restaurant");
                break;
            case DISH:
                Log.d("clicked","for Dish");
                break;
        }
    }

    @Override
    public void thumbClick(String userId) {
        userProfile = new UserProfile(userId);
        setFragmentView(userProfile, R.id.container, 0, true);
    }

    @Override
    public void rPostOpen(List<RestaurantPostObj> postObj, String postId, String restaurantId) {
        openRPostFragment = new OpenRPostFragment(postObj, postId, restaurantId);
        setFragmentView (openRPostFragment, R.id.container1, 4, true);
        Log.d("postOpen","userId: "+userId+" postId: "+postId);
    }

    @Override
    public void phoneBtn(String phone1, String phone2) {
        Log.d("phone numbers", phone1+" : "+phone2);
        callDialog(phone1, phone2);
    }
}