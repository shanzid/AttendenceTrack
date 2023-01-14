package com.attendance.tracker.adapter;

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
import com.attendance.tracker.activity.NewCompanyList.CompanyReportList;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {
    private ArrayList<CompanyReportList> mData;
    private Context mActivity;
    private OnUserClickListener onCategoryItemClick;
    private OnBlockListener onBlockListener;

    AppSessionManager appSessionManager;

    // RecyclerView recyclerView;
    public CompanyListAdapter(ArrayList<CompanyReportList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public CompanyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_company_list, parent, false);
        CompanyListAdapter.ViewHolder viewHolder = new CompanyListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CompanyListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final CompanyReportList todayOfferModel = mData.get(position);

        if (todayOfferModel.getName().equals("")) {
            holder.name.setText("No User Name");

        } else {
            holder.name.setText("Authority:\t\t" + todayOfferModel.getName());

        }
        holder.number1.setText("Mob1:\t\t" + todayOfferModel.getMobile1());
        holder.employess.setText("Employee:\t\t" + todayOfferModel.getEmployees());
        holder.company.setText("Company:\t\t" + todayOfferModel.getCompany());
        holder.mobile2.setText("Mob2:\t\t" + todayOfferModel.getMobile2());
        holder.monthlycharge.setText("Monthly Charge:\t\t" + todayOfferModel.getMonthlyCharge());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryItemClick.itemUserClick(view, position);
            }
        });

        holder.mobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile2(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });
        holder.number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile1(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });


        holder.ImUserBloked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBlockListener.itemUserBlockClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,ImUserBloked;
        public TextView name, number1, employess, company, mobile2, monthlycharge;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.number1 = itemView.findViewById(R.id.MobileTV);
            this.employess = itemView.findViewById(R.id.employeesTV);
            this.company = itemView.findViewById(R.id.companyTV);
            this.mobile2 = itemView.findViewById(R.id.number2TV);
            this.monthlycharge = itemView.findViewById(R.id.monthlychargeTV);
            this.ImUserBloked = itemView.findViewById(R.id.ImUserBloked);


        }

    }

    public void SetItemClick(OnUserClickListener onCategoryItemClick) {
        this.onCategoryItemClick = onCategoryItemClick;
    }

    public void SetItemBlockClick(OnBlockListener blockListener) {
        this.onBlockListener = blockListener;
    }


}