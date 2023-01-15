package com.attendance.tracker.activity.master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.data.JoinResponse;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCompanyActivity extends AppCompatActivity {
    private TextInputEditText company,mobile,mobile_2,user_name,email,address,pass,Cpass,employee,cost,et_referCode;
    Spinner division,district,thana,country;
    AppCompatImageView back;
    AppCompatButton save;
    ImageView passwordView,getPasswordView;
    boolean isPasswordVisible = false;
    JsonArray countryArr;
    private List<String> countryList = new ArrayList<>();

    JsonArray divisionArr;
    private List<String> divisionList = new ArrayList<>();

    JsonArray districtArr;
    private List<String> districtList = new ArrayList<>();

    JsonArray thanaArr;
    private List<String> thanaList = new ArrayList<>();

    CheckInternetConnection checkInternetConnection;
    AppSessionManager appSessionManager;

    String countryID;
    String divID = "";
    String disID = "";
    String thanaID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company_new);

        initVariables();
        initView();
        initFunc();
        initListener();
    }
    private void initVariables() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }
    private void initView() {
        //EditText
        company = findViewById(R.id.CompanyNameET);
        mobile = findViewById(R.id.ETno1);
        mobile_2 = findViewById(R.id.ETmob2);
        user_name = findViewById(R.id.ETUser_name);
        email = findViewById(R.id.ETemail);
        pass = findViewById(R.id.ETpass);
        Cpass = findViewById(R.id.ETconfirmpass);
        employee = findViewById(R.id.ETemployee);
        cost = findViewById(R.id.ETCost);
        //spinner
        address = findViewById(R.id.ETaddress);
        division = findViewById(R.id.sp_division);
        country = findViewById(R.id.sp_country);
        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);
        //button
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);//imageview
        passwordView = findViewById(R.id.showPassword);
        getPasswordView = findViewById(R.id.showPassword2);
        et_referCode = findViewById(R.id.et_referCode);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String refercode = prefs.getString("refer_code",null);
        if (refercode.isEmpty()){

        }else {
            et_referCode.setText(refercode);
        }

    }
    private void initFunc() {
        loadCountryList();

    }

    private void initListener() {
        back.setOnClickListener(view -> finish());
        save.setOnClickListener(view -> save());
        findViewById(R.id.showPassword).setOnClickListener(view -> showPassword());
        findViewById(R.id.showPassword2).setOnClickListener(view -> showCPassword());

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDivisionData(country.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDistrictData(country.getSelectedItemPosition(), division.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadThanaData(country.getSelectedItemPosition(), division.getSelectedItemPosition(), district.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        thana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              //  loadUnionData(spinnerCountry.getSelectedItemPosition(), spinnerDivision.getSelectedItemPosition(), spinnerDistrict.getSelectedItemPosition(), spinnerThana.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void showCPassword() {
        if (isPasswordVisible) {
            String cpassword = Cpass.getText().toString();
            Cpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            Cpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            Cpass.setText(cpassword);
            Cpass.setSelection(Cpass.length());
            getPasswordView.setImageResource(R.drawable.eye);

        } else {
            String cpassword = Cpass.getText().toString();
            Cpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            Cpass.setInputType(InputType.TYPE_CLASS_TEXT);
            Cpass.setText(cpassword);
            Cpass.setSelection(Cpass.length());
            getPasswordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void showPassword() {
        if (isPasswordVisible) {
            String password = pass.getText().toString();
            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pass.setText(password);
            pass.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.eye);

        } else {
            String password = pass.getText().toString();
            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pass.setInputType(InputType.TYPE_CLASS_TEXT);
            pass.setText(password);
            pass.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void save() {
        validateData();
    }


    private void validateData() {
        JsonObject countryObj = countryArr.get(country.getSelectedItemPosition() - 1).getAsJsonObject();
        countryID = countryObj.get("Country_ID").getAsString();
        divID = divisionArr.get(division.getSelectedItemPosition() - 1).getAsJsonObject().get("Division_ID").getAsString();
        disID = districtArr.get(district.getSelectedItemPosition() - 1).getAsJsonObject().get("District_ID").getAsString();
        thanaID = thanaArr.get(thana.getSelectedItemPosition() - 1).getAsJsonObject().get("Thana_ID").getAsString();

        if (company.getText().toString().isEmpty()) {
            company.setError("Enter Company");
            company.requestFocus();
        } else if (mobile.getText().toString().isEmpty()) {
            mobile.setError("Enter Mobile");
            mobile.requestFocus();
        } else if (address.getText().toString().isEmpty()) {
            address.setError("Enter Address");
            address.requestFocus();
        } else if (pass.getText().toString().isEmpty()) {
            pass.setError("Enter Password");
            pass.requestFocus();
        } else if (Cpass.getText().toString().isEmpty()) {
            Cpass.setError("Enter Confirm Password");
            Cpass.requestFocus();
        } else if (user_name.getText().toString().isEmpty()) {
            user_name.setError("Enter User Name");
            user_name.requestFocus();
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Enter Email");
            email.requestFocus();
        } else if (employee.getText().toString().isEmpty()) {
            employee.setError("Enter Employee");
            employee.requestFocus();
        } else if (cost.getText().toString().isEmpty()) {
            cost.setError("Enter Cost");
            cost.requestFocus();
        } else if (countryID.equals("")) {
            Toast.makeText(this, "Select Country", Toast.LENGTH_SHORT).show();
        } else if (divID.equals("")) {
            Toast.makeText(this, "Select Division", Toast.LENGTH_SHORT).show();
        } else if (disID.equals("")) {
            Toast.makeText(this, "Select District", Toast.LENGTH_SHORT).show();
        } else if (thanaID.equals("")) {
            Toast.makeText(this, "Select Thana", Toast.LENGTH_SHORT).show();

        } else {
            String username = user_name.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String Company = company.getText().toString().trim();
            String Mobile = mobile.getText().toString().trim();
            String Mobile2 = mobile_2.getText().toString().trim();
            String Address = address.getText().toString().trim();
            String Pass = pass.getText().toString().trim();
            String Conpass = Cpass.getText().toString().trim();
            String employees = employee.getText().toString().trim();
            String costs = cost.getText().toString().trim();

            callCompanyCreateApi(Company, username, Email, Mobile, Mobile2, Address, Pass, Conpass, employees, costs);
        }

                }





    private void callCompanyCreateApi(String company, String username, String email, String mobile, String mobile2, String address,
                                      String pass, String conpass,String employees,String costs) {
        String userName = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME);
        String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);

        Log.d("userName",""+userName+"userPass"+userPassword);


        if (checkInternetConnection.isInternetAvailable(CreateCompanyActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.makeCompany(""+userName,""+userPassword,""+mobile,""+mobile2,
                    ""+username,""+email,""+countryID
            ,""+divID,""+disID,""+thanaID,""+address,""+pass,
                    ""+conpass,""+company,""+costs,""+employees).enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();

                        Toast.makeText(CreateCompanyActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();

                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(CreateCompanyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JoinResponse> call, Throwable t) {
                    Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                    dialog.dismiss();
                    Toast.makeText(CreateCompanyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }


    }


    private void loadCountryList() {
        countryArr = null;
        if (checkInternetConnection.isInternetAvailable(this)) {
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getCountryList("").enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        int errCount = response.body().get("error").getAsInt();
                        if (errCount == 0) {
                            countryArr = response.body().get("report").getAsJsonArray();
                            countryList.add("Select Country");
                            if (countryArr.size() > 0) {
                                for (int i = 0; i < countryArr.size(); i++) {
                                    JsonObject countryObj = countryArr.get(i).getAsJsonObject();
                                    countryList.add(countryObj.get("Name").getAsString());
                                }
                                country.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, countryList));
                            }
                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(CreateCompanyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                    Toast.makeText(CreateCompanyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
           // Snackbar.make(getvie(), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(CreateCompanyActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();

        }
    }
    private void loadDivisionData(int cunPos) {
        divisionArr = null;
        division.setAdapter(null);
        if (cunPos > 0) {
            JsonObject divisionObj = countryArr.get(cunPos - 1).getAsJsonObject();
            String countryCode = divisionObj.get("Country_ID").getAsString();
            if (checkInternetConnection.isInternetAvailable(this)) {
                APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
                mApiService.getDivisionList("", countryCode).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            int errCount = response.body().get("error").getAsInt();
                            if (errCount == 0) {
                                divisionArr = response.body().get("report").getAsJsonArray();
                                if (divisionArr.size() > 0) {
                                    divisionList.clear();
                                    divisionList.add("Select Division");
                                    for (int i = 0; i < divisionArr.size(); i++) {
                                        JsonObject divisionObj = divisionArr.get(i).getAsJsonObject();
                                        divisionList.add(divisionObj.get("Name").getAsString());
                                    }
                                }
                                division.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
                            } else {
                                divisionList.clear();
                                divisionList.add("Select Division");
                                division.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
                            }
                        } else {
                            Log.e("DIVISION_LIST", "Error :" + response.code());
                            Toast.makeText(CreateCompanyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("DIVISION_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateCompanyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateCompanyActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            divisionList.clear();
            divisionList.add("Select Division");
            division.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
        }
    }
    private void loadDistrictData(int cunPos, int divPos) {
        districtArr = null;
        district.setAdapter(null);
        if (divPos > 0) {
            JsonObject countryObj = countryArr.get(cunPos - 1).getAsJsonObject();
            String selectedCunID = countryObj.get("Country_ID").getAsString();

            JsonObject divisionObj = divisionArr.get(divPos - 1).getAsJsonObject();
            String divisionID = divisionObj.get("Division_ID").getAsString();
            if (checkInternetConnection.isInternetAvailable(this)) {
                APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
                mApiService.getDistrictList("", divisionID, selectedCunID).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            int errCount = response.body().get("error").getAsInt();
                            if (errCount == 0) {
                                districtArr = response.body().get("report").getAsJsonArray();
                                if (districtArr.size() > 0) {
                                    districtList.clear();
                                    districtList.add("Select District");
                                    for (int i = 0; i < districtArr.size(); i++) {
                                        JsonObject districtObj = districtArr.get(i).getAsJsonObject();
                                        districtList.add(districtObj.get("Name").getAsString());
                                    }
                                }
                                district.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
                            } else {
                                districtList.clear();
                                districtList.add("Select District");
                                district.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
                            }
                        } else {
                            Log.e("DISTRICT_LIST", "Error :" + response.code());
                            Toast.makeText(CreateCompanyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("DISTRICT_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateCompanyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateCompanyActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            districtList.clear();
            districtList.add("Select District");
            district.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
        }
    }


    private void loadThanaData(int cunPos, int divPos, int disPos) {
        thanaArr = null;
        thana.setAdapter(null);
        if (disPos > 0) {
            JsonObject countryObj = countryArr.get(cunPos - 1).getAsJsonObject();
            String selectedCunID = countryObj.get("Country_ID").getAsString();

            JsonObject divisionObj = divisionArr.get(divPos - 1).getAsJsonObject();
            String divisionID = divisionObj.get("Division_ID").getAsString();

            JsonObject districtObj = districtArr.get(disPos - 1).getAsJsonObject();
            String districtID = districtObj.get("District_ID").getAsString();

            if (checkInternetConnection.isInternetAvailable(CreateCompanyActivity.this)) {
                APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
                mApiService.getThanaList("", districtID, divisionID, selectedCunID).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            int errCount = response.body().get("error").getAsInt();
                            if (errCount == 0) {
                                thanaArr = response.body().get("report").getAsJsonArray();
                                if (thanaArr.size() > 0) {
                                    thanaList.clear();
                                    thanaList.add("Select Thana");
                                    for (int i = 0; i < thanaArr.size(); i++) {
                                        JsonObject thanaObj = thanaArr.get(i).getAsJsonObject();
                                        thanaList.add(thanaObj.get("Name").getAsString());
                                    }
                                }
                                thana.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
                            } else {
                                thanaList.clear();
                                thanaList.add("Select Thana");
                                thana.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
                            }
                        } else {
                            Log.e("THANA_LIST", "Error :" + response.code());
                            Toast.makeText(CreateCompanyActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("THANA_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateCompanyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateCompanyActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            thanaList.clear();
            thanaList.add("Select Thana");
            thana.setAdapter(new ArrayAdapter<String>(CreateCompanyActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
        }
    }
}