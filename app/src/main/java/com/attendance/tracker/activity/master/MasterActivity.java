package com.attendance.tracker.activity.master;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.ChangePassActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.ServerMaintainActivity;
import com.attendance.tracker.UserBlockActivity;
import com.attendance.tracker.activity.AgentList.AgentListActivity;
import com.attendance.tracker.activity.NewCompanyList.NewCompanyListActivity;
import com.attendance.tracker.activity.ProfileDetails.MasterProfileDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.activity.company.CompanyActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.activity.master.CompanyEdit.UserEditActivity;
import com.attendance.tracker.agent.CommisionListActivity;
import com.attendance.tracker.agent.DueListActivity;
import com.attendance.tracker.agent.SalesReportActivity;
import com.attendance.tracker.data.ControllingModel;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterActivity extends AppCompatActivity {

    AppSessionManager appSessionManager;
    TextView userName,userEmail;
    CircleImageView img;
    CheckInternetConnection internetConnection;
    int serverStatus,userBlock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        initVariable();
        initView();
        iniFunction();
        initListener(); }


    private void initVariable() {

        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }

    private void initView() {
        userName =findViewById(R.id.userName);
        userEmail =findViewById(R.id.userMobile);
        img =findViewById(R.id.profile_image);
    }

    private void iniFunction() {
        getServerData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
    }

    private void initListener() {
        findViewById(R.id.cvCreateUser).setOnClickListener(view -> callCreateCompany());
        findViewById(R.id.cvUserEdit).setOnClickListener(view -> callEditCompany());
        findViewById(R.id.cvUserList).setOnClickListener(view -> gotoUserList());
        findViewById(R.id.logout).setOnClickListener(view -> logout());
        findViewById(R.id.cv_chnagepass).setOnClickListener(view -> chnagePass());
        findViewById(R.id.profile).setOnClickListener(view -> Profile());
        findViewById(R.id.cv_addAgent).setOnClickListener(view -> AgentList());
        findViewById(R.id.cv_commission_list).setOnClickListener(view -> gotoComission());
        findViewById(R.id.cv_deu_list).setOnClickListener(view -> gotoDueList());
        findViewById(R.id.cv_sale_report).setOnClickListener(view -> gotoSelReport());

    }

    private void gotoSelReport() {
        startActivity(new Intent(this, SalesReportActivity.class));
    }

    private void gotoComission() {
        startActivity(new Intent(this, CommisionListActivity.class));
    }
    private void gotoDueList() {
        startActivity(new Intent(this, DueListActivity.class));
    }

    private void AgentList() {
        Intent mIntent = new Intent(getApplicationContext(), AgentListActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

    private void Profile() {
        Intent mIntent = new Intent(getApplicationContext(), MasterProfileDetailsActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

    private void chnagePass() {
        Intent mIntent = new Intent(getApplicationContext(), ChangePassActivity.class);
        mIntent.putExtra("userName", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        mIntent.putExtra("userPass", appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD));
        startActivity(mIntent);
    }

    private void callEditCompany() {
        Intent mIntent = new Intent(this, UserEditActivity.class);
        mIntent.putExtra("userType","2");
        mIntent.putExtra("userName","Company Edit");
        startActivity(mIntent);

    }

    private void gotoUserList() {

        Intent mIntent = new Intent(this, NewCompanyListActivity.class);
        mIntent.putExtra("userType","2");
        mIntent.putExtra("userName","Company List");
        mIntent.putExtra("agentID","");
        startActivity(mIntent);


    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Logout App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appSessionManager.logoutUser();
                        startActivity(new Intent(MasterActivity.this, LoginActivity.class));
                        finish();                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void callCreateCompany() {
        startActivity(new Intent(this,CreateCompanyActivity.class));
    }

    public void getServerData(String userId) {
        if (internetConnection.isInternetAvailable(MasterActivity.this)) {

            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getServerStatus(userId).enqueue(new Callback<ControllingModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ControllingModel> call, Response<ControllingModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            serverStatus = response.body().getServerStatus();
                            userBlock = response.body().getUserBlock();
                            if (serverStatus == 1){
                                Intent mIntent = new Intent(MasterActivity.this, ServerMaintainActivity.class);
                                startActivity(mIntent);
                                finish();
                            }else {
                                if (userBlock == 1){
                                    Intent mIntent = new Intent(MasterActivity.this, UserBlockActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                }

                            }
                        } else {
                            Toast.makeText(MasterActivity.this, "some thing went to wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ControllingModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getData(String userID) {
        if (internetConnection.isInternetAvailable(MasterActivity.this)) {
            // progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfiledetails(userID).enqueue(new Callback<ProfileDetailsData>() {
                @Override
                public void onResponse(Call<ProfileDetailsData> call, Response<ProfileDetailsData> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful())
                            //  progressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                        userName.setText(response.body().getName());
                        userEmail.setText(response.body().getMobile());

                        Glide.with(getApplicationContext())
                                .load(BuildConfig.BASE_URL+""+response.body().getPhoto())
                                .into(img);

                    }
                }


                @Override
                public void onFailure(Call<ProfileDetailsData> call, Throwable t) {
                    t.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


}