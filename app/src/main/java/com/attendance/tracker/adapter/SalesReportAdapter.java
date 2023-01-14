package com.attendance.tracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.data.SalesReportList;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.ViewHolder> {
    private ArrayList<SalesReportList> mData;
    private Context mActivity;
    AppSessionManager appSessionManager;
    private OnUserClickListener itemClickListener;

    // RecyclerView recyclerView;
    public SalesReportAdapter(ArrayList<SalesReportList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public SalesReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.itemweek_attend_list, parent, false);
        SalesReportAdapter.ViewHolder viewHolder = new SalesReportAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SalesReportAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SalesReportList todayOfferModel = mData.get(position);


        holder.day.setText("Day : \t" + todayOfferModel.getDay());
        holder.totalempoyee.setText("Direct Sale :\t" + todayOfferModel.getDirectSale());
        holder.present.setText("Renew : \t" + todayOfferModel.getRenew());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemUserClick(view,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView day,  totalempoyee, present;


        public ViewHolder(View itemView) {
            super(itemView);
            this.day = itemView.findViewById(R.id.dayTV);
            this.totalempoyee = itemView.findViewById(R.id.totalTV);
            this.present = itemView.findViewById(R.id.presentTV);


        }

    }

    public void SetItemClickListener(OnUserClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

}
