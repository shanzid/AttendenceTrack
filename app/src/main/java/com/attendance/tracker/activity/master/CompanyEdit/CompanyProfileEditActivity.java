package com.attendance.tracker.activity.master.CompanyEdit;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.R;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.activity.master.MasterActivity;
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

public class CompanyProfileEditActivity extends AppCompatActivity {
    private TextInputEditText company, mobile, email, address, nid, father_name, mother_name,ETno2;
    AppCompatImageView back, Img1card;
    AppCompatButton save;
    AppSessionManager appSessionManager;
    CheckInternetConnection checkInternetConnection;
    CircleImageView profileImg;
    private static final int SELECT_PICTURE = 3999;
  //  private static final int SELECT_PICTURE = 1;
    Bitmap bitmap1;
    private String uri;
    String userID;
    RadioGroup radioGroup;
    String gender;
    RadioButton rMale,rFamale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_edit);

        initVariables();
        initView();
        initFunc();
        initListener();
    }

    private void initVariables() {
        checkInternetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        Intent mIntent = getIntent();
        userID = mIntent.getStringExtra("UserID");
       // Toast.makeText(this, ""+userID, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        radioGroup = findViewById(R.id.radioGroup);
        rMale =findViewById(R.id.radioMale);
        rFamale = findViewById(R.id.radioFemale);

        company = findViewById(R.id.CompanyNameET);
        mobile = findViewById(R.id.ETno1);
        ETno2 = findViewById(R.id.ETno2);
        email = findViewById(R.id.ETemail);
        address = findViewById(R.id.ETaddress);
        nid = findViewById(R.id.ETnid);
        father_name = findViewById(R.id.ETfather);
        mother_name = findViewById(R.id.ETmother_name);
        //gender = findViewById(R.id.ETgender);
        //spinner

        //button
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);//imageview
        //image upload
        Img1card = findViewById(R.id.img1card);
        profileImg = findViewById(R.id.user_profile_image);


        //radioGroup.clearCheck();

    }

    //image upload method
    void changeImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CompanyProfileEditActivity.this);
        final AlertDialog adChangeImage = builder.create();
        LayoutInflater layoutInflater = CompanyProfileEditActivity.this.getLayoutInflater();
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
        if (ContextCompat.checkSelfPermission(CompanyProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CompanyProfileEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
//        Log.e("permission accepted", "permission accepted");
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, SELECT_PICTURE);
//        Intent iGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        iGallery.setType("image/*");
//        startActivityForResult(iGallery, SELECT_PICTURE);

        Intent intent = new Intent();
        intent.setType("image/*");
       // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
                Toast.makeText(CompanyProfileEditActivity.this, "Camera Permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 21) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadImage();

            } else {
                Toast.makeText(CompanyProfileEditActivity.this, "You have to grant permission to upload image..",
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

               // bitmap1 = BitmapFactory.decodeFile(imageAbsolutePath);
                profileImg.setImageURI(selectedImage);
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

        getData(userID);

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

    public void getData(String userID) {
        if (checkInternetConnection.isInternetAvailable(CompanyProfileEditActivity.this)) {
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
                    dialog.dismiss();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void ShowAllData(ProfileDetailsData report) {

        Glide.with(getApplicationContext())
                .load(BuildConfig.BASE_URL+ "" + report.getPhoto())
                .placeholder(R.drawable.user)
                .error(R.drawable.prof)
                .into(profileImg);

        company.setText(report.getName());
        mobile.setText(report.getMobile());
        ETno2.setText(report.getMobile2());
        email.setText(report.getEmail());
        address.setText(report.getAddress());
        nid.setText(report.getNid());
        father_name.setText(report.getFatherName());
        mother_name.setText(report.getMotherName());
       // gender.setText(report.getGender());

        if (report.getGender().equals("Male")){
            rMale.setText("Male");
            rMale.setChecked(true);
            rFamale.setChecked(false);
            gender = "Male";
        }else if (report.getGender().equals("Female")){
            rFamale.setText("Female");
            rMale.setChecked(false);
            rFamale.setChecked(true);
            gender = "Female";
        }

    }

    private void validateData() {


        if (company.getText().toString().isEmpty()) {
            company.setError("Enter Company");
            company.requestFocus();
        } else if (mobile.getText().toString().isEmpty()) {
            mobile.setError("Enter Mobile");
            mobile.requestFocus();
        } else if (ETno2.getText().toString().isEmpty()) {
            ETno2.setError("Enter Mobile");
            ETno2.requestFocus();
        } else if (address.getText().toString().isEmpty()) {
            address.setError("Enter Address");
            address.requestFocus();


        } else if (email.getText().toString().isEmpty()) {
            email.setError("Enter Email");
            email.requestFocus();


        } else {
            String Email = email.getText().toString().trim();
            String Company = company.getText().toString().trim(); // company name is name
            String Mobile = mobile.getText().toString().trim();
            String Mobile2 = ETno2.getText().toString().trim();
            String Address = address.getText().toString().trim();
            String Nid = nid.getText().toString().trim();
            String Father_Name = father_name.getText().toString().trim();
            String Mother_Name = mother_name.getText().toString().trim();
          //  String Gender = gender.getText().toString().trim();

            if (bitmap1 != null) {
                callCompanyEditApi(Company, Email, Mobile, Address, Nid, Father_Name, Mother_Name, gender, bitmap1,Mobile2);

            } else {
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void callCompanyEditApi(String company, String email, String mobile, String address,
                                    String nid, String father_name, String mother_name, String gender, Bitmap image1,String ETno2) {


        String userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        String userName = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME);
        String userPassword = appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD);

        Bitmap bmp = getResizedBitmap(image1, 200);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String imageName = timestamp.getTime() + "";
        MultipartBody.Part body = null;

        try {

            File f = new File(CompanyProfileEditActivity.this.getCacheDir(), imageName);
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
        RequestBody userMobile2 = RequestBody.create(MediaType.parse("text/plain"), ETno2);


        //end

        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                .content(getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false)
                .show();

        APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
        mApiService.ProfileEdit(username, userpass, userid, userCompany, userEmail, userMobile, userNID, userAddress
                , userFathername, usermother_name, userGender, body,userMobile2).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    GotoMain();
                    int errCount = response.body().get("error").getAsInt();
                    if (errCount == 0) {
                        dialog.dismiss();
                        finish();
                    }
                } else {
                    Log.e("COUNTRY_LIST", "Error :" + response.code());
                    Toast.makeText(CompanyProfileEditActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("COUNTRY_LIST", "onFailure: " + t.getMessage());
                finish();
                Toast.makeText(CompanyProfileEditActivity.this, "Successfuly done!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
    }

    private void GotoMain() {
        startActivity(new Intent(CompanyProfileEditActivity.this, MasterActivity.class));
    }


}
