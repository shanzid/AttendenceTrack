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
import com.attendance.tracker.activity.AgentList.AgentListReport;
import com.attendance.tracker.activity.NewCompanyList.CompanyReportList;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> {
    private ArrayList<AgentListReport> mData;
    private Context mActivity;
    private OnBlockListener blockListener;

AppSessionManager appSessionManager;
    // RecyclerView recyclerView;
    public AgentListAdapter(ArrayList<AgentListReport> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public AgentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_agent_list, parent, false);
        AgentListAdapter.ViewHolder viewHolder = new AgentListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AgentListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AgentListReport todayOfferModel = mData.get(position);

        if (todayOfferModel.getName().equals("")){
            holder.name.setText("No User Name");

        }else {
            holder.name.setText("Name:\t"+todayOfferModel.getName());

        }
        holder.number1.setText("Mobile:\t"+todayOfferModel.getMobile1());
        holder.sale.setText("Sale:\t"+todayOfferModel.getSale());
        holder.mobile2.setText("Mobile 2:\t"+todayOfferModel.getMobile2());
        holder.company_count.setText("Company Count:\t"+todayOfferModel.getCompanyCount());

        holder.number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile1(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockListener.itemUserBlockClick(view,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name,number1,company_count,company,mobile2,sale;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.number1 = itemView.findViewById(R.id.mobileTV);
            this.sale = itemView.findViewById(R.id.saleChargeTV);
            this.mobile2 = itemView.findViewById(R.id.mobile2TV);
            this.company_count = itemView.findViewById(R.id.company_countTV);


        }

    }

    public void setClickListener(OnBlockListener blockListener){
        this.blockListener = blockListener;
    }

}