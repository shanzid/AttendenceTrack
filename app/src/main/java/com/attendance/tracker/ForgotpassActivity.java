package com.attendance.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.activity.login.VerifyPhoneActivity;
import com.attendance.tracker.data.ForgetPasswordModel;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotpassActivity extends AppCompatActivity{
    EditText et_name;

    CheckInternetConnection internetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        internetConnection = new CheckInternetConnection();
        et_name = findViewById(R.id.et_name);


        findViewById(R.id.submit).setOnClickListener(view -> callSubmitApi());
    }

    private void callSubmitApi() {
        if (internetConnection.isInternetAvailable(ForgotpassActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            final String userName = et_name.getText().toString().trim();
            if (userName.length() > 0) {
                APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
                mApiService.sendVerifyCode(userName).enqueue(new Callback<ForgetPasswordModel>() {
                    @Override
                    public void onResponse(Call<ForgetPasswordModel> call, Response<ForgetPasswordModel> response) {
                        if (response.isSuccessful()) {
                            int errCode = response.body().getError();
                            if (errCode == 0) {
                                if (response.body().getMethod() == 1) {
                                    Intent intent = new Intent(ForgotpassActivity.this,  VerifyPhoneActivity.class);
                                    intent.putExtra("UNAME", userName);
                                    intent.putExtra("email", response.body().getMobile());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(ForgotpassActivity.this, VerifyPhoneActivity.class);
                                    intent.putExtra("UNAME", userName);
                                    intent.putExtra("email", response.body().getEmail());
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(ForgotpassActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FORGET_PASS", response.code() + "! Error");
                            Toast.makeText(ForgotpassActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ForgetPasswordModel> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(ForgotpassActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Log.d("FORGET_PASS", "onFailure: " + t.getMessage());
                    }
                });
            } else {
                dialog.dismiss();
                et_name.setError("User fame field is invalid");
                et_name.requestFocus();
                return;
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem", Snackbar.LENGTH_SHORT).show();
        }
    }
}