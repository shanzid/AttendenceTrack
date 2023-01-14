package com.attendance.tracker.activity.AttendanceReport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.activity.AttendanceReport.AttendanceReportList;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class AttendWeekAdapter extends RecyclerView.Adapter<AttendWeekAdapter.ViewHolder> {
    private ArrayList<AttendanceReportList> mData;
    private Context mActivity;

    AppSessionManager appSessionManager;

    // RecyclerView recyclerView;
    public AttendWeekAdapter(ArrayList<AttendanceReportList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public AttendWeekAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.itemweek_attend_list, parent, false);
        AttendWeekAdapter.ViewHolder viewHolder = new AttendWeekAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttendWeekAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AttendanceReportList todayOfferModel = mData.get(position);


        holder.day.setText("Date : \t" + todayOfferModel.getDay()+"\t"+ todayOfferModel.getMonth()+"\t"+todayOfferModel.getYear());
        holder.totalempoyee.setText("Total Employee :\t" + todayOfferModel.getTotalEmployee());
        holder.present.setText("Present : \t" + todayOfferModel.getPresent());


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

}