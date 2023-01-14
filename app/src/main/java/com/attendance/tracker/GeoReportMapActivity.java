package com.attendance.tracker;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.attendance.tracker.databinding.ActivityReportViewMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeoReportMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityReportViewMapBinding binding;
    String lat,lng;
    double lati,longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_report_map);


        lat = getIntent().getStringExtra("latitude");
        lng = getIntent().getStringExtra("longtitude");
        lati = Double.parseDouble(lat.trim());
        longi = Double.parseDouble(lng.trim());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapGeo);
        mapFragment.getMapAsync(GeoReportMapActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng OwnLoc = new LatLng(lati, longi);
      //  Toast.makeText(this, "lat"+lat+"long"+lng, Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(OwnLoc).title("Your Location here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(OwnLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}