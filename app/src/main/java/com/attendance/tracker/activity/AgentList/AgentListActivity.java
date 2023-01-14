package com.attendance.tracker.activity.AgentList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.tracker.R;
import com.attendance.tracker.activity.NewCompanyList.CompanyListResponse;
import com.attendance.tracker.activity.NewCompanyList.CompanyReportList;
import com.attendance.tracker.activity.NewCompanyList.NewCompanyListActivity;
import com.attendance.tracker.activity.company.CompanyActivity;
import com.attendance.tracker.adapter.AgentListAdapter;
import com.attendance.tracker.adapter.CompanyListAdapter;
import com.attendance.tracker.interfaces.OnBlockListener;
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

public class AgentListActivity extends AppCompatActivity implements OnBlockListener {
    private RecyclerView userList;
    private ArrayList<AgentListReport> userDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    private AgentListAdapter userAdapter;
    private TextView total;
    private String userType, userName;
    String userId;
    private ProgressBar progressBar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_list);
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
        setContentView(R.layout.activity_agent_list);

        userList = findViewById(R.id.rv_userList);
        total = findViewById(R.id.hederName);
        progressBar = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);

    }

    private void initFunc() {

        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);

            getProfileData(userId, "0,20","");

    }

    private void initListener() {

        findViewById(R.id.back).setOnClickListener(view -> finish());

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getProfileData(userId, "0,20",query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()){
                            getProfileData(userId, "0,20","");
                        }
                        return false;
                    }
                });
    }

    public void getProfileData(String userId, String limit,String search) {
        userDataList.clear();
        if (internetConnection.isInternetAvailable(AgentListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getAgentList(userId, limit,search).enqueue(new Callback<AgentListResponse>() {
                @Override
                public void onResponse(Call<AgentListResponse> call, Response<AgentListResponse> response) {
                    AgentListResponse agentListResponse=response.body();
                    String total_agent= String.valueOf(agentListResponse.getTotalAgent());
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            userDataList.addAll(response.body().getReport());
                            LoadData();
                            total.setText("Total Agent:\t\t"+total_agent);
                            progressBar.setVisibility(View.GONE);

                        } else if (response.body().getError() == 1) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AgentListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AgentListResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {

        userAdapter = new AgentListAdapter(userDataList,getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userList.setLayoutManager(layoutManager);
        userList.setHasFixedSize(true);
        userList.setAdapter(userAdapter);
        userAdapter.setClickListener(this);

    }

    @Override
    public void itemUserBlockClick(View view, int position) {
        Intent mIntent = new Intent(this, NewCompanyListActivity.class);
        mIntent.putExtra("userType","4");
        mIntent.putExtra("userName","Company List");
        mIntent.putExtra("agentID",userDataList.get(position).getId());
        startActivity(mIntent);
    }
}