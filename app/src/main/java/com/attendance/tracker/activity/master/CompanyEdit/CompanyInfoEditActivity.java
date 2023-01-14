package com.attendance.tracker.activity.master.CompanyEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.attendance.tracker.R;
import com.attendance.tracker.activity.NewCompanyList.CompanyDetailsData;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyInfoEditActivity extends AppCompatActivity {
    private TextInputEditText company, mobile, email, address, cost, employee, mobile2;
    AppCompatImageView back;
    AppCompatButton save;
    AppSessionManager appSessionManager;
    CheckInternetConnection checkInternetConnection;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_edit);

        initVariables();
        initView();
        initFunc();
        initListener();
    }

    private void initVariables() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        Intent mIntent = getIntent();
        userID = mIntent.getStringExtra("UserID");
    }

    private void initView() {
        company = findViewById(R.id.CompanyNameET);
        mobile = findViewById(R.id.ETno1);
        mobile2 = findViewById(R.id.ETno2);
        email = findViewById(R.id.ETemail);
        address = findViewById(R.id.ETaddress);
        cost = findViewById(R.id.ETnid);
        employee = findViewById(R.id.ETfather);
        //button
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);//imageview
        //image upload


    }

    private void initFunc() {
        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),userID);
    }

    private void initListener() {
        back.setOnClickListener(view -> finish());
        save.setOnClickListener(view -> save());
    }

    private void save() {
        validateData();
    }

    public void getData(String userID,String ID) {
        if (checkInternetConnection.isInternetAvailable(this)) {
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

        company.setText(report.getCompanyName());
      //  name.setText(report.getName());
        email.setText(report.getEmail());
        address.setText(report.getAddress());
        mobile.setText(report.getMobile());
        cost.setText(report.getMonthlyCharge());
        mobile2.setText(report.getMobile2());
        employee.setText(report.getEmployee());
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
    }


    private void validateData() {
        if (company.getText().toString().isEmpty()) {
            company.setError("Enter Company");
            company.requestFocus();
        } else if (mobile.getText().toString().isEmpty()) {
            mobile.setError("Enter Mobile");
            mobile.requestFocus();
        }
//        else if (mobile2.getText().toString().isEmpty()) {
//            mobile2.setError("Enter Mobile");
//            mobile2.requestFocus();
//        }
        else if (address.getText().toString().isEmpty()) {
            address.setError("Enter Address");
            address.requestFocus();
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Enter Email");
            email.requestFocus();
        } else {
            String Email = email.getText().toString().trim();
            String Company = company.getText().toString().trim(); // company name is name
            String Mobile = mobile.getText().toString().trim();
            String Mobile2 = mobile2.getText().toString().trim();
            String Address = address.getText().toString().trim();
            String costs = cost.getText().toString().trim();
            String employees = employee.getText().toString().trim();
            callCompanyEditApi(Company, Email, Mobile, Address, costs, employees,Mobile2);
        }
    }

    private void callCompanyEditApi(String company, String email, String mobile, String address,
                                    String cost, String employee, String ETno2) {
        String userName = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME);
        String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);

        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();

        APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
        mApiService.EditCompanyProfile(userName,userPassword,userID,company,email,
                address,mobile,ETno2,cost,employee).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    int errCount = response.body().get("error").getAsInt();
                    if (errCount == 0) {
                        dialog.dismiss();
                        GotoMain();
                    }
                } else {
                    Log.e("COUNTRY_LIST", "Error :" + response.code());
                    Toast.makeText(CompanyInfoEditActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                Toast.makeText(CompanyInfoEditActivity.this, "Successfuly done!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private void GotoMain() {
        Intent mIntent = new Intent(this, UserEditActivity.class);
        mIntent.putExtra("userType","2");
        mIntent.putExtra("userName","Company Edit");
        startActivity(mIntent);
        finish();
    }
}