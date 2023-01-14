package com.attendance.tracker.activity.company.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.company.fragment.adapter.SelfDueAdapter;
import com.attendance.tracker.activity.company.fragment.adapter.SelfPaidAdapter;
import com.attendance.tracker.activity.user.UserMainActivity;
import com.attendance.tracker.agent.SelfDueList;
import com.attendance.tracker.agent.SelfDueModel;
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


public class PaidFragment extends Fragment {
    View view;
    RecyclerView rvPaidList;
    SelfPaidAdapter selfDueAdapter;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    ArrayList<SelfDueList> selfDue = new ArrayList<>();

    public PaidFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_paid, container, false);
        initVariable();
        initView(view);
        initFunction();
        return view;
    }

    private void initVariable() {
        appSessionManager = new AppSessionManager(getActivity());
        internetConnection = new CheckInternetConnection();
    }

    private void initView(View view) {
        rvPaidList = view.findViewById(R.id.rvPaidList);
        LoadData();
    }

    private void initFunction() {
        callSelfPaidApi(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),"0,20","1");
    }

    public void callSelfPaidApi(String userId,String loadMore,String type){
        if (internetConnection.isInternetAvailable(getActivity())) {
            selfDue.clear();
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getSelfDueList(userId,loadMore,type).enqueue(new Callback<SelfDueModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<SelfDueModel> call, Response<SelfDueModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            selfDue.addAll(response.body().getDueList()) ;
                            selfDueAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), "some thing went to wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<SelfDueModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(view.findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void LoadData() {
        selfDueAdapter = new SelfPaidAdapter(selfDue,getActivity());
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        rvPaidList.setLayoutManager(horizontalLayoutManagaer);
        rvPaidList.setAdapter(selfDueAdapter);

        //selfDueAdapter.setOnBottomReachedListener(this);
    }

}