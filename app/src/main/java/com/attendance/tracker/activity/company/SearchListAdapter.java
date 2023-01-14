package com.attendance.tracker.activity.company;

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
import com.attendance.tracker.data.SearchData;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private ArrayList<SearchData> mData;
    private Context mActivity;
    private OnBlockListener blockListener;
AppSessionManager appSessionManager;
    // RecyclerView recyclerView;
    public SearchListAdapter(ArrayList<SearchData> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_search, parent, false);
        SearchListAdapter.ViewHolder viewHolder = new SearchListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SearchData todayOfferModel = mData.get(position);

        holder.name.setText("Name:\t\t"+todayOfferModel.getName());
        holder.mobile.setText("Mobile:\t\t"+todayOfferModel.getMobile());
        holder.mobile2.setText("Mobile2:\t\t"+todayOfferModel.getMobile2());
        holder.address.setText("Address:\t\t"+todayOfferModel.getAddress());
        holder.nid.setText("NID:\t\t"+todayOfferModel.getNid());
        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +  todayOfferModel.getMobile()));
                mActivity.startActivity(callIntent);
            }
        });
        holder.mobile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +  todayOfferModel.getMobile2()));
                mActivity.startActivity(callIntent);
            }
        });
        holder.blockUser.setOnClickListener(new View.OnClickListener() {
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
        public ImageView imageView,blockUser;
        public TextView name,mobile,mobile2,address,nid;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.mobile = itemView.findViewById(R.id.mobileTV);
            this.mobile2 = itemView.findViewById(R.id.mobile2TV);
            this.address = itemView.findViewById(R.id.addressTV);
            this.nid = itemView.findViewById(R.id.nidTV);
            this.blockUser = itemView.findViewById(R.id.blockUser);


        }

    }
    public void setOnBlockListener(OnBlockListener blockListener) {
        this.blockListener = blockListener;
    }

}