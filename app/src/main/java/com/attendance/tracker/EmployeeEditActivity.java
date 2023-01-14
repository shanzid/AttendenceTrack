package com.attendance.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.activity.ProfileDetails.EmployeeDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.attendance.tracker.utils.ImageHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeEditActivity extends AppCompatActivity {
    private TextInputEditText company, mobile, email, address, nid, father_name, mother_name, ETno2;
    AppCompatImageView back, Img1card;
    AppCompatButton save;
    AppSessionManager appSessionManager;
    CheckInternetConnection checkInternetConnection;
    CircleImageView profileImg;
    private static final int SELECT_PICTURE = 3999;
    Bitmap bitmap1;
    RadioGroup radioGroup;
    String gender;
    RadioButton rMale,rFamale;
    String empUserId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);

        initVariables();
        initView();
        initFunc();
        initListener();
    }

    private void initVariables() {
        Intent mIntent = getIntent();
        empUserId = mIntent.getStringExtra("userID");
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
    }

    private void initView() {
        //EditText
        company = findViewById(R.id.CompanyNameET);
        ETno2 = findViewById(R.id.ETno2);
        mobile = findViewById(R.id.ETno1);
        email = findViewById(R.id.ETemail);
        address = findViewById(R.id.ETaddress);
        nid = findViewById(R.id.ETnid);
        father_name = findViewById(R.id.ETfather);
        mother_name = findViewById(R.id.ETmother_name);
       // gender = findViewById(R.id.ETgender);

        //radio button
        radioGroup = findViewById(R.id.radioGroup);
        rMale =findViewById(R.id.radioMale);
        rFamale = findViewById(R.id.radioFemale);
        //button
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);//imageview
        //image upload
        Img1card = findViewById(R.id.img1card);
        profileImg = findViewById(R.id.user_profile_image);

    }

    //image upload method
    void changeImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeEditActivity.this);
        final AlertDialog adChangeImage = builder.create();
        LayoutInflater layoutInflater = EmployeeEditActivity.this.getLayoutInflater();
        final View customView = layoutInflater.inflate(R.layout.dialog_layout_for_profile_image_source, null);
        LinearLayout galery = customView.findViewById(R.id.lnr_ChoseLoaction_Gallery);
        LinearLayout camera = customView.findViewById(R.id.lnr_ChoseLoaction_Camera);

        galery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getGalleryImage();
                adChangeImage.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getCameraImage();
                adChangeImage.dismiss();
            }
        });
        adChangeImage.setCancelable(true);
        adChangeImage.setView(customView);
        adChangeImage.show();
    }

    void getGalleryImage() {
        if (ContextCompat.checkSelfPermission(EmployeeEditActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmployeeEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    21);
            Log.e("pick image", "not permitted");
        } else {
            uploadImage();
        }
    }

    void getCameraImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 709);
    }

    void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 103) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 709);
            } else {
                Toast.makeText(EmployeeEditActivity.this, "Camera Permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 21) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadImage();

            } else {
                Toast.makeText(EmployeeEditActivity.this, "You have to grant permission to upload image..",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("activityResultcalled", "called");
        if (resultCode == RESULT_OK) {
            Log.e("resultCode", "RESULT_OK");
            if (requestCode == 709) {
                Log.e("requestCode", "709");
                if (data != null) {
                    Bundle extras = data.getExtras();
                    bitmap1 = (Bitmap) extras.get("data");
                    profileImg.setImageBitmap(bitmap1);
                    Img1card.setVisibility(View.VISIBLE);


                    // uploadProfileImage (bitmap);
                }
            } else if (requestCode == SELECT_PICTURE) {

                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    if(Build.VERSION.SDK_INT < 28) {
                        bitmap1 = bitmap;
                    } else {
//                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);
//                        bitmap1 = ImageDecoder.decodeBitmap(source);
                        bitmap1 = rotateBitmap(bitmap,90);
                    }
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileImg.setImageBitmap(bitmap1);
                Img1card.setVisibility(View.VISIBLE);

                //  uploadProfileImage(bmp);

            }
        }
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        int x = original.getWidth();
        int y = original.getHeight();
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original , 0, 0, original .getWidth(), original .getHeight(), matrix, true);
        return rotatedBitmap;
    }


    void saveImage() {
        ImageHelper imgHelper = new ImageHelper();
        String uri = imgHelper.saveToInternalStorage(bitmap1, EmployeeEditActivity.this, appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        //  AppSharedPreferences.saveStringToSharePreference(getActivity(),"user_image_uri",uri);
        //biddaloyApplication.usreImageUrl = uri;
        appSessionManager.storeProfileImageUrl(uri);//there might be problem same profileimageurl

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //image upload end
    private void initFunc() {
        getData(empUserId);

        // Add the Listener to the RadioGroup
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // The flow will come here when
                    // any of the radio buttons in the radioGroup
                    // has been clicked

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                        gender = String.valueOf(radioButton.getText());
                    }
                });
    }

    private void initListener() {
        back.setOnClickListener(view -> finish());
        save.setOnClickListener(view -> save());
        Img1card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();

            }
        });


    }

    private void save() {
        validateData();
    }


    private void validateData() {


        if (company.getText().toString().isEmpty()) {
            company.setError("Enter Company");
            company.requestFocus();
        } else if (mobile.getText().toString().isEmpty()) {
            mobile.setError("Enter Mobile");
            mobile.requestFocus();
        }
//
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
            String mobileTwo = ETno2.getText().toString().trim();
            String Address = address.getText().toString().trim();
            String Nid = nid.getText().toString().trim();
            String Father_Name = father_name.getText().toString().trim();
            String Mother_Name = mother_name.getText().toString().trim();
           // String Gender = gender.getText().toString().trim();


            callCompanyEditApi(Company, Email, Mobile, Address, Nid, Father_Name, Mother_Name, gender, bitmap1,mobileTwo);
        }


    }

    private void callCompanyEditApi(String company, String email, String mobile, String address,
                                    String nid, String father_name, String mother_name, String gender, Bitmap image1,String mobile2) {


        String userID = empUserId;
        String userName = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME);
        String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);
        byte[] byteArray = null;
        if (image1 != null ){
            Bitmap bmp= getResizedBitmap(image1, 200);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
             byteArray = stream.toByteArray();

        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String imageName = timestamp.getTime() + "";
        MultipartBody.Part body = null;

        try {

            File f = new File(EmployeeEditActivity.this.getCacheDir(), imageName);
            if (!f.exists()) {
                f.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(byteArray);
            fos.flush();
            fos.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), f);
            body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);
            Log.e("exception ", "not exception here");

        } catch (Exception e) {
            Log.e("exception ", "exception here");
            e.printStackTrace();
        }

        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), userName);
        RequestBody userpass = RequestBody.create(MediaType.parse("text/plain"), userPassword);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), userID);
        RequestBody userCompany = RequestBody.create(MediaType.parse("text/plain"), company);
        RequestBody userEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody userMobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody userNID = RequestBody.create(MediaType.parse("text/plain"), nid);
        RequestBody userAddress = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody userFathername = RequestBody.create(MediaType.parse("text/plain"), father_name);
        RequestBody usermother_name = RequestBody.create(MediaType.parse("text/plain"), mother_name);
        RequestBody userGender = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody mb2 = RequestBody.create(MediaType.parse("text/plain"), mobile2);


        //end

        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();

        APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
        mApiService.ProfileEdit(username,userpass,userid,userCompany,userEmail,userMobile,userNID,userAddress
                ,userFathername,usermother_name,userGender,body,mb2).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    int errCount = response.body().get("error").getAsInt();
                    if (errCount == 0) {
                        dialog.dismiss();
                        finish();
                    }
                } else {
                    Log.e("COUNTRY_LIST", "Error :" + response.code());
                    Toast.makeText(EmployeeEditActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                Toast.makeText(EmployeeEditActivity.this, "Successfuly done!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();

            }
        });
    }

    private void gotoProfile() {
        Intent mIntent = new Intent(EmployeeEditActivity.this, EmployeeDetailsActivity.class);
        mIntent.putExtra("userId",appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getData(String userID) {
        if (checkInternetConnection.isInternetAvailable(EmployeeEditActivity.this)) {
            // progressBar.setVisibility(View.VISIBLE);
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfiledetails(userID).enqueue(new Callback<ProfileDetailsData>() {
                @Override
                public void onResponse(Call<ProfileDetailsData> call, Response<ProfileDetailsData> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful())
                            //  progressBar.setVisibility(View.GONE);

                            dialog.dismiss();
                        ShowAllData(response.body());
                    }
                }


                @Override
                public void onFailure(Call<ProfileDetailsData> call, Throwable t) {
                    t.printStackTrace();
                    // progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void ShowAllData(ProfileDetailsData report) {

        Glide.with(getApplicationContext())
                .load(BuildConfig.BASE_URL + "" + report.getPhoto())
                .into(profileImg);

        company.setText(report.getName());
        mobile.setText(report.getMobile());
        email.setText(report.getEmail());
        address.setText(report.getAddress());
        nid.setText(report.getNid());
        father_name.setText(report.getFatherName());
        mother_name.setText(report.getMotherName());
      //  gender.setText(report.getGender());


        if (report.getGender().equals("Male")){
            rMale.setText(report.getGender());
            rMale.setChecked(true);
            rFamale.setChecked(false);
            gender = "Male";
        }else if (report.getGender().equals("Female")){
            rFamale.setText(report.getGender());
            rMale.setChecked(false);
            rFamale.setChecked(true);
            gender = "Female";
        }
    }


}
