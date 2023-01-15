package com.attendance.tracker.activity.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.ChangePassActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.ServerMaintainActivity;
import com.attendance.tracker.SupportActivity;
import com.attendance.tracker.UserBlockActivity;
import com.attendance.tracker.activity.DateWiseGeoActivity;
import com.attendance.tracker.activity.ProfileDetails.EmployeeDetailsActivity;
import com.attendance.tracker.activity.ProfileDetails.ProfileDetailsData;
import com.attendance.tracker.activity.ShowGeoReportActivity;
import com.attendance.tracker.activity.Task.TaskEmployeeActivity;
import com.attendance.tracker.activity.login.LoginActivity;
import com.attendance.tracker.data.ControllingModel;
import com.attendance.tracker.data.GeoFanceReportList;
import com.attendance.tracker.data.GeoSubmitResponse;
import com.attendance.tracker.databinding.ActivityMapTestUserBinding;
import com.attendance.tracker.interfaces.OnDataCompleteListener;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMainActivity extends AppCompatActivity {
    AppSessionManager appSessionManager;
    TextView userName, userEmail;
    private GoogleMap mMap;
    String getLink;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUserPosition;
    private DatabaseReference databaseReference;
    private GeoFire geoFire;
    public static List<LatLng> areaList;
    public static String userID = "";
    private OnDataCompleteListener onDataCompleteListener;
    String path = "AttendanceTracker";
    private DatabaseReference myArea;
    public Location lastLocation;
    String timeStamp;
    Geocoder geocoder;
    String userExitLat, userExitLng;
    private GeoQuery geoQuery;
    ArrayList<GeoFanceReportList> geoReportLists = new ArrayList<>();
    CheckInternetConnection internetConnection;
    List<LatLng> areList = new ArrayList<>();
    private Location mLAstKnownLocation;
    private final float DEFAULT_ZOOM = 18.0f;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    public static final int GPS_REQUEST_CODE = 9003;
    List<Address> addresses;
    MapTestUserActivity.LocationService mLocationService = new MapTestUserActivity.LocationService();
    Intent mServiceIntent;
    BroadcastReceiver br = new MapTestUserActivity.MyBroadcastReceiver();

    MapTestUserActivity.MyBroadcastReceiver myReceiver = null;
    Intent i;

    CircleImageView logout, imgview;
    String address;
    Date currentTime;
    Date oldTime;

    Date currentTimeExit;
    Date oldTimeExit;
    String userId;
    int serverStatus, userBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        initVariable();
        initView();
        iniFunction();
        initListener();
        checkMainPermission();
    }

    private void checkMainPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        buildLocationRequest();
                        buildLocationCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                        //      startService(new Intent(MapTestUserActivity.this, ForegroundService.class));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(getApplicationContext(), "You Must Enable Permission", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void buildLocationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mMap != null) {

                    lastLocation = locationResult.getLastLocation();

//                    // add user Marker
                    //  addUserMarker();
