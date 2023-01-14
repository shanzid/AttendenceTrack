package com.attendance.tracker.activity.company;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.ChangePassActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.ServerMaintainActivity;
import com.attendance.tracker.UserBlockActivity;
import com.attendance.tracker.activity.AttendanceReport.AttendanceHistoryActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.activity.UserListActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.activity.login.NewPasswordSetActivity;
import com.attendance.tracker.activity.master.CompanyEdit.UserEditActivity;
import com.attendance.tracker.agent.AgentDashboardActivity;
import com.attendance.tracker.agent.CommisionListActivity;
import com.attendance.tracker.agent.SalesReportActivity;
import com.attendance.tracker.data.CommissionModel;
import com.attendance.tracker.data.ControllingModel;
import com.attendance.tracker.data.DeuData;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.attendance.simpleplacepicker.MapActivity;
import com.attendance.simpleplacepicker.utils.SimplePlacePicker;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompanyActivity extends AppCompatActivity {
    AppSessionManager appSessionManager;
    TextView userName, userEmail, companyEdit,tvDue;
    String latitude, longlitude, address;
    CheckInternetConnection internetConnection;
    CircleImageView imgview;
    String userId, userType;
    int serverStatus,userBlock,paymentDue,dueBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_new);
        initVariable();
        initView();
        iniFunction();

    }


    private void initVariable() {
        if (hasPermissionInManifest(CompanyActivity.this, 1, Manifest.permission.ACCESS_FINE_LOCATION))
            ;
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
    }

    private void initView() {
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userMobile);
        imgview = findViewById(R.id.img);
        tvDue = findViewById(R.id.tvDue);

    }

    private void iniFunction() {



    }



    @Override
    protected void onResume() {
        super.onResume();
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        getData(userId);
        userType = appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY);
        getServerData(userId);

        getDueData(userId);
    }
    public void getDueData(String userId) {
        if (internetConnection.isInternetAvailable(CompanyActivity.this)) {

            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getDeuList(userId).enqueue(new Callback<DeuData>() {
                @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                @Override
                public void onResponse(@NonNull Call<DeuData> call, @NonNull Response<DeuData> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null){
                            if (response.body().getError() == 0) {
                                tvDue.setText("Due : "+response.body().getCurrentDue()+" ৳");
                            }
                        }
                            dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<DeuData> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initListener() {

        if (dueBlock == 0){
            findViewById(R.id.cvCreateUser).setOnClickListener(view -> callCreateCompany());
            findViewById(R.id.cvUserList).setOnClickListener(view -> gotoEmployeList());
            findViewById(R.id.cv_set_geo).setOnClickListener(view -> gotoUserList());
            findViewById(R.id.live_track_employeeId).setOnClickListener(view -> gotoUserLiveList());
            findViewById(R.id.cv_dateWise).setOnClickListener(view -> gotoDateWizeList());
            findViewById(R.id.cv_leaderAtten).setOnClickListener(view -> gotoLeaderAttendanceList());
            findViewById(R.id.cv_leaderList).setOnClickListener(view -> gotoLeaderList());

            findViewById(R.id.cv_setLoc).setOnClickListener(view -> selectLocationOnMap());
            findViewById(R.id.cv_company_details).setOnClickListener(view -> CompanyDetails());
            findViewById(R.id.cv_company_edit).setOnClickListener(view -> GoemployeeEdit());
            findViewById(R.id.cv_blockuser).setOnClickListener(view -> BlockUserList());
            findViewById(R.id.cv_attendance_report).setOnClickListener(view -> AttendanceReport());
            findViewById(R.id.cv_sale_report).setOnClickListener(view -> gotoSaleReport());

            findViewById(R.id.cv_due_report).setOnClickListener(view -> gotoDeu());
            findViewById(R.id.tvDue).setOnClickListener(view -> gotoDeu());
            findViewById(R.id.logout).setOnClickListener(view -> logout());
            findViewById(R.id.cv_Changepass).setOnClickListener(view -> ChnagePass());
        }else if (dueBlock == 1){
            // change background color

            findViewById(R.id.cvCreateUser).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cvUserList).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_set_geo).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_dateWise).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_leaderAtten).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_leaderList).setBackgroundColor(getResources().getColor(R.color.cardbg));

            findViewById(R.id.cv_setLoc).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_company_details).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_company_edit).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_blockuser).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_attendance_report).setBackgroundColor(getResources().getColor(R.color.cardbg));
            findViewById(R.id.cv_sale_report).setBackgroundColor(getResources().getColor(R.color.cardbg));




            findViewById(R.id.cv_due_report).setOnClickListener(view -> gotoDeu());
            findViewById(R.id.tvDue).setOnClickListener(view -> gotoDeu());
            findViewById(R.id.logout).setOnClickListener(view -> logout());
            findViewById(R.id.cv_Changepass).setOnClickListener(view -> ChnagePass());
        }



    }

    private void gotoSaleReport() {
        Intent mIntent = new Intent(this, SalesReportActivity.class);
        startActivity(mIntent);
    }

    private void gotoDeu() {
        Intent mIntent = new Intent(this, CompanyDueListActivity.class);
        startActivity(mIntent);
    }

    private void gotoEmployeList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Employee List");
        startActivity(mIntent);
    }

    private void AttendanceReport() {
        Intent mIntent = new Intent(getApplicationContext(), AttendanceHistoryActivity.class);
        mIntent.putExtra("userID", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);

    }

    private void BlockUserList() {
        Intent mIntent = new Intent(getApplicationContext(), BlockUserListActivity.class);
        mIntent.putExtra("userID", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

    private void ChnagePass() {
        Intent mIntent = new Intent(getApplicationContext(), ChangePassActivity.class);
        mIntent.putExtra("userName", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        mIntent.putExtra("userPass", appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD));
        startActivity(mIntent);
    }

    private void GoemployeeEdit() {
        Intent mIntent = new Intent(this, UserEditActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Edit EmployeeList");
        startActivity(mIntent);
    }

    private void CompanyDetails() {
        Intent mIntent = new Intent(getApplicationContext(), ProfileDetailsActivity.class);
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        mIntent.putExtra("UserID", userId);
        startActivity(mIntent);
    }

    private void gotoUserLiveList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Employeelive list");
        startActivity(mIntent);
    }

    private void gotoDateWizeList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Date");
        startActivity(mIntent);

    }

    private void gotoEmpAttendanceList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Employee");
        startActivity(mIntent);
    }


    private void gotoLeaderAttendanceList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "1");
        mIntent.putExtra("userName", "Leader");
        startActivity(mIntent);
    }


    private void gotoLeaderList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "1");
        mIntent.putExtra("userName", "Leader List");
        startActivity(mIntent);
    }

    private void gotoUserList() {
        Intent mIntent = new Intent(this, UserListActivity.class);
        mIntent.putExtra("userType", "0");
        mIntent.putExtra("userName", "Set Geo");
        startActivity(mIntent);


    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Logout App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appSessionManager.logoutUser();
                        startActivity(new Intent(CompanyActivity.this, LoginActivity.class));
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void callCreateCompany() {
        startActivity(new Intent(this, CreateLeaderActivity.class));
    }


    private void startMapActivity(String apiKey, String country, String language, String[] supportedAreas, String radius) {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(SimplePlacePicker.API_KEY, apiKey);
        bundle.putString(SimplePlacePicker.COUNTRY, country);
        bundle.putString(SimplePlacePicker.LANGUAGE, language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS, supportedAreas);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS, supportedAreas);
        bundle.putString(SimplePlacePicker.RADIUS, radius);

        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }

    private void selectLocationOnMap() {
        String apiKey = getString(R.string.places_api_key);
        String mCountry = "bgd";
        String mLanguage = "en";
        String radius = "0";
        // String [] mSupportedAreas = mSupportedAreaEt.getText().toString().split(",");
        String[] mSupportedAreas = {"", ""};
        startMapActivity(apiKey, mCountry, mLanguage, mSupportedAreas, radius);
    }

    private void updateDataServer(Intent data) {
        latitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, -1));
        longlitude = String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, -1));
        address = data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
        String radiusData = data.getStringExtra(SimplePlacePicker.RADIUS);


        long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = Long.toString(tsLong);

        submitCompanyGeo(userId, timeStamp, latitude + "," + longlitude, radiusData, userType);


