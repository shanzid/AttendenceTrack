package com.attendance.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.activity.ProfileDetails.EmployeeDetailsActivity;
import com.attendance.tracker.data.ChnagePassResponse;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {
    TextInputEditText oldpass,newpass,confirmpass;
private AppCompatButton submit;
AppSessionManager appSessionManager;
CheckInternetConnection checkInternetConnection;
String userName,userPass;
ImageView back,pass1,pass2,pass3;

    boolean isPasswordVisible = false;
    boolean isPasswordVisible2 = false;
    boolean isPasswordVisible3 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        initView();
        InitFunc();
        InitClick();
    }

    private void InitClick() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            finish();

            }
        });

      submit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

                  validateData();

          }
      });
    }

    private void validateData() {

        if (oldpass.getText().toString().isEmpty()) {
            oldpass.setError("Enter old Password!");
            oldpass.requestFocus();
        } else if (newpass.getText().toString().isEmpty()) {
            newpass.setError("Enter New Password!");
            newpass.requestFocus();
        }  else if (confirmpass.getText().toString().isEmpty()) {
            confirmpass.setError("Enter Confirm Password!");
            confirmpass.requestFocus();


        } else {
            String OldPass = oldpass.getText().toString().trim();
            String NewPass = newpass.getText().toString().trim(); // company name is name
            String ConfirmPass = confirmpass.getText().toString().trim();



            callChagePass(OldPass, NewPass, ConfirmPass);
        }




}

    private void callChagePass(String oldPass, String newPass, String confirmPass) {
    //    String userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
      //  String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);

        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();

        APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
        mApiService.ChangePass(""+userName,""+userPass,""+oldPass,""+newPass,""+confirmPass
                ).enqueue(new Callback<ChnagePassResponse>() {
            @Override
            public void onResponse(Call<ChnagePassResponse> call, Response<ChnagePassResponse> response) {

                if (response.isSuccessful()) {
                  String errormes=  response.body().getErrorReport();

                    Toast.makeText(ChangePassActivity.this, ""+errormes, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                  if(errormes=="Successfully Password Change")
                      goToPage();

                } else {

                    Toast.makeText(ChangePassActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }
            @Override
            public void onFailure(Call<ChnagePassResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();


            }
        });
    }

    private void goToPage() {
        Intent mIntent = new Intent(getApplicationContext(), EmployeeDetailsActivity.class);
        startActivity(mIntent);
    }


    private void InitFunc() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        Intent mIntent = getIntent();
        userName = mIntent.getStringExtra("userName");
        userPass = mIntent.getStringExtra("userPass");
    }

    private void initView() {
        oldpass=findViewById(R.id.oldPassword);
        back=findViewById(R.id.back);
        newpass=findViewById(R.id.NewPassword);
        confirmpass=findViewById(R.id.ConfirmPassword);
        submit=findViewById(R.id.changeButton);
        pass1=findViewById(R.id.showPassword);
        pass2=findViewById(R.id.showPassword1);
        pass3=findViewById(R.id.showPassword2);



        findViewById(R.id.showPassword).setOnClickListener(view ->showCPassword() );
        findViewById(R.id.showPassword1).setOnClickListener(view ->showCPassword2());
        findViewById(R.id.showPassword2).setOnClickListener(view ->showCPassword3() );
    }

    private void showCPassword() {
        if (isPasswordVisible) {
            String cpassword = oldpass.getText().toString();
            oldpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            oldpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            oldpass.setText(cpassword);
            oldpass.setSelection(oldpass.length());
            pass1.setImageResource(R.drawable.eye);

        } else {
            String cpassword = oldpass.getText().toString();
            oldpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            oldpass.setInputType(InputType.TYPE_CLASS_TEXT);
            oldpass.setText(cpassword);
            oldpass.setSelection(oldpass.length());
            pass1.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }
    private void showCPassword2() {
        if (isPasswordVisible2) {
            String cpassword = newpass.getText().toString();
            newpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            newpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newpass.setText(cpassword);
            newpass.setSelection(newpass.length());
            pass2.setImageResource(R.drawable.eye);

        } else {
            String cpassword = newpass.getText().toString();
            newpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            newpass.setInputType(InputType.TYPE_CLASS_TEXT);
            newpass.setText(cpassword);
            newpass.setSelection(newpass.length());
            pass2.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible2= !isPasswordVisible2;

    }
    private void showCPassword3() {
        if (isPasswordVisible3) {
            String cpassword = confirmpass.getText().toString();
            confirmpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmpass.setText(cpassword);
            confirmpass.setSelection(confirmpass.length());
            pass3.setImageResource(R.drawable.eye);

        } else {
            String cpassword = confirmpass.getText().toString();
            confirmpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmpass.setInputType(InputType.TYPE_CLASS_TEXT);
            confirmpass.setText(cpassword);
            confirmpass.setSelection(confirmpass.length());
            pass3.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible3= !isPasswordVisible3;

    }

}