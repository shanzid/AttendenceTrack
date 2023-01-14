package com.attendance.tracker.activity.ProfileDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.master.CompanyEdit.CompanyProfileEditActivity;
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

public class ProfileDetailsActivity extends AppCompatActivity {
    AppCompatTextView name, mobile, email, address, nid, father_name, mother_name, gender,mobile2ID;
    AppCompatImageView back;
    private ProgressBar progressBar;
    TextView user_name;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    CircleImageView img;
    private String userType, userName;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        ids();
        initVariable();
        Clicklistener();
    }



    private void Clicklistener() {
        findViewById(R.id.tvCompany).setOnClickListener(view -> GoEdit());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GoEdit() {
        Intent mIntent = new Intent(getApplicationContext(), CompanyProfileEditActivity.class);
        mIntent.putExtra("UserID", userID);
        startActivity(mIntent);
    }

    private void initVariable() {

        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
        //userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        Intent mIntent = getIntent();
        userID = mIntent.getStringExtra("UserID");


    }

    private void ids() {
        mobile2ID = findViewById(R.id.mobile2ID);
        user_name = findViewById(R.id.nameID);
        img = findViewById(R.id.profileIMG);
        mobile = findViewById(R.id.mobileID);
        email = findViewById(R.id.emailID);
        address = findViewById(R.id.addressID);
        nid = findViewById(R.id.nidID);
        father_name = findViewById(R.id.fathernameID);
        mother_name = findViewById(R.id.mother_nameID);
        gender = findViewById(R.id.genderID);
      //  progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
    }

    public void getData(String userID) {

        if (internetConnection.isInternetAvailable(ProfileDetailsActivity.this)) {
          //  progressBar.setVisibility(View.VISIBLE);
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

                        ShowAllData(response.body());
                    }
                }


                @Override
                public void onFailure(Call<ProfileDetailsData> call, Throwable t) {
                    t.printStackTrace();
                  //  progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void ShowAllData(ProfileDetailsData report) {

        Glide.with(getApplicationContext())
                .load(BuildConfig.BASE_URL + "" + report.getPhoto())
                .into(img);

        appSessionManager.createLoginSession(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME),
                appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD), appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY), appSessionManager.getUserDetails().get(AppSessionManager.KEY_MOBILE),
                appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERADDRESS),""+ report.getPhoto());





        user_name.setText(report.getName());
        mobile.setText(report.getMobile());
        email.setText(report.getEmail());
        address.setText(report.getAddress());
        nid.setText(report.getNid());
        father_name.setText(report.getFatherName());
        mother_name.setText(report.getMotherName());
        gender.setText(report.getGender());
        mobile2ID.setText(report.getMobile2());
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", report.getMobile(), null));
                startActivity(intent);
            }
        });
        mobile2ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", report.getMobile2(), null));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        getData(userID);
        super.onResume();
    }
}