package com.attendance.tracker.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends AppCompatActivity {
    public static int APP_REQUEST_CODE_SMS = 98;
    CheckInternetConnection internetConnection;

    String email = "";
    String tempUName = "";
    TextView emailNotice;
    EditText verifyCodeEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        internetConnection = new CheckInternetConnection();
        Intent getData = getIntent();
        email = getData.getStringExtra("email");
        tempUName = getData.getStringExtra("UNAME");
        verifyCodeEt = findViewById(R.id.et_otp);
        emailNotice = findViewById(R.id.emailText);
        emailNotice.setText("We have sent a verification code at "+email+". Check your Inbox ");


        findViewById(R.id.submit).setOnClickListener(view -> submitCode());

    }

    public void submitCode() {
        if (internetConnection.isInternetAvailable(VerifyPhoneActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            final String code = verifyCodeEt.getText().toString().trim();
            if (code.length() > 0) {
                APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
                mApiService.submitVerifyCode(tempUName,code).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            int errCode = response.body().get(ConstantValue.api_res_key_error).getAsInt();
                            if (errCode == 0) {
                                Intent intent = new Intent(VerifyPhoneActivity.this, NewPasswordSetActivity.class);
                                intent.putExtra("UNAME", tempUName);
                                //intent.putExtra("code", code);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(VerifyPhoneActivity.this, response.body().get(ConstantValue.api_res_key_error_report).getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FORGET_PASS", response.code() + "! Error");
                            Toast.makeText(VerifyPhoneActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(VerifyPhoneActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Log.d("FORGET_PASS", "onFailure: " + t.getMessage());
                    }
                });
            } else {
                dialog.dismiss();
                verifyCodeEt.setError("Empty field is not allowed!!");
                verifyCodeEt.requestFocus();
                return;
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem", Snackbar.LENGTH_SHORT).show();
        }
    }
}
