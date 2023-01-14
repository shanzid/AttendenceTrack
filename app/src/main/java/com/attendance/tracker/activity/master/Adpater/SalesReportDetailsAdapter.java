package com.attendance.tracker.activity.master.Adpater;

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
import com.attendance.tracker.activity.NewCompanyList.CompanyDetailsActivity;
import com.attendance.tracker.data.SalesReportDetailsList;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class SalesReportDetailsAdapter extends RecyclerView.Adapter<SalesReportDetailsAdapter.ViewHolder> {
    private ArrayList<SalesReportDetailsList> mData;
    private Context mActivity;
    AppSessionManager appSessionManager;
    private OnUserClickListener itemClickListener;

    // RecyclerView recyclerView;
    public SalesReportDetailsAdapter(ArrayList<SalesReportDetailsList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public SalesReportDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_sales_report_details, parent, false);
        SalesReportDetailsAdapter.ViewHolder viewHolder = new SalesReportDetailsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SalesReportDetailsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SalesReportDetailsList todayOfferModel = mData.get(position);


        holder.invoice.setText("Invoice : \t" + todayOfferModel.getInvoice());
        holder.company.setText("Company : \t" + todayOfferModel.getCompany());
        holder.name.setText("Name : \t" + todayOfferModel.getName());
        holder.mobile.setText("Mobile : \t" + todayOfferModel.getMobile1());
        holder.totalempoyee.setText("Direct Sale :\t" + todayOfferModel.getDirectSale());
        holder.present.setText("Renew : \t" + todayOfferModel.getRenew());

        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile1(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

        holder.company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, CompanyDetailsActivity.class);
                intent.putExtra("UserID",todayOfferModel.getId());
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
        public TextView invoice,company,name,mobile,totalempoyee, present;


        public ViewHolder(View itemView) {
            super(itemView);
            this.invoice = itemView.findViewById(R.id.invoice);
            this.company = itemView.findViewById(R.id.company);
            this.name= itemView.findViewById(R.id.name);
            this.mobile= itemView.findViewById(R.id.mobile);
            this.totalempoyee = itemView.findViewById(R.id.totalTV);
            this.present = itemView.findViewById(R.id.presentTV);


        }

    }


}
