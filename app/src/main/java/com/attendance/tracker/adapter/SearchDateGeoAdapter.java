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
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.data.SearchGeoList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SearchDateGeoAdapter extends RecyclerView.Adapter<SearchDateGeoAdapter.ViewHolder> {
    private ArrayList<SearchGeoList> todayOfferModels;
    private OnUserClickListener accClickListener;
    private Context mActivity;

    List<Address> addresses;
    Geocoder geocoder ;
    double latitude;
    double longitude;
    // RecyclerView recyclerView;
    public SearchDateGeoAdapter(ArrayList<SearchGeoList> todayOfferModels, Context mActivity) {
        this.todayOfferModels = todayOfferModels;
        this.mActivity = mActivity;
        geocoder = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
    }

    @Override
    public SearchDateGeoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_geo_search, parent, false);
        SearchDateGeoAdapter.ViewHolder viewHolder = new SearchDateGeoAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchDateGeoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SearchGeoList todayOfferModel = todayOfferModels.get(position);
        holder.status.setText(todayOfferModel.getStatus());

        if (todayOfferModel.getStatus().equals("In")){
            holder.lytStatus.setBackgroundResource(R.drawable.shadow_oval_green);
        }else if (todayOfferModel.getStatus().equals("Out")){
            holder.lytStatus.setBackgroundResource(R.drawable.shadow_oval_red);

        }
        if (todayOfferModel.getGeo() != null) {
            String address = "";
            String lng = "";
            String currentString = todayOfferModel.getGeo();
                String[] separated = currentString.split(",");
                String lat = separated[0];
                if (separated.length > 1){
                     lng = separated[1];
                }else {
                    lng = "null";
                }

                try {


                    latitude = Double.valueOf(lat.trim()).doubleValue();
                    longitude = Double.valueOf(lng.trim()).doubleValue();
                } catch (NumberFormatException e) {
// log e if you want...
                }
                try {
                    if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                        try {
                            address = addresses.get(0).getAddressLine(0);

                        } catch (Exception e) {
                            Toast.makeText(mActivity, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        address = "Address not Valid";
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                // holder.name.setText("Latitude : "+lat);
                holder.date.setText("Date : " + todayOfferModel.getDate());
                //holder.lon.setText("Longitude : "+lng);
                holder.lon.setText("Latitude : " + lat + "   " + "Longitude : " + lng);
                holder.address.setText("address : " + address);

        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                accClickListener.itemUserClick(view, todayOfferModel.getId());
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mActivity, ReportViewMapActivity.class);
                intent.putExtra("Lat",latitude);
                intent.putExtra("Lng",longitude);
                mActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return todayOfferModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name,date,lon,address,status;
        public RelativeLayout lytStatus;


        public ViewHolder(View itemView) {
            super(itemView);
          //  this.imageView = itemView.findViewById(R.id.imageView);
            //this.name = itemView.findViewById(R.id.name);
            this.date = itemView.findViewById(R.id.date);
            this.lon = itemView.findViewById(R.id.lon);
            this.address = itemView.findViewById(R.id.addressID);
            this.status = itemView.findViewById(R.id.status);
            this.lytStatus = itemView.findViewById(R.id.lytStatus);



        }

    }

    public void setClickListener(OnUserClickListener itemClickListener) {
        this.accClickListener = itemClickListener;
    }

    private boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }
}