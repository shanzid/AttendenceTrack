package com.attendance.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.data.HelpModel;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    AppCompatTextView titleTV,addressTv,mobile,mobile2,email;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_development);
        initView();
        initVariables();
        initFunc();
    }

    private void initView() {
        titleTV = findViewById(R.id.companynameID);
        addressTv = findViewById(R.id.NameID);
        mobile = findViewById(R.id.mobileID);
        mobile2 = findViewById(R.id.mobile2ID);
        email = findViewById(R.id.emailID);
    }

    private void initVariables() {
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
    }

    private void initFunc() {
        findViewById(R.id.back).setOnClickListener(view -> finish());

        getHelp(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
    }


    public void getHelp(String userId){
        if (internetConnection.isInternetAvailable(SupportActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getHelpList(userId).enqueue(new Callback<HelpModel>() {
                @Override
                public void onResponse(Call<HelpModel> call, Response<HelpModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            setAllData(response.body());
                            dialog.dismiss();

                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(SupportActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<HelpModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setAllData(HelpModel helpModel) {
        titleTV.setText(helpModel.getTitle());
        addressTv.setText(helpModel.getAddress());
        mobile.setText(helpModel.getMobile());
        mobile2.setText(helpModel.getMobile2());
        email.setText(helpModel.getEmail());


        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", helpModel.getMobile(), null));
                startActivity(intent);
            }
        });

        mobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", helpModel.getMobile2(), null));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{helpModel.getEmail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "Body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SupportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}