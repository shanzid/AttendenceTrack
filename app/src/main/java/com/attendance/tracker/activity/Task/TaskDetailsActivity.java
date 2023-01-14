package com.attendance.tracker.activity.Task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.data.TaskDetailsResponse;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsActivity extends AppCompatActivity {
    TextView taskno, taskdetail, task_date;
    AppCompatImageView back;
    AppCompatButton save;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    CircleImageView img;
    private String userType, userName;
    String userID,taskID;
    AppCompatTextView number,user,status;
    String rating = "0";
    RatingBar ratingBarMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Intent mIntent = getIntent();
        taskID = mIntent.getStringExtra("TaskID");
        ids();
        initVariable();
        Clicklistener();

    }


    private void Clicklistener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeTask();
            }
        });
    }

    private void completeTask() {
        showDialog(this);
    }

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_rating);

        AppCompatButton cancel = dialog.findViewById(R.id.cancel);
        AppCompatButton save = dialog.findViewById(R.id.save);

        RatingBar mBar = dialog.findViewById(R.id.ratingBar);

       mBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
           @Override
           public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
               rating = String.valueOf(ratingBar.getRating());
           }
       });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTaskComplate(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),taskID,rating);
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating.equals("0")){
                    Toast.makeText(activity, "Please select Rating", Toast.LENGTH_SHORT).show();
                }else {
                    submitTaskComplate(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),taskID,rating);
                    dialog.dismiss();
                }

            }
        });
        dialog.show();

    }



    private void initVariable() {

        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
    }

    private void ids() {
        taskno = findViewById(R.id.TaskNo);
        taskdetail = findViewById(R.id.detailsID);
        task_date = findViewById(R.id.dateID);
        number = findViewById(R.id.tv_Phone);
        user = findViewById(R.id.tv_userName);
        status = findViewById(R.id.tv_Status);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        ratingBarMark = findViewById(R.id.ratingBar);
    }

    public void getData(String userID,String task_id) {
        if (internetConnection.isInternetAvailable(TaskDetailsActivity.this)) {
            // progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getTaskDetails(userID,task_id).enqueue(new Callback<TaskDetailsResponse>() {
                @Override
                public void onResponse(Call<TaskDetailsResponse> call, Response<TaskDetailsResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful())
                            //  progressBar.setVisibility(View.GONE);
                            dialog.dismiss();

                        ShowAllData(response.body());
                    }
                }


                @Override
                public void onFailure(Call<TaskDetailsResponse> call, Throwable t) {
                    t.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    public void submitTaskComplate(String userID,String task_id,String rating) {
        if (internetConnection.isInternetAvailable(TaskDetailsActivity.this)) {
            // progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.TaskComplete(userID,task_id,rating).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                            Toast.makeText(TaskDetailsActivity.this, ""+response.body().get("error_report").getAsString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),taskID);

                    }
                }


                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetTextI18n")
    private void ShowAllData(TaskDetailsResponse report) {
        taskno.setText(report.getTaskList());
        taskdetail.setText(report.getDetails());
        task_date.setText(report.getDates());
        user.setText("Employee: "+report.getUser());
        number.setText("Mobile: "+report.getUserMobile());
        status.setText("Status: "+report.getStatus());
        ratingBarMark.setRating(Float.parseFloat(report.getMarks()));

       if (report.getStatus().equals("Pending")){
           if (appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY).equals("1")){
               save.setVisibility(View.VISIBLE);
           }else {
               save.setVisibility(View.GONE);
           }
       }


    }

    @Override
    protected void onResume() {
        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),taskID);
        super.onResume();
    }
}