//                    // add user Circle
//                    addUserGeoCircle();

                }
            }
        };

    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
    }

    private void initVariable() {

        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
        userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        long tsLong = System.currentTimeMillis() / 1000;
        timeStamp = Long.toString(tsLong);
        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        geocoder = new Geocoder(this, Locale.getDefault());
        mServiceIntent = new Intent(this, mLocationService.getClass());

    }

    private void initView() {
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userMobile);
        imgview = findViewById(R.id.img);
    }

    private void iniFunction() {


        getServerData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));

    }

    private void initListener() {
        findViewById(R.id.cvGotoMap).setOnClickListener(view -> GotoMap());
        findViewById(R.id.cv_manualAttID).setOnClickListener(view -> GotoManual());
        findViewById(R.id.cvGoToTask).setOnClickListener(view -> gotoTask());
        findViewById(R.id.cvEmpAttendance).setOnClickListener(view -> gotoAttendance());
        findViewById(R.id.cvDateWise).setOnClickListener(view -> gotoDateWiseAttendance());
        findViewById(R.id.profile).setOnClickListener(view -> EmployeeDetails());
        findViewById(R.id.logout).setOnClickListener(view -> logout());
        // findViewById(R.id.edit).setOnClickListener(view -> EditProfile());
        findViewById(R.id.cv_chnagepass).setOnClickListener(view -> chnagepass());
        findViewById(R.id.cv_support).setOnClickListener(view -> support());
        findViewById(R.id.cvShareApp).setOnClickListener(view -> shareApp());

    }

    private void shareApp() {
        getLink = "https://artificial-soft.com/"+userId;

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://artisoft.page.link/")
                .setLink(Uri.parse(getLink))
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.attendance.tracker")
                                //.setMinimumVersion(125)
                                .build())

                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Attendance Tracking");
                                assert shortLink != null;
                                getLink= shortLink.toString();
                                shareIntent.putExtra(Intent.EXTRA_TEXT, getLink);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch(Exception e) {
                                //e.toString();
                            }
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

    private void support() {
        Intent mIntent = new Intent(getApplicationContext(), SupportActivity.class);
        startActivity(mIntent);
    }

    private void chnagepass() {
        Intent mIntent = new Intent(getApplicationContext(), ChangePassActivity.class);
        mIntent.putExtra("userName", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERNAME));
        mIntent.putExtra("userPass", appSessionManager.getUserDetails().get(AppSessionManager.KEY_PASSWORD));
        startActivity(mIntent);
    }

    private void EmployeeDetails() {
        Intent mIntent = new Intent(getApplicationContext(), EmployeeDetailsActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);
    }

