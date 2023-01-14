//package com.artisoft.geofinder;
//
//import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
//import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.ActivityManager;
//import android.app.AlarmManager;
//import android.app.KeyguardManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.IntentSender;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.PowerManager;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.artisoft.geofinder.R;
//import com.artisoft.geofinder.activity.MapsActivity;
//import com.artisoft.geofinder.activity.user.MyLatLong;
//import com.artisoft.geofinder.data.GeoFanceReport;
//import com.artisoft.geofinder.data.GeoFanceReportList;
//import com.artisoft.geofinder.data.GeoSubmitResponse;
//import com.artisoft.geofinder.databinding.ActivityMapTestUserBinding;
//import com.artisoft.geofinder.interfaces.OnDataCompleteListener;
//import com.artisoft.geofinder.network.APIService;
//import com.artisoft.geofinder.network.ApiUtil.ApiUtils;
//import com.artisoft.geofinder.network.ConstantValue;
//import com.artisoft.geofinder.utils.AppSessionManager;
//import com.artisoft.geofinder.utils.CheckInternetConnection;
//import com.artisoft.geofinder.utils.Util;
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryEventListener;
//import com.google.android.gms.common.api.ResolvableApiException;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResponse;
//import com.google.android.gms.location.SettingsClient;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//
//import java.sql.Time;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MapTestUserActivityold extends FragmentActivity implements OnMapReadyCallback, GeoQueryEventListener, OnDataCompleteListener {
//    private GoogleMap mMap;
//    private ActivityMapTestUserBinding binding;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private Marker currentUserPosition;
//    private DatabaseReference databaseReference;
//    private GeoFire geoFire;
//    public static List<LatLng> areaList ;
//    AppSessionManager appSessionManager;
//    public static  String userID = "";
//    private OnDataCompleteListener onDataCompleteListener;
//    String path = "AttendanceTracker";
//    private  DatabaseReference myArea;
//    public Location lastLocation;
//    String timeStamp;
//    String userExitLat, userExitLng;
//    private GeoQuery geoQuery ;
//    ArrayList<GeoFanceReportList> geoReportLists = new ArrayList<>();
//    CheckInternetConnection internetConnection;
//    List<LatLng> areList = new ArrayList<>();
//    private Location mLAstKnownLocation;
//    private final float DEFAULT_ZOOM = 18.0f;
//    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
//    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
//    public static final int GPS_REQUEST_CODE = 9003;
//
//    LocationService mLocationService = new LocationService();
//    Intent mServiceIntent;
//    BroadcastReceiver br = new MyBroadcastReceiver();
//
//    MyBroadcastReceiver myReceiver=null;
//    Intent i;
//
//    Date currentTime;
//    Date oldTime;
//
//    Date currentTimeExit;
//    Date oldTimeExit;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initVariables();
//        checkMainPermission();
//
//    }
//
//    private void checkMainPermission() {
//        Dexter.withContext(this)
//                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        buildLocationRequest();
//                        buildLocationCallback();
//                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapTestUserActivity.this);
//
//                        settingGeoFire();
//                        getGeoLocation(userID);
//                        //      startService(new Intent(MapTestUserActivity.this, ForegroundService.class));
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                        Toast.makeText(MapTestUserActivityold.this, "You Must Enable Permission", Toast.LENGTH_SHORT).show();
//                    }
//                }).check();
//    }
//
//    private void initVariables() {
//
//        binding = ActivityMapTestUserBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        appSessionManager = new AppSessionManager(this);
//        internetConnection = new CheckInternetConnection();
//        onDataCompleteListener = this;
//        userID = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
//        long tsLong = System.currentTimeMillis() / 1000;
//        timeStamp = Long.toString(tsLong);
//        binding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    private void settingGeoFire() {
//        databaseReference = FirebaseDatabase.getInstance().getReference("myLocation");
//        geoFire = new GeoFire(databaseReference);
//    }
//
//    private void initArea() {
//        if (areList != null){
//            FirebaseDatabase.getInstance()
//                    .getReference(path)
//                    .child(userID)
//                    .setValue(areList).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Log.w("employer Enter","Done");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            Toast.makeText(MapTestUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//        }
//
//        myArea =  FirebaseDatabase.getInstance()
//                .getReference(path)
//                .child(userID);
//
//        myArea.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // update area list
//                List<MyLatLong> latLngList = new ArrayList<>();
//
//                for (DataSnapshot locationSnapShot : snapshot.getChildren()){
//                    MyLatLong latLng = locationSnapShot.getValue(MyLatLong.class);
//                    latLngList.add(latLng);
//                }
//                onDataCompleteListener.LoadLocationSuccess(latLngList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void addUserMarker() {
//
//        geoFire.setLocation(userID, new GeoLocation(lastLocation.getLatitude(),
//                lastLocation.getLongitude()), new GeoFire.CompletionListener() {
//
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//
//
//                if (currentUserPosition != null) currentUserPosition.remove();
//                currentUserPosition = mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),
//                        lastLocation.getLongitude())).title("You"));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserPosition.getPosition(), 15.0f));
//
//            }
//        });
//    }
//
//    private void buildLocationCallback(){
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (mMap != null) {
//
//                    lastLocation = locationResult.getLastLocation();
//
////                    // add user Marker
//                    addUserMarker();
////                    // add user Circle
////                    addUserGeoCircle();
//
//                }
//            }
//        };
//
//    }
//
//    private void addUserGeoCircle() {
//        if(geoQuery != null){
//            geoQuery.removeGeoQueryEventListener(this);
//
//            geoQuery.removeAllListeners();
//        }
//
//        for (LatLng area : areaList){
//            mMap.addCircle(new CircleOptions()
//                    .center(area)
//                    .radius(200)
//                    .strokeColor(Color.BLUE)
//                    .fillColor(0x220000FF)
//                    .strokeWidth(5));
//
//            // create geo
//            geoQuery = geoFire.queryAtLocation(new GeoLocation(area.latitude,area.longitude), 0.2f);
//            geoQuery.addGeoQueryEventListener(MapTestUserActivity.this);
//
//        }
//
//        starServiceFunc();
//
//    }
//
//    private void buildLocationRequest(){
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(3000);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        if (fusedLocationProviderClient != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        }
//        enableUserLocation();
//        openGps();
////       // add userCircle
//        addUserGeoCircle();
//    }
//
//    private void enableUserLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        } else {
//            //Ask for permission
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GPS_REQUEST_CODE) {
//
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//
//            if (providerEnabled) {
//                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void openGps() {
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
//
//        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location
//        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                getDeviceLocation();
//            }
//        });
//
//        //if task failed means gps is disabled so ask user to enable gps
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    ResolvableApiException resolvable = (ResolvableApiException) e;
//                    try {
//                        resolvable.startResolutionForResult(MapTestUserActivity.this, 51);
//                    } catch (IntentSender.SendIntentException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    @SuppressLint("MissingPermission")
//    private void getDeviceLocation() {
//
////        userExitLat = String.valueOf(location.getLatitude());
////        userExitLng = String.valueOf(location.getLongitude())
//        fusedLocationProviderClient.getLastLocation()
//                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            mLAstKnownLocation = task.getResult();
//                            if (mLAstKnownLocation != null) {
//
//
//                                userExitLat = String.valueOf(mLAstKnownLocation.getLatitude());
//                                userExitLng = String.valueOf(mLAstKnownLocation.getLongitude());
//                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                            }
//                            else {
//                                final LocationRequest locationRequest = LocationRequest.create();
//                                locationRequest.setInterval(1000);
//                                locationRequest.setFastestInterval(5000);
//                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                                locationCallback = new LocationCallback() {
//                                    @Override
//                                    public void onLocationResult(LocationResult locationResult) {
//                                        super.onLocationResult(locationResult);
//                                        if (locationResult == null) {
//                                            return;
//                                        }
//                                        mLAstKnownLocation = locationResult.getLastLocation();
//                                        userExitLat = String.valueOf(mLAstKnownLocation.getLatitude());
//                                        userExitLng = String.valueOf(mLAstKnownLocation.getLongitude());
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                                        //remove location updates in order not to continues check location unnecessarily
//                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//
//                                    }
//                                };
//                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, null);
//                            }
//                        } else {
//                            Toast.makeText(MapTestUserActivity.this, "Unable to get last location ", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//    @Override
//    protected void onStop() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//
//        super.onStop();
//    }
//
//    @Override
//    public void onKeyEntered(String key, GeoLocation location) {
//        currentTime = Calendar.getInstance().getTime();
//        if (oldTime == null){
//            submitAttended(getApplicationContext(),userID,timeStamp,userExitLat +","+userExitLng, "0");
//            sendNotification("Employee Enter","Done");
//            oldTime = currentTime;
//        }else {
//            Long diff  = currentTime.getTime() - oldTime.getTime();
//            Long seconds = diff / 1000;
//            Long minutes = seconds / 60;
//            if (minutes>=5){
//                sendNotification("Employee Enter","Done");
//                submitAttended(getApplicationContext(),userID,timeStamp,userExitLat +","+userExitLng, "0");
//
//            }
//        }
//    }
//
//    public void submitAttended(Context mContext, String userId, String timestamp, String latlong,String type){
//        if (internetConnection.isInternetAvailable(mContext)) {
////            final MaterialDialog dialog = new MaterialDialog.Builder(mContext).title(mContext.getResources().getString(R.string.loading))
////                    .content(mContext.getResources().getString(R.string.pleaseWait))
////                    .progress(true, 0)
////                    .cancelable(false)
////                    .show();
//
//            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
//            mApiService.submitAttend(userId,timestamp,latlong,type).enqueue(new Callback<GeoSubmitResponse>() {
//                @Override
//                public void onResponse(Call<GeoSubmitResponse> call, Response<GeoSubmitResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getError() == 0) {
//                            Toast.makeText(mContext, ""+response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
//                            //dialog.dismiss();
//
//                            Log.w("test",""+response.body().getErrorReport());
//                            // sendFBaseTokenToServer();
//                        } else if (response.body().getError() == 1) {
//                           // dialog.dismiss();
//                            Toast.makeText(getApplicationContext(), "Wrong login information.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeoSubmitResponse> call, Throwable t) {
////                    Log.d("LOGIN", "onFailure: " + t.getMessage());
//                }
//            });
//        } else {
//            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onKeyExited(String key) {
//
//        currentTimeExit = Calendar.getInstance().getTime();
//        if (oldTimeExit == null){
//            sendNotification("Employee Exit","Cancel");
//            submitAttended(getApplicationContext(),userID,timeStamp,userExitLat +","+userExitLng, "1");
//
//            oldTimeExit = currentTimeExit;
//        }else {
//            Long diff  = currentTimeExit.getTime() - oldTimeExit.getTime();
//            Long seconds = diff / 1000;
//            Long minutes = seconds / 60;
//            if (minutes>=5){
//                sendNotification("Employee Exit","Cancel");
//                submitAttended(getApplicationContext(),userID,timeStamp,userExitLat +","+userExitLng, "1");
//
//            }
//        }
//    }
//
//    @Override
//    public void onKeyMoved(String key, GeoLocation location) {
//        //   sendNotification("Employer Exit","Cancel");
//
//    }
//
//    @Override
//    public void onGeoQueryReady() {
//        //  sendNotification("Onready","Onready");
//
//    }
//    @Override
//    public void onGeoQueryError(DatabaseError error) {
//        //sendNotification("Employer erorr","erorr");
//
//    }
//
//    private void sendNotification(String title, String details) {
//
//
//        String channel_id = "userLocation";
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
//            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.enableLights(true);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setDescription(title);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(notificationChannel);
//        }
//
//        NotificationCompat.Builder builder = new   NotificationCompat.Builder(this,channel_id);
//        builder.setContentTitle(title);
//        builder.setContentText(details);
//        builder.setAutoCancel(false);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//
//        Notification notification = builder.build();
//
//        notificationManager.notify(new Random().nextInt(),notification);
//
//
//    }
//
//    @Override
//    public void LoadLocationSuccess(List<MyLatLong> area) {
//        areaList = new ArrayList<>();
//
//
//        for (MyLatLong finalLatLong : area){
//            LatLng convert = new LatLng(finalLatLong.getLatitude(),finalLatLong.getLongitude());
//            areaList.add(convert);
//        }
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(MapTestUserActivity.this);
//        }
//
//
//        // clear map and add again
//        if (mMap != null){
//            mMap.clear();
//            // add user Marker
//            addUserMarker();
//            // add user Circle
//            addUserGeoCircle();
//        }
//
//    }
//
//    @Override
//    public void LoadLocationFail(String message) {
//        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
//    }
//
//    public void getGeoLocation(String userId){
//        geoReportLists.clear();
//        if (internetConnection.isInternetAvailable(MapTestUserActivity.this)) {
////            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
////                    .content(getResources().getString(R.string.pleaseWait))
////                    .progress(true, 0)
////                    .cancelable(false)
////                    .show();
//
//            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
//            mApiService.getAllGeo(userId).enqueue(new Callback<GeoFanceReport>() {
//                @Override
//                public void onResponse(Call<GeoFanceReport> call, Response<GeoFanceReport> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body().getError() == 0) {
//                            geoReportLists.addAll(response.body().getReport());
//
//                            for (int i = 0; i< geoReportLists.size();i++){
//
//                                String currentString = geoReportLists.get(i).getGeo();
//                                String[] separated = currentString.split(",");
//                                String lat = separated[0];
//                                String lng = separated[1];
//
//                                Log.d("split","lat"+lat+"lng"+lng);
//                                areList.addAll(Collections.singleton(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
//
//                            }
//                            initArea();
//                            // dialog.dismiss();
//
//                        } else if (response.body().getError() == 1) {
//                            //  dialog.dismiss();
//                            Toast.makeText(MapTestUserActivity.this, "Wrong login information.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GeoFanceReport> call, Throwable t) {
//                    // dialog.dismiss();
//                    Log.d("LOGIN", "onFailure: " + t.getMessage());
//                }
//            });
//        } else {
//            //  Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // unregisterReceiver(br);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //stopServiceFunc();
//        //if (myReceiver != null)unregisterReceiver(myReceiver);
//    }
//
//    private void starServiceFunc(){
//        mLocationService = new LocationService();
//        mServiceIntent = new Intent(this, mLocationService.getClass());
//        startAlert();
//        if (!Util.isMyServiceRunning(mLocationService.getClass(), this)) {
//            startService(mServiceIntent);
//            Toast.makeText(this, getString(R.string.service_start_successfully), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void stopServiceFunc(){
//        mLocationService = new LocationService();
//        mServiceIntent = new Intent(this, mLocationService.getClass());
//        if (Util.isMyServiceRunning(mLocationService.getClass(), this)) {
//            stopService(mServiceIntent);
//            Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show();
//            //saveLocation(); // explore it by your self
//        } else {
//            Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
///*        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        this.registerReceiver(br, filter);*/
//
//        myReceiver = new MyBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.artisoft.geofinder.LocationService.MY_ACTION");
//        //startService(i);
//        registerReceiver(myReceiver, intentFilter);
//    }
//
//    @SuppressLint("InvalidWakeLockTag")
//    public void startAlert(){
//
//        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
//                "MyWakeLock");
//        try {
//            wakeLock.acquire(5 * 60 * 1000L);
//            KeyguardManager keyguard_manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//
//            KeyguardManager.KeyguardLock manager = keyguard_manager.newKeyguardLock("MyKeyguardLock");
//            manager.disableKeyguard();
//
//        }catch (Exception e){
//            wakeLock.release();
//        }finally {
//            wakeLock.release();
//        }
//    }
//
//    public static class MyBroadcastReceiver extends BroadcastReceiver{
//        static final String Log_Tag = "MyReceiver";
//        @Override
//        public void onReceive(Context arg0, Intent arg1){
//            Location location = arg1.getParcelableExtra("locationData");
//            System.out.println("I am here");
//        }
//
//    }
//
//    public static class LocationService extends Service implements GeoQueryEventListener {
//        private GeoQuery geoQuery;
//        FusedLocationProviderClient fusedLocationClient;
//        LocationRequest locationRequest;
//        LocationCallback locationCallback;
//        Boolean isFirstTimeVisit = true;
//        Date currentTime;
//        Date oldTime;
//        Intent intent = new Intent("com.artisoft.geofinder.LocationService.MY_ACTION");
//        private GeoFire geoFire;
//        private DatabaseReference databaseReference;
//
//        private void startLocationUpdates() {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            fusedLocationClient.requestLocationUpdates(locationRequest,
//                    locationCallback,
//                    Looper.getMainLooper());
//        }
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//
//            new Notification();
//
//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            databaseReference = FirebaseDatabase.getInstance().getReference("myLocation");
//            geoFire = new GeoFire(databaseReference);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() ;
//            else startForeground(
//                    1,
//                    new Notification()
//            );
//
//            locationRequest = LocationRequest.create();
//            locationRequest.setInterval(3000);
//            locationRequest.setFastestInterval(3000);
//            locationRequest.setMaxWaitTime(5000);
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//            isFirstTimeVisit = false;
//            if (geoQuery!=null){
//                geoQuery.removeAllListeners();
//            }
//
//            for (LatLng area : areaList){
//                geoQuery = geoFire.queryAtLocation(new GeoLocation(area.latitude,area.longitude), 0.2f);
//                geoQuery.addGeoQueryEventListener(this);
//            }
//
//
//            locationCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(@NonNull LocationResult locationResult) {
//                    Location location =  locationResult.getLastLocation();
//                    intent.putExtra("locationData", location);
//                    sendBroadcast(intent);
//                    if (!isBackGround()){
//                        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
//                            @Override
//                            public void onComplete(String key, DatabaseError error) {
//
//                                Log.d("","");
//                            }
//                        });
//                    }
//                }
//            };
//            startLocationUpdates();
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        private void createNotificationChanel() {
//            String notificationChannelId = "Location channel id";
//            String channelName = "Background Service";
//
//            NotificationChannel chan = new NotificationChannel(
//                    notificationChannelId,
//                    channelName,
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            chan.setLightColor(Color.BLUE);
//            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//
//            manager.createNotificationChannel(chan);
//
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, notificationChannelId);
//
//            Notification notification = notificationBuilder.setOngoing(true)
//                    .setContentTitle("Location updates:")
//                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .build();
//            startForeground(2, notification);
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            super.onStartCommand(intent, flags, startId);
//            return START_STICKY;
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//            fusedLocationClient.removeLocationUpdates(locationCallback);
//        }
//
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        private void sendNotification(String title, String details) {
//
//            String channel_id = "userLocation";
//            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
//                NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
//                notificationChannel.enableLights(true);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setDescription(title);
//                notificationChannel.setLightColor(Color.RED);
//                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.createNotificationChannel(notificationChannel);
//            }
//
//            NotificationCompat.Builder builder = new   NotificationCompat.Builder(this,channel_id);
//            builder.setContentTitle(title);
//            builder.setContentText(details);
//            builder.setAutoCancel(false);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//
//            Notification notification = builder.build();
//
//            notificationManager.notify(new Random().nextInt(),notification);
//
//        }
//
//        private boolean isBackGround(){
//            ActivityManager.RunningAppProcessInfo procces  =  new ActivityManager.RunningAppProcessInfo();
//            ActivityManager.getMyMemoryState(procces);
//            return (procces.importance == IMPORTANCE_FOREGROUND
//                    || procces.importance == IMPORTANCE_VISIBLE);
//        }
//
//        @Override
//        public void onKeyEntered(String key, GeoLocation location) {
//            currentTime = Calendar.getInstance().getTime();
//            if(!isBackGround()){
//                if (oldTime == null){
//                    sendNotification("Employee Enter","Done");
//                    oldTime = currentTime;
//                }else {
//                    Long diff  = currentTime.getTime() - oldTime.getTime();
//                    Long seconds = diff / 1000;
//                    Long minutes = seconds / 60;
//                    if (minutes>=5){
//                        sendNotification("Employee Enter","Done");
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onKeyExited(String key) {
//
//        }
//
//        @Override
//        public void onKeyMoved(String key, GeoLocation location) {
//
//        }
//
//        @Override
//        public void onGeoQueryReady() {
//
//        }
//
//        @Override
//        public void onGeoQueryError(DatabaseError error) {
//
//        }
//    }
//}