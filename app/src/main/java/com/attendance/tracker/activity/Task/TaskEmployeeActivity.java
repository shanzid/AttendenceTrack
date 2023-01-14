package com.attendance.tracker.activity.Task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.data.TaskData;
import com.attendance.tracker.data.TaskListResponse;
import com.attendance.tracker.interfaces.TaskListener;
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

public class TaskEmployeeActivity extends AppCompatActivity  implements TaskListener {
    private RecyclerView taskdata;
    private ArrayList<TaskData> TaskDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    String userId;
    TaskListAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_employee);

        initVariable();
        initView();
        initFunc();
        initListener();

    }

    private void initListener() {
    }

    private void initFunc() {
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        getProfileData(userId);
    }


    public void getProfileData(String userId) {
        TaskDataList.clear();
        if (internetConnection.isInternetAvailable(getApplicationContext())) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getTaskList(userId).enqueue(new Callback<TaskListResponse>() {
                @Override
                public void onResponse(Call<TaskListResponse> call, Response<TaskListResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            TaskDataList.addAll(response.body().getTaskList());
                            LoadData();
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TaskListResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {
        myAdapter = new TaskListAdapter(getApplicationContext(), TaskDataList);
        taskdata.setHasFixedSize(true);
        taskdata.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        taskdata.setAdapter(myAdapter);
//        myAdapter.notifyDataSetChanged();
//        myAdapter.SetTaskItemClick(this);


    }

    private void initView() {
        taskdata = findViewById(R.id.rvTaskList);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initVariable() {
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(getApplicationContext());
        TaskDataList = new ArrayList<>();
    }


    @Override
    public void taskItemClick(View view, int position) {
//        Intent mIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
//        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
//        mIntent.putExtra("UserID", userId);
//        startActivity(mIntent);
    }
}
