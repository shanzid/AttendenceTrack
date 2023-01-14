package com.attendance.tracker.activity.NewCompanyList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class CompanyDetailsActivity extends AppCompatActivity {
    AppCompatTextView company_name,name, mobile,mobile2, email, address,geoloc,joindate,monthly_charge,current_due,agent_name ;
    AppCompatImageView back;
    private ProgressBar progressBar;
    TextView user_name;
    AppCompatButton chnagepass;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    CircleImageView img;
    private String userType, userName;
    String userID,companyID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        Intent mIntent = getIntent();
        userID = mIntent.getStringExtra("UserID");
      //  companyID = mIntent.getStringExtra("companyId");
        //Toast.makeText(this, ""+userID, Toast.LENGTH_SHORT).show();
        ids();
        initVariable();
        Clicklistener();
        // initFunc();
    }


    private void Clicklistener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoEdit();
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




        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),userID);

    }

    private void ids() {
        company_name = findViewById(R.id.companynameID);
        img = findViewById(R.id.profileIMG);
        name = findViewById(R.id.NameID);
        email = findViewById(R.id.emailID);
        address = findViewById(R.id.addressID);
        mobile2 = findViewById(R.id.mobile2ID);
        mobile = findViewById(R.id.mobileID);
        geoloc = findViewById(R.id.geoID);
        joindate = findViewById(R.id.joindateID);
        monthly_charge = findViewById(R.id.monthlychargeID);
        agent_name = findViewById(R.id.agentnameID);
       // current_due = findViewById(R.id.currentID);
        back = findViewById(R.id.back);

    }

    public void getData(String userID,String ID) {
        if (internetConnection.isInternetAvailable(CompanyDetailsActivity.this)) {
            // progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getCompanydetails(userID,ID).enqueue(new Callback<CompanyDetailsData>() {
                @Override
                public void onResponse(Call<CompanyDetailsData> call, Response<CompanyDetailsData> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful())
                            //  progressBar.setVisibility(View.GONE);

                            dialog.dismiss();
                        ShowAllData(response.body());
                    }
                }


                @Override
                public void onFailure(Call<CompanyDetailsData> call, Throwable t) {
                    t.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void ShowAllData(CompanyDetailsData report) {

        Glide.with(getApplicationContext())
                .load(BuildConfig.BASE_URL + "" + report.getPhoto())
                .into(img);

        company_name.setText(report.getCompanyName());
        name.setText(report.getName());
        email.setText(report.getEmail());
        address.setText(report.getAddress());
        mobile.setText(report.getMobile());
        mobile2.setText(report.getMobile2());
        geoloc.setText(report.getGeoLocation());
        joindate.setText(report.getJoinDate());
        monthly_charge.setText(report.getMonthlyCharge());
        agent_name.setText(report.getAgentName());
       // current_due.setText(report.getCurrentDue());
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", report.getMobile(), null));
                startActivity(intent);
            }
        });
        mobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", report.getMobile2(), null));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{report.getEmail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "Body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CompanyDetailsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}