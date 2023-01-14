package com.attendance.tracker.activity.NewCompanyList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.simpleplacepicker.MapActivity;
import com.attendance.simpleplacepicker.utils.SimplePlacePicker;
import com.attendance.tracker.Live_trackerMapsActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.DateWiseGeoActivity;
import com.attendance.tracker.activity.ProfileDetails.EmployeeDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.MasterProfileDetailsActivity;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.activity.company.BlockUserListActivity;
import com.attendance.tracker.activity.master.Adpater.ProfileAdapter;
import com.attendance.tracker.activity.user.UserGeoListActivity;
import com.attendance.tracker.adapter.CompanyListAdapter;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.data.JoinResponse;
import com.attendance.tracker.data.ProfileData;
import com.attendance.tracker.data.ProfileList;
import com.attendance.tracker.data.UserList;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCompanyListActivity extends AppCompatActivity implements OnUserClickListener {
    private RecyclerView userList;
    private ArrayList<CompanyReportList> userDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    private CompanyListAdapter userAdapter;
    TextView hederName,total;
    private String userType, userName;
    String userId;
    private ProgressBar progressBar;
    SearchView searchView;
    String agentID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company_list);
        initVariable();
        initView();
        initFunc();
        initListener();
        initSearch();

    }

    private void initSearch() {
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //  getData(query);
                        if (agentID.isEmpty()){
                            getProfileData(userId, "0,20",query);
                        }else {
                            getAgentProfileData(agentID, "0,20",query);
                        }


                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()){
                            if (agentID.isEmpty()){
                                getProfileData(userId, "0,20",newText);
                            }else {
                                getAgentProfileData(agentID, "0,20",newText);
                            }
                        }
                        return false;
                    }


                });
    }

    private void initVariable() {
        Intent mIntent = getIntent();
        userType = mIntent.getStringExtra("userType");
        userName = mIntent.getStringExtra("userName");
        agentID = mIntent.getStringExtra("agentID");


        userDataList = new ArrayList<>();
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);

        //  Toast.makeText(this, ""+userName, Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        userDataList = new ArrayList<>();

        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        setContentView(R.layout.activity_new_company_list);

        userList = findViewById(R.id.rv_userList);
        searchView = findViewById(R.id.search_view);
        total = findViewById(R.id.totalCom);
        progressBar = findViewById(R.id.progress_bar);

    }

    private void initFunc() {

        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);


//
//        if (userName.equals("Company List")) {
//            getProfileData(userName, userType);}
//
//        else
//            getProfileData(userId, userType);


        if (agentID.isEmpty()){
            getProfileData(userId, "0,20","");
        }else {
            getAgentProfileData(agentID, "0,20","");

        }


    }

    private void initListener() {

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }


    public void getProfileData(String userId, String limit,String search) {
        userDataList.clear();
        if (internetConnection.isInternetAvailable(NewCompanyListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getCompanyList(userId, limit,search).enqueue(new Callback<CompanyListResponse>() {
                @Override
                public void onResponse(Call<CompanyListResponse> call, Response<CompanyListResponse> response) {
                    CompanyListResponse companyListResponse=response.body();
                    String total_company= String.valueOf(companyListResponse.getTotalComapny());
                    if (response.isSuccessful()) {



                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            total.setText("Total Company:\t\t"+total_company);
                            progressBar.setVisibility(View.GONE);

                        } else if (response.body().getError() == 1) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewCompanyListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompanyListResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getAgentProfileData(String userId, String limit,String search) {
        userDataList.clear();
        if (internetConnection.isInternetAvailable(NewCompanyListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getAgentCompanyList(userId, limit,search).enqueue(new Callback<CompanyListResponse>() {
                @Override
                public void onResponse(Call<CompanyListResponse> call, Response<CompanyListResponse> response) {
                    CompanyListResponse companyListResponse=response.body();
                    String total_company= String.valueOf(companyListResponse.getTotalComapny());
                    if (response.isSuccessful()) {



                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            total.setText("Total Company:\t\t"+total_company);
                            progressBar.setVisibility(View.GONE);

                        } else if (response.body().getError() == 1) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewCompanyListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompanyListResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {

        userAdapter = new CompanyListAdapter(userDataList,getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);
        userList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
        userAdapter.SetItemClick(this);
        userAdapter.SetItemBlockClick(new OnBlockListener() {
            @Override
            public void itemUserBlockClick(View view, int position) {
                new AlertDialog.Builder(NewCompanyListActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to block Company?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callBlockApi(userId,userDataList.get(position).getId(),"0");
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }


    @Override
    public void itemUserClick(View view, int position) {
        Intent mIntent = new Intent(NewCompanyListActivity.this, CompanyDetailsActivity.class);
        mIntent.putExtra("UserID", userDataList.get(position).getId());
        startActivity(mIntent);
    }

    private void callBlockApi(String userId,String id,String condition) {


        if (internetConnection.isInternetAvailable(NewCompanyListActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitBlockList(userId, id,condition).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {

                        int errCount = response.body().get("error").getAsInt();
                        if (errCount == 0) {
                            dialog.dismiss();
                            Toast.makeText(NewCompanyListActivity.this, "" + response.body().get("error_report").getAsString(), Toast.LENGTH_SHORT).show();
                            getProfileData(userId, "0,20","");
                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(NewCompanyListActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }

//
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

}