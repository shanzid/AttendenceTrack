package com.attendance.tracker.agent;

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

import com.attendance.tracker.data.DueList;
import com.attendance.tracker.data.SearchData;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class DueAdapter extends RecyclerView.Adapter<DueAdapter.ViewHolder> {
    private ArrayList<DueList> mData;
    private Context mActivity;
    private OnBlockListener blockListener;
    OnBottomReachedListener onBottomReachedListener;

    // RecyclerView recyclerView;
    public DueAdapter(ArrayList<DueList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;

    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @Override
    public DueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_due, parent, false);
        DueAdapter.ViewHolder viewHolder = new DueAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DueAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (position == mData.size() - 1) {
            onBottomReachedListener.onBottomReached(position);
        }
        final DueList todayOfferModel = mData.get(position);
        holder.name.setText("Name:\t\t"+todayOfferModel.getName());
        holder.mobile.setText("Mobile:\t\t"+todayOfferModel.getMobile1());
        holder.mobile2.setText("Mobile2:\t\t"+todayOfferModel.getMobile2());
        holder.company.setText("Company:\t\t"+todayOfferModel.getCompany());
        holder.due.setText("Due:\t\t"+todayOfferModel.getDue());

        holder.mobile.setOnClickListener(new View.OnClickListener() {
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


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name,mobile,mobile2,company,due;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.mobile = itemView.findViewById(R.id.mobileTV);
            this.mobile2 = itemView.findViewById(R.id.mobile2TV);
            this.company = itemView.findViewById(R.id.companyTV);
            this.due = itemView.findViewById(R.id.dueTV);


        }

    }
    public void setOnBlockListener(OnBlockListener blockListener) {
        this.blockListener = blockListener;
    }

}