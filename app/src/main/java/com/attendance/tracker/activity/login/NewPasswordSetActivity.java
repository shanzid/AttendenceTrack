package com.attendance.tracker.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordSetActivity extends AppCompatActivity {
    CheckInternetConnection internetConnection;
    String tempUName = "";
    String code = "";
    TextInputEditText editTextNewPassword;
    TextInputEditText editTextReNewPassword;
    boolean isPasswordVisible = false;
    boolean isPasswordVisibleCon = false;
    ImageView passwordView,passwordViewTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password_set);
        Intent getData = getIntent();
        tempUName = getData.getStringExtra("UNAME");
        // code = getData.getStringExtra("code");
        internetConnection = new CheckInternetConnection();
        editTextNewPassword =  findViewById(R.id.et_password);
        editTextReNewPassword =  findViewById(R.id.et_Confirm_password);
        passwordView = findViewById(R.id.showPassword);
        passwordViewTwo = findViewById(R.id.showPasswordTwo);
        findViewById(R.id.resetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = editTextNewPassword.getText().toString().trim();
                String newRePassword = editTextReNewPassword.getText().toString().trim();
                if (newPassword.isEmpty() || newPassword.length() < 6) {
                    editTextNewPassword.setError("Minimum 6 characters!");
                    editTextNewPassword.requestFocus();
                    return;
                }

                if (!newPassword.equals(newRePassword)) {
                    editTextReNewPassword.setError("Password does not matched!");
                    editTextReNewPassword.requestFocus();
                    return;
                }

                submitNewPassword(newPassword, newRePassword);
            }
        });

        findViewById(R.id.showPassword).setOnClickListener(view -> showPassword());
        findViewById(R.id.showPasswordTwo).setOnClickListener(view -> showPasswordConfirm());

    }

    private void showPasswordConfirm() {
        if (isPasswordVisibleCon) {
            String pass = editTextReNewPassword.getText().toString();
            editTextReNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editTextReNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextReNewPassword.setText(pass);
            editTextReNewPassword.setSelection(pass.length());
            passwordViewTwo.setImageResource(R.drawable.eye);

        } else {
            String pass = editTextReNewPassword.getText().toString();
            editTextReNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editTextReNewPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            editTextReNewPassword.setText(pass);
            editTextReNewPassword.setSelection(pass.length());
            passwordViewTwo.setImageResource(R.drawable.hidden);

        }
        isPasswordVisibleCon= !isPasswordVisibleCon;

    }
    private void showPassword() {
        if (isPasswordVisible) {
            String pass = editTextNewPassword.getText().toString();
            editTextNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextNewPassword.setText(pass);
            editTextNewPassword.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.eye);

        } else {
            String pass = editTextNewPassword.getText().toString();
            editTextNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            editTextNewPassword.setText(pass);
            editTextNewPassword.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void submitNewPassword(String newPass, String newRePass) {
        if (internetConnection.isInternetAvailable(this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitForgetPassword(tempUName, newPass, newRePass).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        int errCount = response.body().get(ConstantValue.api_res_key_error).getAsInt();
                        if (errCount == 0) {
                            Toast.makeText(NewPasswordSetActivity.this, response.body().get(ConstantValue.api_res_key_error_report).getAsString(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewPasswordSetActivity.this, "Please set valid password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SUBMIT_PASS", response.code() + "! Error");
                        Toast.makeText(NewPasswordSetActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(NewPasswordSetActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.d("SUBMIT_PASS", "onFailure: " + t.getMessage());
                }
            });
        }else {
            Toast.makeText(this, "Internet Problem", Toast.LENGTH_SHORT).show();
        }
    }
}
