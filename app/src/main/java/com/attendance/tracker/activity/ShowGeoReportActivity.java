package com.attendance.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.adapter.GeoAttendanceAdapter;
import com.attendance.tracker.data.GeoReport;
import com.attendance.tracker.data.GeoReportList;
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

public class ShowGeoReportActivity extends AppCompatActivity  {
    RecyclerView reportList;
    GeoAttendanceAdapter geoAttendanceAdapter;
    ArrayList<GeoReportList> geoReportLists = new ArrayList<>();
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;

    String userId ;
    DatePickerDialog picker;
    TextView tv_start_date,tv_end_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_geo_report);

        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();

        initVariable();
        initView();
        initListener();
        initFunc();

    }

    private void initVariable() {
        Intent mIntent = getIntent();
        userId = mIntent.getStringExtra("userId");
      //  Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    private void initFunc() {
        getGeoReport(userId);
    }

    public void getGeoReport(String userId){
        geoReportLists.clear();
        if (internetConnection.isInternetAvailable(ShowGeoReportActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.showGeoReport(userId).enqueue(new Callback<GeoReport>() {
                @Override
                public void onResponse(Call<GeoReport> call, Response<GeoReport> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            geoReportLists.addAll(response.body().getReport());
                            LoadData();
                            dialog.dismiss();

                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(ShowGeoReportActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoReport> call, Throwable t) {
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
        reportList = findViewById(R.id.reportList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        reportList.setHasFixedSize(true);
        reportList.setLayoutManager(layoutManager);
        geoAttendanceAdapter = new GeoAttendanceAdapter(geoReportLists,this);
        reportList.setAdapter(geoAttendanceAdapter);

    }


}
