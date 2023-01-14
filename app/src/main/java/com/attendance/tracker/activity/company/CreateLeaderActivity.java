package com.attendance.tracker.activity.company;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

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

public class CreateLeaderActivity extends AppCompatActivity {
    AppSessionManager appSessionManager;
    CheckInternetConnection checkInternetConnection;
    Spinner sp_type, sp_leader_list;
    TextInputEditText name, number, email, confirm_password, password, mother, father, nid;
    JsonArray LeaderArr;
    private List<String> leaderList = new ArrayList<>();
    private List<String> leaderIdList = new ArrayList<>();

    String typeId;
    String leaderId = "0";

    ArrayList<String> typeList = new ArrayList<>();
    LinearLayout lytLeader;
    AppCompatImageView back;
    AppCompatButton save;

    Spinner division, district, thana, country;
    JsonArray countryArr;
    private List<String> countryList = new ArrayList<>();

    JsonArray divisionArr;
    private List<String> divisionList = new ArrayList<>();

    JsonArray districtArr;
    private List<String> districtList = new ArrayList<>();

    JsonArray thanaArr;
    private List<String> thanaList = new ArrayList<>();

    String countryID;
    String divID = "";
    String disID = "";
    String thanaID = "";
    ImageView passwordView,getPasswordView;
    boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_join);
        initVariable();
        initView();
        iniFunction();
        initListener();
    }

    private void initVariable() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }

    private void initView() {


        sp_leader_list = findViewById(R.id.sp_leader_list);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        nid = findViewById(R.id.nid);
        father = findViewById(R.id.father);
        mother = findViewById(R.id.mother);
        password = findViewById(R.id.ETpass);
        confirm_password = findViewById(R.id.ETconfirmpass);
        lytLeader = findViewById(R.id.lytLeader);

        //button
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);//imageview
        passwordView = findViewById(R.id.showPassword);
        getPasswordView = findViewById(R.id.showPassword2);

        //spinner
        division = findViewById(R.id.sp_division);
        sp_type = findViewById(R.id.sp_type);
        country = findViewById(R.id.sp_country);
        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);

    }
    private void showCPassword() {
        if (isPasswordVisible) {
            String cpassword = confirm_password.getText().toString();
            confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirm_password.setText(cpassword);
            confirm_password.setSelection(confirm_password.length());
            getPasswordView.setImageResource(R.drawable.eye);

        } else {
            String cpassword = confirm_password.getText().toString();
            confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirm_password.setInputType(InputType.TYPE_CLASS_TEXT);
            confirm_password.setText(cpassword);
            confirm_password.setSelection(confirm_password.length());
            getPasswordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void showPassword() {
        if (isPasswordVisible) {
            String pass = password.getText().toString();
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setText(pass);
            password.setSelection(pass.length());
            passwordView.setImageResource(R.drawable.eye);

        } else {
            String pass = password.getText().toString();
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            password.setText(pass);
            password.setSelection(password.length());
            passwordView.setImageResource(R.drawable.hidden);

        }
        isPasswordVisible= !isPasswordVisible;

    }

    private void iniFunction() {
        loadType();
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = typeList.get(i);
                if (selected.equals("Employee")) {
                    lytLeader.setVisibility(View.VISIBLE);
                    sp_leader_list.setVisibility(View.VISIBLE);
                    loadLeaderList();
                    showLeader();
                } else if (selected.equals("Leader")) {
                    lytLeader.setVisibility(View.GONE);
                    sp_leader_list.setVisibility(View.GONE);
                    typeId = "1";
                } else {
                    lytLeader.setVisibility(View.GONE);
                    sp_leader_list.setVisibility(View.GONE);
                    typeId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        loadCountryList();

    }

    private void showLeader() {
        typeId = "0";  //check this

        sp_leader_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leaderId = leaderIdList.get(i);
//                            Log.d("leaderID",leaderId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initListener() {

        passwordView.setOnClickListener(view -> showPassword());
        getPasswordView.setOnClickListener(view -> showCPassword());
        back.setOnClickListener(view -> finish());
        save.setOnClickListener(view -> createdLeader());

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

    private void loadLeaderList() {
        LeaderArr = null;
        leaderList.clear();
        leaderIdList.clear();
        if (checkInternetConnection.isInternetAvailable(this)) {
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getLeader().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        int errCount = response.body().get("error").getAsInt();
                        if (errCount == 0) {
                            LeaderArr = response.body().get("report").getAsJsonArray();
                            leaderList.add("Select Leader");
                            leaderIdList.add("00");
                            if (LeaderArr.size() > 0) {
                                for (int i = 0; i < LeaderArr.size(); i++) {
                                    JsonObject countryObj = LeaderArr.get(i).getAsJsonObject();
                                    leaderList.add(countryObj.get("Name").getAsString());
                                    leaderIdList.add(countryObj.get("ID").getAsString());
                                }
                                sp_leader_list.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, leaderList));
                            }
                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(CreateLeaderActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                    Toast.makeText(CreateLeaderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Snackbar.make(getvie(), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(CreateLeaderActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();

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
                                country.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, countryList));
                            }
                        }
                    } else {
                        Log.e("COUNTRY_LIST", "Error :" + response.code());
                        Toast.makeText(CreateLeaderActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                    Toast.makeText(CreateLeaderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Snackbar.make(getvie(), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(CreateLeaderActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();

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
                                division.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
                            } else {
                                divisionList.clear();
                                divisionList.add("Select Division");
                                division.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
                            }
                        } else {
                            Log.e("DIVISION_LIST", "Error :" + response.code());
                            Toast.makeText(CreateLeaderActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("DIVISION_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateLeaderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateLeaderActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            divisionList.clear();
            divisionList.add("Select Division");
            division.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionList));
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
                                district.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
                            } else {
                                districtList.clear();
                                districtList.add("Select District");
                                district.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
                            }
                        } else {
                            Log.e("DISTRICT_LIST", "Error :" + response.code());
                            Toast.makeText(CreateLeaderActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("DISTRICT_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateLeaderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateLeaderActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            districtList.clear();
            districtList.add("Select District");
            district.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, districtList));
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

            if (checkInternetConnection.isInternetAvailable(CreateLeaderActivity.this)) {
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
                                thana.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
                            } else {
                                thanaList.clear();
                                thanaList.add("Select Thana");
                                thana.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
                            }
                        } else {
                            Log.e("THANA_LIST", "Error :" + response.code());
                            Toast.makeText(CreateLeaderActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("THANA_LIST", "onFailure: " + t.getMessage());
                        Toast.makeText(CreateLeaderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateLeaderActivity.this, "Internet connection problem!", Toast.LENGTH_SHORT).show();
            }
        } else {
            thanaList.clear();
            thanaList.add("Select Thana");
            thana.setAdapter(new ArrayAdapter<String>(CreateLeaderActivity.this, android.R.layout.simple_spinner_dropdown_item, thanaList));
        }
    }

    private void loadType() {
        typeList = new ArrayList<>();
        typeList.add("Select type");
        typeList.add("Employee");
        typeList.add("Leader");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeList);
        sp_type.setAdapter(arrayAdapter);
    }


    public void createdLeader() {
        JsonObject countryObj = countryArr.get(country.getSelectedItemPosition() - 1).getAsJsonObject();
        countryID = countryObj.get("Country_ID").getAsString();
        divID = divisionArr.get(division.getSelectedItemPosition() - 1).getAsJsonObject().get("Division_ID").getAsString();
        disID = districtArr.get(district.getSelectedItemPosition() - 1).getAsJsonObject().get("District_ID").getAsString();
        thanaID = thanaArr.get(thana.getSelectedItemPosition() - 1).getAsJsonObject().get("Thana_ID").getAsString();
        if (name.getText().toString().isEmpty()) {
            name.setError("Enter User Name");
            name.requestFocus();
        } else if (number.getText().toString().isEmpty()) {
            number.setError("Enter User number");
            number.requestFocus();
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Enter User email");
            email.requestFocus();
        } else if (nid.getText().toString().isEmpty()) {
            nid.setError("Enter User nid");
            nid.requestFocus();
        } else if (father.getText().toString().isEmpty()) {
            father.setError("Enter Father Name");
            father.requestFocus();
        } else if (mother.getText().toString().isEmpty()) {
            mother.setError("Enter Mother Name");
            mother.requestFocus();
        } else if (password.getText().toString().isEmpty()) {
            password.setError("Enter User password");
            password.requestFocus();
        } else if (confirm_password.getText().toString().isEmpty()) {
            confirm_password.setError("Enter User confirm Password");
            confirm_password.requestFocus();
        } else if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            Toast.makeText(this, "your confirm password don't match", Toast.LENGTH_SHORT).show();
        } else if (typeId.isEmpty()) {
            Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
        } else if (countryID.equals("")) {
            Toast.makeText(this, "Select Country", Toast.LENGTH_SHORT).show();
        } else if (divID.equals("")) {
            Toast.makeText(this, "Select Division", Toast.LENGTH_SHORT).show();
        } else if (disID.equals("")) {
            Toast.makeText(this, "Select District", Toast.LENGTH_SHORT).show();
        } else if (thanaID.equals("")) {
            Toast.makeText(this, "Select Thana", Toast.LENGTH_SHORT).show();

        } else {
            String Name = name.getText().toString().trim();
            String Number = number.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String NID = nid.getText().toString().trim();
            String FName = father.getText().toString().trim();
            String MName = mother.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String CPass = confirm_password.getText().toString().trim();


            String userName = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME);
            String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);

            callJoinApi(userName, userPassword, Name, Number, Email, NID, FName, MName, countryID, divID, disID, thana, typeId, leaderId, pass, CPass);

        }


    }

    private void callJoinApi(String userName, String userPassword, String name, String number, String email, String nid,
                             String fName, String mName, String countryID,
                             String divID, String disID,
                             Spinner thana, String typeId,
                             String leaderId, String pass, String cPass) {

        if (checkInternetConnection.isInternetAvailable(CreateLeaderActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.goJoin("" + userName, "" + userPassword, "" + number, "" + typeId, "" + leaderId, "" + name, "" + email, "" + nid, "" + fName,
                    "" + mName, "" + countryID, "" + divID, "" + disID, "" + thana, "" + pass, "" + cPass).enqueue(new Callback<JoinResponse>() {
                @Override
                public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            dialog.dismiss();
                            Toast.makeText(CreateLeaderActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            finish();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(CreateLeaderActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JoinResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }

    }


}