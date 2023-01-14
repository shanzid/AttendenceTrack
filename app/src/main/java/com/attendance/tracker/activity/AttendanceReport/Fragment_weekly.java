package com.attendance.tracker.activity.AttendanceReport;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.AttendanceReport.Adapter.AttendWeekAdapter;
import com.attendance.tracker.activity.AttendanceReport.Adapter.DailyAttendAdapter;
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

public class Fragment_weekly extends Fragment {
    View view;

    RecyclerView reportList;
    AttendWeekAdapter dailyAttendAdapter;
    ArrayList<AttendanceReportList> ReportLists = new ArrayList<>();
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    DatePickerDialog picker;
    TextView tv_start_date, tv_end_date;
    TextView hederTitle;
    String startDate, endDate, startDateTimeStamp, EndDateTimeStamp;
    String userId, type;
    String weekID;
    String monthID;
    String yearID;

    //spinner
    String[] week = {"1", "2",
            "3", "4",
            "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30","31", "32",
            "33", "34",
            "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52"};

        String currentDate,currentMonth,currentYear;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);


        initVariable();
        initView();
        initFunc();
        initListener();
        return view;
    }


    private void initVariable() {


        Intent mIntent = getActivity().getIntent();
        // userId = mIntent.getStringExtra("userId");
        // type = mIntent.getStringExtra("type");
        appSessionManager = new AppSessionManager(getContext());
        internetConnection = new CheckInternetConnection();
    }

    private void initView() {

        view.findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);
        Spinner day_spin = view.findViewById(R.id.sp_day);
        tv_start_date = view.findViewById(R.id.tv_start_date);


        day_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weekID=week[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter ad
                = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                week);



        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        day_spin.setAdapter(ad);

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


    }

    private void showStartDatePicker() {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(getActivity(),
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
        view. findViewById(R.id.lytStartDate).setOnClickListener(view -> showStartDatePicker());

        view.findViewById(R.id.btnSearch).setOnClickListener(view -> gotoSearch());
    }

    private void gotoSearch() {
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        getReport(userId, weekID, currentMonth, currentYear);
    }


    private void initFunc() {
        // getGeoReport(userId,type);
    }

    public void getReport(String userId, String day, String month, String year) {
        String monthShort = month.substring(0,3);

        ReportLists.clear();
        if (internetConnection.isInternetAvailable(getContext())) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.showAttReport(userId, day, monthShort, year).enqueue(new Callback<AttendanceReportResponse>() {
                @Override
                public void onResponse(Call<AttendanceReportResponse> call, Response<AttendanceReportResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            ReportLists.addAll(response.body().getReport());
                            dailyAttendAdapter.notifyDataSetChanged();

                            dialog.dismiss();

                            Log.w("test", "" + response.body().getErrorReport());
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AttendanceReportResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(view.findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadData() {

        reportList = view.findViewById(R.id.reportList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        reportList.setHasFixedSize(true);
        reportList.setLayoutManager(layoutManager);
        dailyAttendAdapter = new AttendWeekAdapter(ReportLists, getActivity());
        reportList.setAdapter(dailyAttendAdapter);

    }
}
