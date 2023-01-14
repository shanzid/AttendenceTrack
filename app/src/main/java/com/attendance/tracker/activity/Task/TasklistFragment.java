package com.attendance.tracker.activity.Task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

public class TasklistFragment extends Fragment{
    View view;
    private RecyclerView taskdata;
    private ArrayList<TaskData> TaskDataList;
    private CheckInternetConnection internetConnection;
    private AppSessionManager appSessionManager;
    String userId;
    TaskListAdapter myAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasklist, container, false);
        initVariable();
        initView();
        initFunc();
        initListener();
        return view;
    }

    private void initListener() {
    }

    private void initFunc() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getTaskData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));

    }

    public void getTaskData(String userId) {
        TaskDataList.clear();
        if (internetConnection.isInternetAvailable(getContext())) {

//                final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.loading))
//                        .content(getResources().getString(R.string.pleaseWait))
//                        .progress(true, 0)
//                        .cancelable(false)
//                        .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getTaskList(userId).enqueue(new Callback<TaskListResponse>() {
                @Override
                public void onResponse(Call<TaskListResponse> call, Response<TaskListResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            TaskDataList.addAll(response.body().getTaskList());
                            LoadData();
                          //  dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            Toast.makeText(getContext(), "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TaskListResponse> call, Throwable t) {
                  // dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(view.findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {
        myAdapter = new TaskListAdapter(getActivity(), TaskDataList);
        taskdata.setHasFixedSize(true);
        taskdata.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        taskdata.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

    }

    private void initView() {
        taskdata = view.findViewById(R.id.rvTaskList);

    }

    private void initVariable() {
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(getContext());
        TaskDataList = new ArrayList<>();
    }



}
