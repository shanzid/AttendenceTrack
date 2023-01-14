package com.attendance.tracker.activity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.GeoReportMapActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.adapter.GeoFanceReportAdapter;
import com.attendance.tracker.data.GeoFanceReport;
import com.attendance.tracker.data.GeoFanceReportList;
import com.attendance.tracker.data.JoinResponse;
import com.attendance.tracker.interfaces.OnDeleteListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserGeoListActivity extends AppCompatActivity implements OnDeleteListener, OnUserClickListener {
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    ArrayList<GeoFanceReportList> geoReportLists = new ArrayList<>();
    GeoFanceReportAdapter geoFanceReportAdapter;
    RecyclerView rv_geoList;
    String LoginUserID, userId,type;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_geo_list);
        initVariables();
        initView();
        initFunc();
        initListener();


    }

    private void initVariables() {
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
        LoginUserID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        Intent mIntent = getIntent();
        userId = mIntent.getStringExtra("userId");
        type = mIntent.getStringExtra("type");
    }

    private void initView() {
        rv_geoList = findViewById(R.id.rv_geoList);
    }

    private void initFunc() {
    getGeoLocation(userId);
    }

    private void initListener() {

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    public void getGeoLocation(String userId){
        geoReportLists.clear();
        if (internetConnection.isInternetAvailable(UserGeoListActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getAllGeo(userId).enqueue(new Callback<GeoFanceReport>() {
                @Override
                public void onResponse(Call<GeoFanceReport> call, Response<GeoFanceReport> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            geoReportLists.addAll(response.body().getReport());
                            LoadData();
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(UserGeoListActivity.this, "No data Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoFanceReport> call, Throwable t) {
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
        rv_geoList.setHasFixedSize(true);
        rv_geoList.setLayoutManager(layoutManager);
        geoFanceReportAdapter = new GeoFanceReportAdapter(geoReportLists,this);
        rv_geoList.setAdapter(geoFanceReportAdapter);
        geoFanceReportAdapter.notifyDataSetChanged();
        geoFanceReportAdapter.setClickListener(this);
        geoFanceReportAdapter.setItemClickListener(this);
    }


    @Override
    public void itemDeleteClick(View view, int position) {
        deleteGeoData(LoginUserID,geoReportLists.get(position).getId());
    }

    public void deleteGeoData(String LoginUserID,String id){
        if (internetConnection.isInternetAvailable(UserGeoListActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.deleteGeo(LoginUserID,id).enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(UserGeoListActivity.this, ""+response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            getGeoLocation(userId);
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(UserGeoListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
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
        String currentString = geoReportLists.get(position).getGeo();
        String[] separated = currentString.split(",");
        String lat = separated[0];
        String lng = separated[1];

        Intent mIntent = new Intent(this, GeoReportMapActivity.class);
        mIntent.putExtra("latitude",lat);
        mIntent.putExtra("longtitude",lng);
        startActivity(mIntent);


    }
}