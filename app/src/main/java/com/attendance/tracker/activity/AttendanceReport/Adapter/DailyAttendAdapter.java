package com.attendance.tracker.activity.AttendanceReport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.activity.AttendanceReport.DailyAttenReportList;
import com.attendance.tracker.activity.NewCompanyList.CompanyReportList;
import com.attendance.tracker.utils.AppSessionManager;
import com.firebase.geofire.util.Constants;

import java.util.ArrayList;

public class DailyAttendAdapter extends RecyclerView.Adapter<DailyAttendAdapter.ViewHolder> {
    private ArrayList<DailyAttenReportList> mData;
    private Context mActivity;

AppSessionManager appSessionManager;
    // RecyclerView recyclerView;
    public DailyAttendAdapter(ArrayList<DailyAttenReportList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public DailyAttendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_daily_attend_list, parent, false);
        DailyAttendAdapter.ViewHolder viewHolder = new DailyAttendAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DailyAttendAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final DailyAttenReportList todayOfferModel = mData.get(position);


        holder.name.setText("Name:\t\t"+todayOfferModel.getName());
        holder.mobile.setText("Mobile:\t\t"+todayOfferModel.getMobile());
        holder.ins.setText("Ins:\t\t"+todayOfferModel.getIns());
        holder.out.setText("Out:\t\t"+todayOfferModel.getOut());

        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name,mobile,ins,out;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.NameTV);
            this.mobile = itemView.findViewById(R.id.MobileTV);
            this.ins = itemView.findViewById(R.id.InsTV);
            this.out = itemView.findViewById(R.id.OutTV);



        }

    }

}