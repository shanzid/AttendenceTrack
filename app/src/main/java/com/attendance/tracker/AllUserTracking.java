package com.attendance.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.attendance.tracker.data.ProfileData;
import com.attendance.tracker.data.ProfileList;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllUserTracking extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    AppSessionManager appSessionManager;
    private ArrayList<ProfileList> userDataList;
    private CheckInternetConnection internetConnection;
    String userId, search;
    private String userType, userName;
    private DatabaseReference databaseReference;
    String path = "ownUserLocation";
    String endChild = "l";
    String userID = "";
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    boolean isFirstTime = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_tracking);
        appSessionManager = new AppSessionManager(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("ownUserLocation");
        //getMyLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.back).setOnClickListener(view -> finish());
        internetConnection = new CheckInternetConnection();
        appSessionManager = new AppSessionManager(this);
        Intent mIntent = getIntent();
        userType = mIntent.getStringExtra("type");
        userDataList = new ArrayList<>();
    }

    private void getMyLocation() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String key = postSnapshot.getKey();
                    String lat = postSnapshot.child("latitute").getValue(String.class);
                    String lon = postSnapshot.child("logititude").getValue(String.class);
                    String userName = postSnapshot.child("userName").getValue(String.class);

                    if(lat !=null && lon !=null){
                        LatLng latLon = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                        if (userName !=null){
                            mMap.clear();
                            Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(latLon).
                                    icon(BitmapDescriptorFactory.fromBitmap(
                                            createCustomMarker(AllUserTracking.this, R.drawable.ic_set_location, userName)))));

                            // mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Test User"));
                            mMap.setOnMarkerClickListener((new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    loadNavigationView(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
/*                                Geocoder geocoder = new Geocoder(AllUserTracking.this, Locale.getDefault());
                                List<Address>addresses = null; //1 num of possible location returned
                                try {
                                    addresses = geocoder.getFromLocation(Double.parseDouble(String.valueOf(marker.getPosition().latitude)), Double.parseDouble(String.valueOf(marker.getPosition().longitude)), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                //create your custom title
                                String title = address +"-"+city+"-"+state;
                                marker.setTitle(title);
                                marker.showInfoWindow();*/
                                    return false;
                                }
                            }));
                        }
                    }


                    /*for (int i=0; i<userDataList.size(); i++){
                            UserListData users = new UserListData();
                            users.setName(userDataList.get(i).getName());
                            users.setLat(lat);
                            users.setLat(lon);
                            Log.d("user",users.toString());
                            LatLng latLon = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                            Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(latLon).
                                    icon(BitmapDescriptorFactory.fromBitmap(
                                            createCustomMarker(AllUserTracking.this, R.drawable.ic_set_location, userDataList.get(i).getName())))));

                            // mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Test User"));
                            mMap.setOnMarkerClickListener((new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    //loadNavigationView(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
                                    Geocoder geocoder = new Geocoder(AllUserTracking.this, Locale.getDefault());
                                    List<Address>addresses = null; //1 num of possible location returned
                                    try {
                                        addresses = geocoder.getFromLocation(Double.parseDouble(String.valueOf(marker.getPosition().latitude)), Double.parseDouble(String.valueOf(marker.getPosition().longitude)), 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    //create your custom title
                                    String title = address +"-"+city+"-"+state;
                                    marker.setTitle(title);
                                    marker.showInfoWindow();
                                    return false;
                                }
                            }));

                    }*/

                    //userDataList.clear();

                    Log.d("",userId);
/*
                    BookList bookList = new BookList();
                    bookList.setbookName(bookName);

                    quoteRequestListArrayList.add(bookList);*/

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read app title value.", error.toException());
            }
        });

    }

    private void setUpMap(double lat,double longs,String date) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(lat, longs);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Last Update \t"+date);
        markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.walk)));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    @Override
    protected void onResume() {
        userId = appSessionManager.getUserDetails().get(AppSessionManager.KEY_USERID);
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                getMyLocation();

                //getMyLocation();
                //  Toast.makeText(Live_trackerMapsActivity.this, "every 5", Toast.LENGTH_SHORT).show();
                //getProfileData(userId, userType, "");
            }
        }, delay);

        getProfileData(userId, userType, "");

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        //addAlluser();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, ""+connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }


    public void getProfileData(String userId, String type,String search) {
        if (internetConnection.isInternetAvailable(AllUserTracking.this)) {

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.getProfileList(userId, type,search).enqueue(new Callback<ProfileData>() {
                @Override
                public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            if (userDataList !=null){
                                userDataList.clear();
                            }
                            userDataList.addAll(response.body().getReport());
                        } else if (response.body().getError() == 1) {
                            Toast.makeText(AllUserTracking.this, "No Data Found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfileData> call, Throwable t) {
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addAlluser(){
        ArrayList<LatLng> locationArrayList;

        LatLng sydney = new LatLng(23.80184912282346, 90.3705284259997);
        LatLng TamWorth = new LatLng(23.795684332393666, 90.3644344470757);
        LatLng NewCastle = new LatLng(23.794584848586485, 90.38228723040238);
        LatLng Brisbane = new LatLng(23.803812370457035, 90.35443517179895);
        locationArrayList = new ArrayList<>();
        locationArrayList.add(sydney);
        locationArrayList.add(TamWorth);
        locationArrayList.add(NewCastle);
        locationArrayList.add(Brisbane);
        for (int i = 0; i < locationArrayList.size(); i++) {

            LatLng latLng = new LatLng((Double.parseDouble(String.valueOf(locationArrayList.get(i).latitude))),(Double.parseDouble(String.valueOf(locationArrayList.get(i).latitude))));

            Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(AllUserTracking.this, R.drawable.ic_set_location, "Narender")))));

           // mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Test User"));
            mMap.setOnMarkerClickListener((new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    //loadNavigationView(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
                    Geocoder geocoder = new Geocoder(AllUserTracking.this, Locale.getDefault());
                    List<Address>addresses = null; //1 num of possible location returned
                    try {
                        addresses = geocoder.getFromLocation(Double.parseDouble(String.valueOf(marker.getPosition().latitude)), Double.parseDouble(String.valueOf(marker.getPosition().longitude)), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    //create your custom title
                    String title = address +"-"+city+"-"+state;
                    marker.setTitle(title);
                    marker.showInfoWindow();
                    return false;
                }
            }));
        }
    }

    public void loadNavigationView(String lat,String lng){
        Uri navigation = Uri.parse("google.navigation:q="+lat+","+lng+"");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView txt_name = (TextView)marker.findViewById(R.id.userName);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}
