package com.attendance.tracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.ReportViewMapActivity;
import com.attendance.tracker.data.GeoReportList;
import com.attendance.tracker.interfaces.LatLongListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GeoAttendanceAdapter extends RecyclerView.Adapter<GeoAttendanceAdapter.ViewHolder> {
    private ArrayList<GeoReportList> todayOfferModels;
    private Context mActivity;
    List<Address> addresses;
    Geocoder geocoder;
    double latitude;
    double longitude;
    private LatLongListener latLongListener;
    String lng = "";

    // RecyclerView recyclerView;
    public GeoAttendanceAdapter(ArrayList<GeoReportList> todayOfferModels, Context mActivity) {
        this.todayOfferModels = todayOfferModels;
        this.mActivity = mActivity;
        geocoder = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
        //  this.appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public GeoAttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_geo, parent, false);
        GeoAttendanceAdapter.ViewHolder viewHolder = new GeoAttendanceAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GeoAttendanceAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final GeoReportList todayOfferModel = todayOfferModels.get(position);
        String address = "";
        String currentString = todayOfferModel.getGeo();
        String[] separated = currentString.split(",");
        String lat = separated[0];
        if (separated.length > 1) {
            lng = separated[1];
        } else {
            lng = "null";
        }
        try {
            latitude = Double.parseDouble(lat.trim());
            longitude = Double.parseDouble(lng.trim());
        } catch (NumberFormatException e) {
        }

        if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() != 0) {
                address = addresses.get(0).getAddressLine(0);

            }
        } else {
            address = "Geo not Valid";
        }

        holder.name.setText("Address : " + address);
        holder.date.setText("Date : " + todayOfferModel.getDate());
        holder.lat.setText("Latitude : " + lat + "   " + "Longitude : " + lng);
        holder.status.setText(todayOfferModel.getStatus());
        if (todayOfferModel.getStatus().equals("IN")) {
            holder.lytStatus.setBackgroundResource(R.drawable.shadow_oval_green);
        } else if (todayOfferModel.getStatus().equals("OUT")) {
            holder.lytStatus.setBackgroundResource(R.drawable.shadow_oval_red);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedlatlon = todayOfferModel.getGeo();
                String[] separated = selectedlatlon.split(",");
                String lat = separated[0];
                if (separated.length > 1) {
                    lng = separated[1];
                } else {
                    lng = "null";
                }
                if (lat.equals("null") || lng.equals("null")) {
                    Toast.makeText(mActivity, "Invalid location", Toast.LENGTH_SHORT).show();
                } else {
                    Double a = Double.parseDouble(lat);
                    Double b = Double.parseDouble(lng);
                    Intent intent = new Intent(mActivity, ReportViewMapActivity.class);
                    intent.putExtra("Lat", a);
                    intent.putExtra("Lng", b);
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayOfferModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, deleteGeo;
        public TextView name, date, lat, status;
        public RelativeLayout lytStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.name = itemView.findViewById(R.id.name);
            this.date = itemView.findViewById(R.id.date);
            this.lat = itemView.findViewById(R.id.lat);
            this.deleteGeo = itemView.findViewById(R.id.deleteGeo);
            this.status = itemView.findViewById(R.id.status);
            this.lytStatus = itemView.findViewById(R.id.lytStatus);

        }
    }

    public void SetLatLongClick(LatLongListener latLongListener) {
        this.latLongListener = latLongListener;
    }
}