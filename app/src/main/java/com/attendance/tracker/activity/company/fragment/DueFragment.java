package com.attendance.tracker.activity.company.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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
import com.attendance.tracker.activity.company.CompanyActivity;
import com.attendance.tracker.activity.company.PaymentSubmitActivity;
import com.attendance.tracker.activity.company.fragment.adapter.PaymentAdapter;
import com.attendance.tracker.activity.company.fragment.adapter.SelfDueAdapter;
import com.attendance.tracker.agent.DueAdapter;
import com.attendance.tracker.agent.OnBottomReachedListener;
import com.attendance.tracker.agent.SelfDueList;
import com.attendance.tracker.agent.SelfDueModel;
import com.attendance.tracker.data.PaymentList;
import com.attendance.tracker.data.PaymentModel;
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


public class DueFragment extends Fragment implements OnBlockListener {
    View view;
    RecyclerView rvDeuList;
    SelfDueAdapter selfDueAdapter;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    ArrayList<SelfDueList> selfDueLists = new ArrayList<>();
    ArrayList<PaymentList> paymentLists = new ArrayList<>();
    PaymentAdapter paymentAdapter;

    public DueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_due, container, false);
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
        rvDeuList = view.findViewById(R.id.rvDeuList);
        LoadData();

    }

    private void initFunction() {
        callSelfDueApi(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),"0,20","0");
    }

    public void callSelfDueApi(String userId,String loadMore,String type){
        if (internetConnection.isInternetAvailable(getActivity())) {
            selfDueLists.clear();
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
                            selfDueLists.addAll(response.body().getDueList()) ;
                            selfDueAdapter.notifyDataSetChanged();

                        } else if (response.body().getError() == 1){
                            Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
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
        selfDueAdapter = new SelfDueAdapter( selfDueLists,getActivity());
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        rvDeuList.setLayoutManager(horizontalLayoutManagaer);
        rvDeuList.setAdapter(selfDueAdapter);
        //selfDueAdapter.setOnBottomReachedListener(this);
        selfDueAdapter.setOnBlockListener(this);
    }

    @Override
    public void itemUserBlockClick(View view, int position) {
        openPaymentPopup(selfDueLists.get(position).getDueId(),selfDueLists.get(position).getInvoice());
    }

    private void openPaymentPopup(String dueID,String invoice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        final AlertDialog popupConfirmation = builder.create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.payment_dialog, null);

        ImageView back = customView.findViewById(R.id.back);
        RecyclerView recyclerView = customView.findViewById(R.id.paymentMethodList);

        callPayMethodAPi();

        paymentAdapter = new PaymentAdapter( paymentLists,getActivity());
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(paymentAdapter);
        //selfDueAdapter.setOnBottomReachedListener(this);
        paymentAdapter.setOnBlockListener(new OnBlockListener() {
            @Override
            public void itemUserBlockClick(View view, int position) {

                gotoNext(paymentLists.get(position).getMethod(),paymentLists.get(position).getAcc(),dueID,invoice);
                popupConfirmation.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmation.dismiss();
            }
        });

        popupConfirmation.setCancelable(false);
        popupConfirmation.setView(customView);
        popupConfirmation.show();
    }

    private void gotoNext(String method,String acc,String dueId,String invoice) {
        Intent mIntent = new Intent(getActivity(), PaymentSubmitActivity.class);
        mIntent.putExtra("method",method);
        mIntent.putExtra("acc",acc);
        mIntent.putExtra("invoice",invoice);
        mIntent.putExtra("dueId",dueId);
        startActivity(mIntent);

    }

    private void callPayMethodAPi() {
        if (internetConnection.isInternetAvailable(getActivity())) {
            paymentLists.clear();
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getPaymentList(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID)).enqueue(new Callback<PaymentModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            paymentLists.addAll(response.body().getPayments()) ;
                            paymentAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), "something went to wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<PaymentModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(view.findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }
}