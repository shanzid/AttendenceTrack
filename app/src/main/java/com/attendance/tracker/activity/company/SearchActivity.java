package com.attendance.tracker.activity.company;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.data.SearchData;
import com.attendance.tracker.data.SearchResponse;
import com.attendance.tracker.interfaces.OnBlockListener;
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

public class SearchActivity extends AppCompatActivity implements OnBlockListener {
    SearchView searchView;
    ArrayList<SearchData> finalSearchList = new ArrayList<>();
    String search = "";
    String userID;
    SearchListAdapter searchListAdapter;
    AppSessionManager appSessionManager;
    RecyclerView SearchProduct;
    private AppCompatImageView back;
    private CheckInternetConnection internetConnection;

    // Intent getData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initVar();
        id();
        clicklistener();

    }

    private void clicklistener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {


                    @Override
                    public boolean onQueryTextSubmit(String query) {


                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                       // getData(userID,search);
                        if (newText.length() > 3){
                            getData(userID,newText);
                        }
                        return false;
                    }
                });
    }


    private void initVar() {
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
       getData(userID,search);
    }

    private void id() {
        searchView = findViewById(R.id.search_view);
        SearchProduct = findViewById(R.id.search_list_recyclerview);
        back = findViewById(R.id.back);

        //searview


        //
    }

    private void getData(String userId,String search) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();
            finalSearchList.clear();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.GetSearchList(userId,search).enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            finalSearchList.addAll(response.body().getReport());
                            showData();
                            dialog.dismiss();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();

                            Toast.makeText(SearchActivity.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                    dialog.dismiss();

                }
            });

    }

    private void showData() {
        SearchProduct = findViewById(R.id.search_list_recyclerview);
        searchListAdapter = new SearchListAdapter( finalSearchList,getApplicationContext());
        LinearLayoutManager horizontalLayoutManagaer = new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.VERTICAL, false);
        SearchProduct.setLayoutManager(horizontalLayoutManagaer);
        SearchProduct.setAdapter(searchListAdapter);
        searchListAdapter.notifyDataSetChanged();
        searchListAdapter.setOnBlockListener(this);

    }

    @Override
    public void itemUserBlockClick(View view, int position) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to Block User?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBlockApi(userID,finalSearchList.get(position).getId(),"0");
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    private void callBlockApi(String userId,String id,String condition) {


        if (internetConnection.isInternetAvailable(SearchActivity.this)) {
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
                            getData(userId,search);
                            Toast.makeText(SearchActivity.this, "" + response.body().get("error_report").getAsString(), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(SearchActivity.this, "Error!", Toast.LENGTH_SHORT).show();
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





