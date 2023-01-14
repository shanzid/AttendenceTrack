package com.attendance.tracker.activity.company;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.NewCompanyList.CompanyListResponse;
import com.attendance.tracker.activity.NewCompanyList.CompanyReportList;
import com.attendance.tracker.activity.NewCompanyList.NewCompanyListActivity;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.adapter.CompanyListAdapter;
import com.attendance.tracker.data.BlocklistResponse;
import com.attendance.tracker.data.blocklistdata;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUserListActivity extends AppCompatActivity implements OnBlockListener {
    private RecyclerView userList;
    private ArrayList<blocklistdata> userDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    private BlockListAdapter userAdapter;
    TextView hederName,total;
    private String userType, userName;
    String userId;
    private ProgressBar progressBar;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user_list);
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

        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        userList = findViewById(R.id.rv_userList);
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
        getProfileData(userId);

    }

    private void initListener() {

        findViewById(R.id.back).setOnClickListener(view -> finish());
    }


    public void getProfileData(String userId) {
        userDataList.clear();
        if (internetConnection.isInternetAvailable(BlockUserListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.showblockuser(userId).enqueue(new Callback<BlocklistResponse>() {
                @Override
                public void onResponse(Call<BlocklistResponse> call, Response<BlocklistResponse> response) {

                    if (response.isSuccessful()) {



                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            //Toast.makeText(NewCompanyListActivity.this, ""+companyListResponse.getTotalComapny(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        } else if (response.body().getError() == 1) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BlockUserListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BlocklistResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {

        userAdapter = new BlockListAdapter(userDataList,getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);
        userList.setAdapter(userAdapter);
        userAdapter.setOnBlockListener(this);

    }
    private void callBlockApi(String userId,String id,String condition) {


        if (internetConnection.isInternetAvailable(BlockUserListActivity.this)) {
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
                            Toast.makeText(BlockUserListActivity.this, "" + response.body().get("error_report").getAsString(), Toast.LENGTH_SHORT).show();
                            getProfileData(userId);
                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(BlockUserListActivity.this, "Error!", Toast.LENGTH_SHORT).show();
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


    @Override
    public void itemUserBlockClick(View view, int position) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to unblock User?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBlockApi(userId,userDataList.get(position).getEmployeeId(),"1");
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}