//        Toast.makeText(this, latitude+" , "+longlitude, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(this, "radius"+radiusData, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) updateDataServer(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ;
            //     selectLocationOnMap();
        }
    }

    //check for location permission
    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String permissionName) {
        if (ContextCompat.checkSelfPermission(activity,
                permissionName)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{permissionName},
                    requestCode);
        } else {
            return true;
        }
        return false;
    }


    public void submitCompanyGeo(String userId, String timestamp, String latlong, String radius, String type) {
        if (internetConnection.isInternetAvailable(CompanyActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitCompanyGeo(userId, latlong, timestamp, radius, type).enqueue(new Callback<GeoSubmitResponse>() {
                @Override
                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(CompanyActivity.this, "" + response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(CompanyActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoSubmitResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getServerData(String userId) {
        if (internetConnection.isInternetAvailable(CompanyActivity.this)) {

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
                            paymentDue = response.body().getDueWarningDate();
                            dueBlock = response.body().getDueBlock();

                            if (serverStatus != 0){
                               Intent mIntent = new Intent(CompanyActivity.this, ServerMaintainActivity.class);
                               startActivity(mIntent);
                               finish();
                            }else {
                                if (userBlock != 0){
                                    Intent mIntent = new Intent(CompanyActivity.this, UserBlockActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                }else{
                                    if (dueBlock == 1){

                                    }else {
                                        if (paymentDue != 0){
                                            new AlertDialog.Builder(CompanyActivity.this)
                                                    .setTitle("Attention")
                                                    .setMessage("Please clear your due within "+paymentDue+" working Days.")
                                                    .setIcon(R.drawable.ic_logo)

                                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Continue with delete operation
                                                        }
                                                    })

                                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                                    .setNegativeButton(android.R.string.no, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    }
                                }

                            }
                            initListener(); 


                        } else {
                            Toast.makeText(CompanyActivity.this, "some thing went to wrong!", Toast.LENGTH_SHORT).show();
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
        if (internetConnection.isInternetAvailable(CompanyActivity.this)) {
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
                        String uName = response.body().getName();
                        String uMobile = response.body().getMobile();
                        userName.setText(uName);
                        userEmail.setText(uMobile);
                        Glide.with(getApplicationContext())
                                .load(BuildConfig.BASE_URL+ "" + response.body().getPhoto())
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(imgview);

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