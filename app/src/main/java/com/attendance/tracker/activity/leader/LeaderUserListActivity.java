package com.attendance.tracker.activity.leader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.MapsActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.adapter.UserAdapter;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.data.UserData;
import com.attendance.tracker.data.UserList;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.attendance.simpleplacepicker.MapActivity;
import com.attendance.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderUserListActivity extends AppCompatActivity implements OnUserClickListener {
    RecyclerView userList;
    UserAdapter userAdapter;
    List<UserList> userDataList ;
    String userId = "";
    String radius = "";
    String latitude,longlitude ,address;
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    android.app.AlertDialog popupPaymentMethod = null;
    TextView title;
    private String userType, userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_user_list);
        initVariable();
        initViews();
        initFunction();
        initListener();
    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    private void initViews(){
        userList = findViewById(R.id.rv_userList);
        title = findViewById(R.id.title);
    }


    private void initVariable() {
        userDataList = new ArrayList<>();
      Intent mIntent = getIntent();
//        userType = mIntent.getStringExtra("userType");
        userName = mIntent.getStringExtra("userName");
        if (hasPermissionInManifest(LeaderUserListActivity.this,1, Manifest.permission.ACCESS_FINE_LOCATION));
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);

    }

    private void initFunction() {
        if (userName.equals("Employee List")) {
            getUserData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));}



    }
    private void logout() {
        appSessionManager.logoutUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private void gotoMaps() {
        startActivity(new Intent(this, MapsActivity.class));
    }


    @Override
    public void itemUserClick(View view, int position) {
        //
        selectLocationOnMap();
        userId = String.valueOf(position);
    }

    private void startMapActivity(String apiKey, String country, String language, String[]supportedAreas,String radius){
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(SimplePlacePicker.API_KEY,apiKey);
        bundle.putString(SimplePlacePicker.COUNTRY,country);
        bundle.putString(SimplePlacePicker.LANGUAGE,language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,supportedAreas);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,supportedAreas);
        bundle.putString(SimplePlacePicker.RADIUS,radius);

        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }

    private void selectLocationOnMap() {
        String apiKey = getString(R.string.places_api_key);
        String mCountry = "bgd";
        String mLanguage = "en";
        String radius = "0";
        // String [] mSupportedAreas = mSupportedAreaEt.getText().toString().split(",");
        String [] mSupportedAreas = {"",""};
        startMapActivity(apiKey,mCountry,mLanguage,mSupportedAreas,radius);
    }

    private void updateDataServer(Intent data){
        latitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
        longlitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
        address = data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
        String radiusData = data.getStringExtra(SimplePlacePicker.RADIUS);


        long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = Long.toString(tsLong);

        submitGeo(userId,timeStamp,latitude+","+longlitude,radiusData);


//        Toast.makeText(this, latitude+" , "+longlitude, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(this, "radius"+radiusData, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null) updateDataServer(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            //     selectLocationOnMap();
        }
    }

    //check for location permission
    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String permissionName) {
        if (ContextCompat.checkSelfPermission(activity,
                permissionName)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{permissionName},
                    requestCode);
        } else {
            return true;
        }
        return false;
    }

    public void submitGeo(String userId,String timestamp,String latlong,String radius){
        if (internetConnection.isInternetAvailable(LeaderUserListActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitGeo(userId,latlong,radius,timestamp).enqueue(new Callback<GeoSubmitResponse>() {
                @Override
                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(LeaderUserListActivity.this, ""+response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            Log.w("test",""+response.body().getErrorReport());
                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(LeaderUserListActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoSubmitResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getUserData(String userId){
        userDataList.clear();
        if (internetConnection.isInternetAvailable(LeaderUserListActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getUserList(userId).enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(Call<UserData> call, Response<UserData> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(LeaderUserListActivity.this, ""+response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserData> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(userDataList,this);
        userList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
        userAdapter.setClickListener(this);
    }

}