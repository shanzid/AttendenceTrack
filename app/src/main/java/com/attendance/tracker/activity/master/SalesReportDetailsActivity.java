package com.attendance.tracker.activity.master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.master.Adpater.SalesReportDetailsAdapter;
import com.attendance.tracker.adapter.SalesReportAdapter;
import com.attendance.tracker.agent.SalesReportActivity;
import com.attendance.tracker.data.SalesReport;
import com.attendance.tracker.data.SalesReportDetails;
import com.attendance.tracker.data.SalesReportDetailsList;
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

public class SalesReportDetailsActivity extends AppCompatActivity {

    String day,month,year;
    TextView totalDeu,totalCompany;
    RecyclerView reportList;
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    ArrayList<SalesReportDetailsList> salesReportDetailsLists = new ArrayList<>();
    SalesReportDetailsAdapter salesReportAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report_details);
        initVariable();
        initView();
        initFunc();
        initListener();
    }

    private void initVariable() {

        Intent mIntent = getIntent();
        day = mIntent.getStringExtra("day");
        month = mIntent.getStringExtra("month");
        year = mIntent.getStringExtra("year");
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();


    }

    private void initView() {
        totalDeu = findViewById(R.id.totalDeu);
        totalCompany = findViewById(R.id.tvCompany);
    }

    private void initFunc() {

        LoadData();

        getReport(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),"0,20","",day,month,year);
    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getReport(String userId,String limit,String search,String day,String month, String year) {
        String monthShort = month.substring(0,3);
        salesReportDetailsLists.clear();
        if (internetConnection.isInternetAvailable(this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getSalesReportDetails(userId,limit,search,day, monthShort, year).enqueue(new Callback<SalesReportDetails>() {
                @Override
                public void onResponse(Call<SalesReportDetails> call, Response<SalesReportDetails> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            totalDeu.setText("Direct Sale : "+response.body().getRenewSale());
                            totalCompany.setText("Renew Sale : "+response.body().getDirectSale());
                            salesReportDetailsLists.addAll(response.body().getReport());
                            salesReportAdapter.notifyDataSetChanged();

                            dialog.dismiss();

                            Log.w("test", "" + response.body().getErrorReport());
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            salesReportAdapter.notifyDataSetChanged();
                            totalCompany.setText("Direct Sale : 0");
                            totalDeu.setText("Renew Sale : 0");
                            Toast.makeText(SalesReportDetailsActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SalesReportDetails> call, Throwable t) {
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reportList.setHasFixedSize(true);
        reportList.setLayoutManager(layoutManager);
        salesReportAdapter = new SalesReportDetailsAdapter(salesReportDetailsLists, this);
        reportList.setAdapter(salesReportAdapter);
       // dailyAttendAdapter.SetItemClickListener(this);
    }
}