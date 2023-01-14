package com.attendance.tracker.activity.Task;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.data.ProfileData;
import com.attendance.tracker.data.ProfileList;
import com.attendance.tracker.data.TaskinputResponse;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskinputFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    View view;
    AppCompatButton submit;
    TextInputEditText noteinput;
    AppSessionManager appSessionManager;
    CheckInternetConnection checkInternetConnection;
    String userID, employeeID, userType;
    JsonArray EmployeeArr;
    Spinner spinner;
    ArrayList<String> employeeList = new ArrayList<>();
    ArrayList<String> empID = new ArrayList<>();
    String empigleID = "";
    private List<ProfileList> employeeIdList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_taskinput, container, false);
        spinner = view.findViewById(R.id.sp_employeeList);
        spinner.setOnItemSelectedListener(this);
        initvar();
        initView();
        initFunc();
        initClick();
        return view;
    }

    private void initClick() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateData();

            }
        });
    }

    private void validateData() {

        if (noteinput.getText().toString().isEmpty()) {
            noteinput.setError("Enter Note");
            noteinput.requestFocus();
        } else if(empigleID.isEmpty()||empigleID.equals("00")) {
            Toast.makeText(getContext(), "Select Employee", Toast.LENGTH_SHORT).show();
        }else {
            String userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
            String input_note = noteinput.getText().toString().trim();



            calltask(userID,empigleID,input_note);
        }


    }

    private void calltask(String userID,String EmployeeID,String input_note) {

        final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();

        APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
        mApiService.getTaskinput(userID, EmployeeID, input_note).enqueue(new Callback<TaskinputResponse>() {
            @Override
            public void onResponse(Call<TaskinputResponse> call, Response<TaskinputResponse> response) {

                if (response.isSuccessful()) {
                    dialog.dismiss();

                    spinner.setSelection(0);
                    noteinput.setText("");
                    Toast.makeText(getContext(), response.body().getErrorReport(), Toast.LENGTH_SHORT).show();

                   // goToPage();

                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<TaskinputResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();


            }
        });
    }

    private void goToPage() {
        TasklistFragment nextFrag= new TasklistFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHolder, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    private void initFunc() {
        Intent mIntent = getActivity().getIntent();
        //employeeID = mIntent.getStringExtra("employeeID");
        LoadEmployeeList();
         //owEmployee();



    }


    private void LoadEmployeeList() {
        String userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        EmployeeArr = null;
        employeeList.clear();
        empID.clear();
        employeeIdList.clear();
        if (checkInternetConnection.isInternetAvailable(getContext())) {
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfileList(userID, "0","").enqueue(new Callback<ProfileData>() {
                @Override
                public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {

                    if (response.isSuccessful()) {
                        ProfileData profileData = response.body();
                        employeeIdList = profileData.getReport();
                        employeeList.add("Please Select");
                        empID.add("00");
                        for (int i = 0; i < employeeIdList.size(); i++) {
                            employeeList.add(employeeIdList.get(i).getName());
                            empID.add(employeeIdList.get(i).getId());


                        }

                        ArrayAdapter ad = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, employeeList);
                        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(ad);

                    }

                }

                @Override
                public void onFailure(Call<ProfileData> call, Throwable t) {

                }
            });
//            mApiService.getProfileList(user_id,userType).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    if (response.isSuccessful()) {
//                        int errCount = Integer.parseInt(response.body().getErrorReport());
//                        if (errCount == 0) {
//                            EmployeeArr = response.body().getErrorReport();
//                            employeeList.add("Select Employee");
//                            employeeIdList.add("00");
//                            if (EmployeeArr.size() > 0) {
//                                for (int i = 0; i < EmployeeArr.size(); i++) {
//                                    JsonObject Obj = EmployeeArr.get(i).getAsJsonObject();
//                                    employeeIdList.add(Obj.get("ID").getAsString());
//                                }
//                                employeetypelist.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, employeeList));
//                            }
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            });
        } else {
            Toast.makeText(getContext(), "Internet connection problem!", Toast.LENGTH_SHORT).show();

        }
    }

    private void initView() {
        submit = view.findViewById(R.id.submitBT);
        noteinput = view.findViewById(R.id.noteET);


    }

    private void initvar() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(getContext());


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        empigleID=empID.get(i)  ;


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