//    private void EditProfile() {
//        Intent mIntent = new Intent(this, EmployeeEditActivity.class);
//        mIntent.putExtra("userType","0");
//        mIntent.putExtra("userName","Employee Edit");
//        startActivity(mIntent);

    private void GotoManual() {
//        Intent intent=new Intent(getApplicationContext(), UnderDevelopmentActivity.class);
//        startActivity(intent);
        // getDeviceLocation();

        popupManualAttan();
    }

    public void getData(String userID) {
        if (internetConnection.isInternetAvailable(UserMainActivity.this)) {
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


                        userName.setText(response.body().getName());
                        userEmail.setText(response.body().getMobile());
                        Glide.with(getApplicationContext())
                                .load(BuildConfig.BASE_URL + "" + response.body().getPhoto())
                                .into(imgview);

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

    private void getDeviceLocation(String userReson) {

//        userExitLat = String.valueOf(location.getLatitude());
//        userExitLng = String.valueOf(location.getLongitude())
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLAstKnownLocation = task.getResult();
                            if (mLAstKnownLocation != null) {


                                userExitLat = String.valueOf(mLAstKnownLocation.getLatitude());
                                userExitLng = String.valueOf(mLAstKnownLocation.getLongitude());

                                getLocationAddress(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude(), userReson);

                                // Only if available else return NULL


                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLAstKnownLocation = locationResult.getLastLocation();
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        userExitLat = String.valueOf(mLAstKnownLocation.getLatitude());
                                        userExitLng = String.valueOf(mLAstKnownLocation.getLongitude());
                                        getLocationAddress(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude(), userReson);

                                    }
                                };
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, null);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get last location ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getLocationAddress(double latitude, double longitude, String userReson) {
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();


        if (userExitLat.equals("") && userExitLng.equals("")) {
            Toast.makeText(this, "LatLong Fail", Toast.LENGTH_SHORT).show();
        } else if (address.equals("")) {
            Toast.makeText(this, "Address not Found", Toast.LENGTH_SHORT).show();

        } else {

            //  submitManualLocation(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID),userExitLng+","+userExitLat,address,"");


            submitManualLocation(appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID), userExitLng + "," + userExitLat, address, userReson);

        }

    }

    private void popupManualAttan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomBottomSheetDialogTheme);
        final AlertDialog popupConfirmation = builder.create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.manual_work_dialog, null);
        EditText reason = customView.findViewById(R.id.reason);
        TextView tvCancel = customView.findViewById(R.id.testCancel);
        TextView btnContinue = customView.findViewById(R.id.submit);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmation.dismiss();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason.getText().toString().isEmpty()) {
                    Toast.makeText(UserMainActivity.this, "Enter Your Reason", Toast.LENGTH_SHORT).show();
                } else {
                    String userReason = reason.getText().toString();
                    getDeviceLocation(userReason);
                }


                popupConfirmation.dismiss();
            }
        });

        popupConfirmation.setCancelable(false);
        popupConfirmation.setView(customView);
        popupConfirmation.show();
    }

    private void gotoTask() {
        Intent intent = new Intent(getApplicationContext(), TaskEmployeeActivity.class);
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        intent.putExtra("EmployeeID", userId);
        startActivity(intent);


    }

    private void GotoMap() {
        startActivity(new Intent(this, MapTestUserActivity.class));
    }

    private void gotoAttendance() {
        Intent mIntent = new Intent(UserMainActivity.this, ShowGeoReportActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        startActivity(mIntent);

    }

    private void gotoDateWiseAttendance() {
        Intent mIntent = new Intent(UserMainActivity.this, DateWiseGeoActivity.class);
        mIntent.putExtra("userId", appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID));
        mIntent.putExtra("type", appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY));
        startActivity(mIntent);

    }

    private void gotoSupport() {
        Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
        startActivity(intent);
    }

    private void logout() {
        //   Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Logout App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appSessionManager.logoutUser();
                        stopService(mServiceIntent);
                        startActivity(new Intent(UserMainActivity.this, LoginActivity.class));
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
//    private void submitManualLocation(String userID,String geo,String address,String hint){
//        if (internetConnection.isInternetAvailable(UserMainActivity.this)) {
//            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
//                    .content(getResources().getString(R.string.pleaseWait))
//                    .progress(true, 0)
//                    .cancelable(false)
//                    .show();
//
//            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
//
//            mApiService.submitManual(userID,geo,address,hint).enqueue(new Callback<GeoSubmitResponse>() {
//
//            mApiService.submitManual(userID,geo,address,"").enqueue(new Callback<GeoSubmitResponse>() {
//                @Override
//                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getError() == 0) {
//                            Toast.makeText(UserMainActivity.this, ""+response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            Log.w("test",""+response.body().getErrorReport());
//                            // sendFBaseTokenToServer();
//                        } else if (response.body().getError() == 1) {
//                            dialog.dismiss();
//                            Toast.makeText(UserMainActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeoSubmitResponse> call, Throwable t) {
//                    dialog.dismiss();
//                    Log.d("LOGIN", "onFailure: " + t.getMessage());
//                }
//            });
//        } else {
//            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
//        }
//    }
//
//
//
//}

    private void submitManualLocation(String userID, String geo, String address, String hint) {
        if (internetConnection.isInternetAvailable(UserMainActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.submitManual(userID, geo, address, hint).enqueue(new Callback<GeoSubmitResponse>() {
                @Override
                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            Toast.makeText(UserMainActivity.this, "" + response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.w("test", "" + response.body().getErrorReport());
                            // sendFBaseTokenToServer();
                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                            Toast.makeText(UserMainActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeoSubmitResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getServerData(String userId) {
        if (internetConnection.isInternetAvailable(UserMainActivity.this)) {

            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getServerStatus(userId).enqueue(new Callback<ControllingModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ControllingModel> call, Response<ControllingModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            serverStatus = response.body().getServerStatus();
                            userBlock = response.body().getUserBlock();
                            if (serverStatus != 0) {
                                Intent mIntent = new Intent(UserMainActivity.this, ServerMaintainActivity.class);
                                startActivity(mIntent);
                                finish();
                            } else {
                                if (userBlock != 0) {
                                    Intent mIntent = new Intent(UserMainActivity.this, UserBlockActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                }

                            }
                        } else {
                            Toast.makeText(UserMainActivity.this, "some thing went to wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ControllingModel> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

}

