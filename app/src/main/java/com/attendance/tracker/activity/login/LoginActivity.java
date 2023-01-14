package com.attendance.tracker.activity.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.ForgotpassActivity;
import com.attendance.tracker.MainActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.company.CompanyActivity;
import com.attendance.tracker.activity.master.CreateCompanyActivity;
import com.attendance.tracker.activity.master.MasterActivity;
import com.attendance.tracker.activity.user.MapTestUserActivity;
import com.attendance.tracker.activity.user.UserMainActivity;
import com.attendance.tracker.agent.AgentDashboardActivity;
import com.attendance.tracker.agent.SalesReportActivity;
import com.attendance.tracker.data.LoginData;
import com.attendance.tracker.data.SalesReport;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText et_name,et_password;
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    AppCompatImageView map,man;
    boolean isPasswordVisible = false;
    ImageView passwordView;
    AppCompatTextView createAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initFunc();
        initListener();
    }

    private void initVariable() {
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }

    private void initView() {


        setContentView(R.layout.activity_login);

        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        passwordView = findViewById(R.id.showPassword);
      //  et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

    }

    private void initFunc() {

    }

    private void initListener() {
        findViewById(R.id.login).setOnClickListener(view -> gotoNext());

        findViewById(R.id.showPassword).setOnClickListener(view -> showPassword());

        findViewById(R.id.forgotpassTV).setOnClickListener(view -> gotoForgotPassword());

        findViewById(R.id.tvRegister).setOnClickListener(view -> gotoRegistration());

    }

    private void gotoForgotPassword() {
        startActivity(new Intent(this, ForgotpassActivity.class));
    }

    private void gotoRegistration() {
        startActivity(new Intent(this, CreateCompanyActivity.class));
    }

    private void showPassword() {
        if (isPasswordVisible) {
            String pass = et_password.getText().toString();
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et_password.setText(pass);
            et_password.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.eye);

        } else {
            String pass = et_password.getText().toString();
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            et_password.setInputType(InputType.TYPE_CLASS_TEXT);
            et_password.setText(pass);
            et_password.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void gotoNext() {
        if (et_name.getText().toString().equals("")){
            Toast.makeText(this, "Enter Your User Name", Toast.LENGTH_SHORT).show();

        }else if (et_password.getText().toString().equals("")){
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();

        }else {
            String userName = et_name.getText().toString();
            String userPassword = et_password.getText().toString();
            callLoginApi(userName,userPassword);
        }
    }

    private void callLoginApi(String userName, String userPassword) {
        if (internetConnection.isInternetAvailable(LoginActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.goLogin(userName,userPassword).enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            dialog.dismiss();
                            appSessionManager.createLoginSession(""+response.body().getId(),""+userName,
                                    ""+userPassword,""+response.body().getCatagoryType(),""+response.body().getMobile(),
                                    ""+response.body().getAddress(),""+response.body().getImageUrl());

                            switch (response.body().getCatagoryType()){
                                case "0":
                                    gotoUser();
                                    break;
                                case "1":
                                    gotoLeader();
                                    break;
                                case "2":
                                    gotoCompany();
                                    break;
                                case "3":
                                    gotoMasterAdmin();
                                    break;
                                case "4":
                                    gotoAgent();
                                    break;
                                default:
                                    break;
                            }


                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void gotoAgent() {
        startActivity(new Intent(this, AgentDashboardActivity.class));
        finish();
    }

    private void gotoCompany() {
        startActivity(new Intent(this,CompanyActivity.class));
        finish();
    }
    private void gotoUser() {
/*        startActivity(new Intent(this, UserMainActivity.class));
        finish();*/


        Intent mIntent = new Intent(this, MapTestUserActivity.class);
        // mIntent.putExtra("userId",appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }
    private void gotoMasterAdmin() {
        startActivity(new Intent(this, MasterActivity.class));
        finish();
    }
    private void gotoLeader() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
