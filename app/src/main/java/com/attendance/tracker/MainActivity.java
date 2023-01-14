package com.attendance.tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.activity.leader.LeaderProfileActivity;
import com.attendance.tracker.activity.Task.TaskActivity;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.activity.leader.LeaderUserListActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.activity.user.MapTestUserActivity;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity{
    AppSessionManager appSessionManager;
    TextView userName,usermobile;
    String userId,userType;
    CircleImageView imgview;
    CheckInternetConnection internetConnection;
    MapTestUserActivity.LocationService mLocationService = new MapTestUserActivity.LocationService();
    Intent mServiceIntent;
    String getLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariable();
        initView();
        iniFunction();
        initListener(); }


    private void initVariable() {

        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
        mServiceIntent = new Intent(this, mLocationService.getClass());
    }

    private void initView() {
        userName =findViewById(R.id.userName);
        usermobile =findViewById(R.id.userMobile);
        imgview =findViewById(R.id.img);
    }

    private void iniFunction() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        userName.setText(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        userType = appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY);
        getData(userId);
    }

    private void initListener() {
        findViewById(R.id.cvLeaderAttendance).setOnClickListener(view -> gotoLeaderAttendance());
        findViewById(R.id.cvEmployeeAtt4).setOnClickListener(view -> gotoEmpAttendance());
        findViewById(R.id.cvEmpList).setOnClickListener(view -> gotoUserList());
        findViewById(R.id.cvDataWize).setOnClickListener(view -> gotoDateWizeList());
        findViewById(R.id.cvSupport).setOnClickListener(view -> gotoSupport());
        findViewById(R.id.logout).setOnClickListener(view -> logout());
        findViewById(R.id.cvGeoReport).setOnClickListener(view -> gotoGeoUserList());
        findViewById(R.id.cvTask).setOnClickListener(view -> task());
        findViewById(R.id.cv_chnagepass).setOnClickListener(view -> ChnagePass());
        findViewById(R.id.profiledetails).setOnClickListener(view -> ProfileDetails());
        findViewById(R.id.cvShareApp).setOnClickListener(view -> shareApp());
    }

    private void ProfileDetails() {
        Intent mIntent = new Intent(getApplicationContext(), LeaderProfileActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

    private void shareApp() {
        getLink = "https://artificial-soft.com/"+userId;
/*        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://artificial-soft.com/"))
                .setDomainUriPrefix("https://artisoft.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();*/

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://artisoft.page.link/")
                .setLink(Uri.parse(getLink))
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.attendance.tracker")
                                //.setMinimumVersion(125)
                                .build())

                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Tracking");
                                assert shortLink != null;
                                getLink= shortLink.toString();
                                shareIntent.putExtra(Intent.EXTRA_TEXT, getLink);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch(Exception e) {
                                //e.toString();
                            }
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

    private void ChnagePass() {
        Intent mIntent = new Intent(getApplicationContext(), ChangePassActivity.class);
        mIntent.putExtra("userName", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        mIntent.putExtra("userPass", appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD));
        startActivity(mIntent);


}

    private void task() {
        Intent mIntent = new Intent(this, TaskActivity.class);
        mIntent.putExtra("userId",appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        mIntent.putExtra("userType","0");
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        mIntent.putExtra("EmployeeID", userId);
        startActivity(mIntent);
    }

    private void gotoDateWizeList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType","0");
        mIntent.putExtra("userName","Date Wise Attendance");
        startActivity(mIntent);

    }


    private void gotoGeoUserList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType","0");
        mIntent.putExtra("userName","All Employee");
        startActivity(mIntent);

    }



    private void gotoSupport() {
        Intent intent=new Intent(getApplicationContext(), SupportActivity.class);
        startActivity(intent);
    }



    private void gotoUserList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType","0");
        mIntent.putExtra("userName","Employee List");
        startActivity(mIntent);


    }

    private void gotoLeaderAttendance() {
        Intent mIntent = new Intent(MainActivity.this, MapTestUserActivity.class);
       // mIntent.putExtra("userId",appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);

    }

    private void gotoEmpAttendance() {
        Intent mIntent = new Intent(MainActivity.this,LeaderUserListActivity.class);
        mIntent.putExtra("userType","0");
        mIntent.putExtra("userName","Employee List");
        startActivity(mIntent);

    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Logout App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appSessionManager.logoutUser();
                        stopService(mServiceIntent);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void getData(String userID) {

        if (internetConnection.isInternetAvailable(MainActivity.this)) {
            //  progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfiledetails(userID).enqueue(new Callback<ProfileDetailsData>() {
                @Override
                public void onResponse(Call<ProfileDetailsData> call, Response<ProfileDetailsData> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful())
                            //  progressBar.setVisibility(View.GONE);
                            dialog.dismiss();

                        usermobile.setText(response.body().getMobile());
                        Glide.with(getApplicationContext())
                                .load(BuildConfig.BASE_URL+ "" + response.body().getPhoto())
                                .into(imgview);
                    }
                }


                @Override
                public void onFailure(Call<ProfileDetailsData> call, Throwable t) {
                    t.printStackTrace();
                    //  progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

}