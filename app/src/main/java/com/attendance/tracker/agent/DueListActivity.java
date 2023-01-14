package com.attendance.tracker.agent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.activity.company.SearchListAdapter;
import com.attendance.tracker.data.DueList;
import com.attendance.tracker.data.DueModel;
import com.attendance.tracker.data.ProfileData;
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

public class DueListActivity extends AppCompatActivity {
    RecyclerView rvDeuList;
    SearchView searchView;
    TextView totalDeu,totalCompany;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    ArrayList<DueList> dueLists = new ArrayList<>();
    DueAdapter dueAdapter;
    String userID = "";
    int loadMore  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_list);
        initVariables();
        initView();
        initFunc();
        initListener();
    }

    private void initVariables() {
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
        userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
    }

    private void initView() {
        rvDeuList = findViewById(R.id.rvDeuList);
        searchView = findViewById(R.id.search_view);
        totalDeu = findViewById(R.id.totalDeu);
        totalCompany = findViewById(R.id.tvCompany);
    }

    private void initFunc() {
        LoadData();
        getDueData(userID,loadMore,"");
    }

    private void initListener() {
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getDueData(userID, loadMore,query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()){
                            getDueData(userID, loadMore,newText);
                        }
                        return false;
                    }
                });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getDueData(String userId, int limit,String search) {
        dueLists.clear();
        if (internetConnection.isInternetAvailable(DueListActivity.this)) {

            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getDueList(userId,limit+ConstantValue.str_value_loadmore_10,search).enqueue(new Callback<DueModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<DueModel> call, Response<DueModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            if (response.body().getReport() != null){
                                totalDeu.setText("Total Due : "+response.body().getTotalDue());
                                totalCompany.setText("Total Company : "+response.body().getTotalComapny());
                                dueLists.addAll(response.body().getReport());
                                dueAdapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(DueListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();

                            }

                            dialog.dismiss();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();

                            Toast.makeText(DueListActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DueModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {
        dueAdapter = new DueAdapter( dueLists,getApplicationContext());
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.VERTICAL, false);
        rvDeuList.setLayoutManager(horizontalLayoutManagaer);
        rvDeuList.setAdapter(dueAdapter);

        dueAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
              //  getDueData(userID, loadMore = loadMore + ConstantValue.int_value_10,"");
            }
        });
    }

}