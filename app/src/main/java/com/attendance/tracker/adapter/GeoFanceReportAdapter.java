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
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.GeoReportMapActivity;
import com.attendance.tracker.R;
import com.attendance.tracker.data.GeoFanceReportList;
import com.attendance.tracker.interfaces.OnDeleteListener;
import com.attendance.tracker.interfaces.OnUserClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoFanceReportAdapter extends RecyclerView.Adapter<GeoFanceReportAdapter.ViewHolder> {
    private ArrayList<GeoFanceReportList> todayOfferModels;
    private Context mActivity;
    private OnDeleteListener deleteListener;
    Geocoder geocoder ;
    List<Address> addresses;
    static  double latitude;
    static double longitude;
    private OnUserClickListener itemClickListener;

    // RecyclerView recyclerView;
    public GeoFanceReportAdapter(ArrayList<GeoFanceReportList> todayOfferModels, Context mActivity) {
        this.todayOfferModels = todayOfferModels;
        this.mActivity = mActivity;
        geocoder = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
    }

    @Override
    public GeoFanceReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_geo_report, parent, false);
        GeoFanceReportAdapter.ViewHolder viewHolder = new GeoFanceReportAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GeoFanceReportAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final GeoFanceReportList todayOfferModel = todayOfferModels.get(position);
        String address = "";
        String currentString = todayOfferModel.getGeo();
        String[] separated = currentString.split(",");
        String lat = separated[0];
        String lng = separated[1];

        try {


            latitude =  Double.valueOf(lat.trim()).doubleValue();
            longitude =  Double.valueOf(lng.trim()).doubleValue();
        }
        catch (NumberFormatException e)
        {
// log e if you want...
        }
        try {
            if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180){
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5



                try{
                    address = addresses.get(0).getAddressLine(0);

                }
                catch(Exception e) {
                    Toast.makeText(mActivity,"No Data Found", Toast.LENGTH_SHORT).show();
                }

            }else {
                address = "Address not Valid";
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        // holder.name.setText("Latitude : "+lat);
        holder.date.setText("Date : "+todayOfferModel.getDate());
        //holder.lon.setText("Longitude : "+lng);
        //   holder.lat.setText("Latitude : "+lat+"   "+"Longitude : "+lng);
        holder.addres.setText("address : "+address);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemUserClick(view,position);
            }
        });
        holder.deleteGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteListener.itemDeleteClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayOfferModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,deleteGeo;
        public TextView name,date,lat,addres;


        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.addres = itemView.findViewById(R.id.address);
            this.date = itemView.findViewById(R.id.date);
            //   this.lat = itemView.findViewById(R.id.lat);
            this.deleteGeo = itemView.findViewById(R.id.deleteGeo);
            //this.radius = itemView.findViewById(R.id.radius);


        }

    }

    public void setClickListener(OnDeleteListener itemClickListener) {
        this.deleteListener = itemClickListener;
    }

    public void setItemClickListener(OnUserClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
