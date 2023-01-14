package com.attendance.tracker.activity.company;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.data.PaymentModel;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSubmitActivity extends AppCompatActivity {
    String accountNumber,invoice,dueID,method;
    TextView tvAccNumber;
    EditText senderNumber,title,tranxID;
    AppSessionManager appSessionManager;
    CheckInternetConnection internetConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_submit);
        initVariable();
        initView();
        initFunction();

    }

    private void initVariable() {
        Intent mIntent = getIntent();
        accountNumber = mIntent.getStringExtra("acc");
        method = mIntent.getStringExtra("method");
        invoice = mIntent.getStringExtra("invoice");
        dueID = mIntent.getStringExtra("dueId");

        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }

    private void initView() {
        tvAccNumber = findViewById(R.id.tvAccNumber);
        senderNumber = findViewById(R.id.sNumber);
        title = findViewById(R.id.accTitle);
        tranxID = findViewById(R.id.tranxID);
        tvAccNumber.setText(method + " : "+accountNumber);

    }

    private void initFunction(){
        findViewById(R.id.submit).setOnClickListener(view -> validateData());
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    private void validateData() {
        if (senderNumber.getText().toString().equals("")){
            Toast.makeText(this, "Please Enter Sender Number", Toast.LENGTH_SHORT).show();
        }else {
            String sNumber = senderNumber.getText().toString();
            String accTitle = title.getText().toString();
            String tnx = tranxID.getText().toString();



            callPaymentSubmitApi(sNumber,accTitle,tnx);
        }
    }

    private void callPaymentSubmitApi(String sNumber, String accTitle, String tnx) {
        if (internetConnection.isInternetAvailable(this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitDue(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),method,sNumber,invoice,dueID,accTitle,tnx).enqueue(new Callback<JsonObject>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                      if (response.body().get("error").getAsInt() == 0){
                          Toast.makeText(PaymentSubmitActivity.this, ""+response.body().get("error_report").getAsString(), Toast.LENGTH_SHORT).show();
                          gotoDueList();
                      }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void gotoDueList() {
        startActivity(new Intent(PaymentSubmitActivity.this,CompanyDueListActivity.class));
        finish();
    }


}