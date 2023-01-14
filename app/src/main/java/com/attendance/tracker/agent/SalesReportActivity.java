package com.attendance.tracker.agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.AttendanceReport.Adapter.AttendWeekAdapter;
import com.attendance.tracker.activity.AttendanceReport.AttendanceReportList;
import com.attendance.tracker.activity.AttendanceReport.AttendanceReportResponse;
import com.attendance.tracker.activity.master.SalesReportDetailsActivity;
import com.attendance.tracker.adapter.SalesReportAdapter;
import com.attendance.tracker.data.SalesReport;
import com.attendance.tracker.data.SalesReportList;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReportActivity extends AppCompatActivity implements OnUserClickListener {
    RecyclerView reportList;
    SalesReportAdapter dailyAttendAdapter;
    ArrayList<SalesReportList> ReportLists = new ArrayList<>();
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    DatePickerDialog picker;
    TextView tv_start_date, tv_end_date;
    TextView hederTitle;
    String monthID;
    String yearID;
    String currentDate,currentMonth,currentYear;
    TextView totalDeu,totalCompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);
        initVariable();
        initView();
        initFunc();
        initListener();
    }
    private void initVariable() {


        Intent mIntent = getIntent();
        // userId = mIntent.getStringExtra("userId");
        // type = mIntent.getStringExtra("type");
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
    }

    private void initView() {
        tv_start_date = findViewById(R.id.tv_start_date);

        totalDeu = findViewById(R.id.totalDeu);
        totalCompany = findViewById(R.id.tvCompany);
        findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Log.w("date",""+formattedDate);
        String[] separated = formattedDate.split("-");

        currentDate = separated[0];
        currentMonth =  separated[1]; //
        currentYear =  separated[2]; //
        tv_start_date.setText(currentMonth+"-"+currentYear);

        LoadData();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showStartDatePicker() {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        cldr.set(year, monthOfYear, dayOfMonth);
                        String dateString = sdf.format(cldr.getTime());
                        convertStartDateTime(dateString);
                    }
                }, year, month, day);
        picker.show();
    }

    private void convertStartDateTime(String startDate) {
        String[] separated = startDate.split("-");
        currentDate = separated[0];
        currentMonth =  separated[1];
        currentYear =  separated[2];
        tv_start_date.setText(currentMonth+"-"+currentYear);

    }

    private void initListener() {
         findViewById(R.id.lytStartDate).setOnClickListener(view -> showStartDatePicker());

        findViewById(R.id.btnSearch).setOnClickListener(view -> gotoSearch());
    }

    private void gotoSearch() {
        getReport(currentMonth, currentYear);
    }


    private void initFunc() {
     //   getReport(currentMonth, currentYear);
    }

    public void getReport(String month, String year) {
        String monthShort = month.substring(0,3);
        ReportLists.clear();
        if (internetConnection.isInternetAvailable(this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getMonthlyList(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME), appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD), monthShort, year).enqueue(new Callback<SalesReport>() {
                @Override
                public void onResponse(Call<SalesReport> call, Response<SalesReport> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            totalDeu.setText("Direct Sale : "+response.body().getRenewSale());
                            totalCompany.setText("Renew Sale : "+response.body().getDirectSale());
                            ReportLists.addAll(response.body().getReport());
                            dailyAttendAdapter.notifyDataSetChanged();

                            dialog.dismiss();

                            Log.w("test", "" + response.body().getErrorReport());
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            dailyAttendAdapter.notifyDataSetChanged();
                             totalCompany.setText("Direct Sale : 0");
                            totalDeu.setText("Renew Sale : 0");
                            Toast.makeText(SalesReportActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SalesReport> call, Throwable t) {
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
        dailyAttendAdapter = new SalesReportAdapter(ReportLists, this);
        reportList.setAdapter(dailyAttendAdapter);
        dailyAttendAdapter.SetItemClickListener(this);
    }

    @Override
    public void itemUserClick(View view, int position) {
        Intent mIntent  = new Intent(this, SalesReportDetailsActivity.class);
        mIntent.putExtra("day",ReportLists.get(position).getDay());
        mIntent.putExtra("month",currentMonth);
        mIntent.putExtra("year",currentYear);
        startActivity(mIntent);


    }
}
