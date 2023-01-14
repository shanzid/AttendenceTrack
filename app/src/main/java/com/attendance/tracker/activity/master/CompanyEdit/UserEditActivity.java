package com.attendance.tracker.activity.master.CompanyEdit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.NewCompanyList.CompanyDetailsActivity;
import com.attendance.tracker.activity.NewCompanyList.NewCompanyListActivity;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.data.JoinResponse;
import com.attendance.tracker.data.ProfileData;
import com.attendance.tracker.data.ProfileList;
import com.attendance.tracker.data.UserList;
import com.attendance.tracker.interfaces.OnDeleteListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
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

public class UserEditActivity extends AppCompatActivity implements OnDeleteListener, OnUserClickListener {
    private RecyclerView userList;
    private ArrayList<ProfileList> userDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    private ProfileAdapterEdit userAdapter;
    private TextView hederName;
    private String userType, userName;
    String latitude, longlitude, address;
    String userId;
    ProgressBar progressBar;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initVariable();
        initView();
        initFunc();
        initListener();

    }

    private void initVariable() {
        Intent mIntent = getIntent();
        userType = mIntent.getStringExtra("userType");
        userName = mIntent.getStringExtra("userName");
        userDataList = new ArrayList<>();
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);


        //  Toast.makeText(this, ""+userName, Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        userDataList = new ArrayList<>();

        if (hasPermissionInManifest(UserEditActivity.this, 1, Manifest.permission.ACCESS_FINE_LOCATION))
            ;
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);

        searchView = findViewById(R.id.search_view);
        userList = findViewById(R.id.rv_userList);
        hederName = findViewById(R.id.hederName);
        progressBar = findViewById(R.id.progress_bar);

    }

    private void initFunc() {

        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);


        if (userName.equals("Company Edit")) {
            getProfileData(userName, userType,"");
            hederName.setText("Company Edit");

        } else if (userName.equals("Edit EmployeeList")) {
            getProfileData(userId, userType,"");
            hederName.setText("EmployeeList");
        } else
            getProfileData(userId, userType,"");

    }

    private void initListener() {

        findViewById(R.id.back).setOnClickListener(view -> finish());

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getProfileData(userId, userType,query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()){
                            getProfileData(userId, userType,newText);
                        }
                        return false;
                    }
                });
    }


    public void getProfileData(String userId, String type,String search) {
        userDataList.clear();
        if (internetConnection.isInternetAvailable(UserEditActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfileList(userId, type,search).enqueue(new Callback<ProfileData>() {
                @Override
                public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            progressBar.setVisibility(View.GONE);

                        } else if (response.body().getError() == 1) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserEditActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfileData> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(layoutManager);
        userAdapter = new ProfileAdapterEdit(this, userDataList);
        userList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
       // userAdapter.SetItemClick(this);
        userAdapter.deleteListener(this);
        userAdapter.SetItemClick(this);
    }

//    @Override
//    public void itemUserClick(View view, int position) {
//
//        if (userName.equals("Leader List")) {
//            Log.w("Not Worked", "Noting Go");
//
//        } else if (userName.equals("Employee List")) {
//            selectLocationOnMap();
//            userId = String.valueOf(position);
//            Log.w("Not Worked", "Noting Go");
//        } else if (userName.equals("Company List")) {
//            Log.w("Not Worked", "Noting Go");
//        } else if (userName.equals("Date")) {
//            Intent mIntent = new Intent(UserEditActivity.this, DateWiseGeoActivity.class);
//            mIntent.putExtra("userId", userDataList.get(position).getId());
//            mIntent.putExtra("type", userType);
//            startActivity(mIntent);
//        } else if (userName.equals("All Employee")) {
//            Intent mIntent = new Intent(UserEditActivity.this, UserGeoListActivity.class);
//            mIntent.putExtra("userId", userDataList.get(position).getId());
//            mIntent.putExtra("type", userType);
//            startActivity(mIntent);
//        } else {
//            //  Intent mIntent = new Intent(UserListActivity.this,ShowGeoReportActivity.class);
//            Intent mIntent = new Intent(UserEditActivity.this, DateWiseGeoActivity.class);
//            mIntent.putExtra("userId", userDataList.get(position).getId());
//            startActivity(mIntent);
//        }
//    }

    private void startMapActivity(String apiKey, String country, String language, String[] supportedAreas, String radius) {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(SimplePlacePicker.API_KEY, apiKey);
        bundle.putString(SimplePlacePicker.COUNTRY, country);
        bundle.putString(SimplePlacePicker.LANGUAGE, language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS, supportedAreas);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS, supportedAreas);
        bundle.putString(SimplePlacePicker.RADIUS, radius);

        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }

    private void selectLocationOnMap() {
        String apiKey = getString(R.string.places_api_key);
        String mCountry = "bgd";
        String mLanguage = "en";
        String radius = "0";
        // String [] mSupportedAreas = mSupportedAreaEt.getText().toString().split(",");
        String[] mSupportedAreas = {"", ""};
        startMapActivity(apiKey, mCountry, mLanguage, mSupportedAreas, radius);
    }

    private void updateDataServer(Intent data) {
        latitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, -1));
        longlitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, -1));
        address = data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
        String radiusData = data.getStringExtra(SimplePlacePicker.RADIUS);


        long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = Long.toString(tsLong);

        submitGeo(userId, timeStamp, latitude + "," + longlitude, radiusData);


//        Toast.makeText(this, latitude+" , "+longlitude, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(this, "radius"+radiusData, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) updateDataServer(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ;
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

    public void submitGeo(String userId, String timestamp, String latlong, String radius) {
        if (internetConnection.isInternetAvailable(UserEditActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitGeo(userId, latlong, radius, timestamp).enqueue(new Callback<GeoSubmitResponse>() {
                @Override
                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(UserEditActivity.this, "" + response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            Log.w("test", "" + response.body().getErrorReport());
                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(UserEditActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
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


    @Override
    public void itemDeleteClick(View view, int position) {
        // this api work only when you login company
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to Delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfileData(userId, userDataList.get(position).getId());
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    // this api work only when you login company
    public void deleteProfileData(String userId, String id) {

        userDataList.clear();
        if (internetConnection.isInternetAvailable(UserEditActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.deleteProfile(userId, id).enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(UserEditActivity.this, "" + response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            getProfileData(userId, userType,"");
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(UserEditActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JoinResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void itemUserClick(View view, int position) {

        if (userName.equals("Edit EmployeeList")){
            Log.w("test","show Companydetails");
        }else {

            Intent mIntent = new Intent(this, CompanyInfoEditActivity.class);
            mIntent.putExtra("UserID", userDataList.get(position).getId());
            startActivity(mIntent);
        }


    }
}