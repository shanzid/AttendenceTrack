package com.attendance.tracker.activity.company.fragment.adapter;

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
import com.attendance.tracker.agent.DueAdapter;
import com.attendance.tracker.agent.OnBottomReachedListener;
import com.attendance.tracker.agent.SelfDueList;
import com.attendance.tracker.data.DueList;
import com.attendance.tracker.interfaces.OnBlockListener;

import java.util.ArrayList;

public class SelfDueAdapter extends RecyclerView.Adapter<SelfDueAdapter.ViewHolder> {
    private ArrayList<SelfDueList> mData;
    private Context mActivity;
    private OnBlockListener blockListener;
    OnBottomReachedListener onBottomReachedListener;

    // RecyclerView recyclerView;
    public SelfDueAdapter(ArrayList<SelfDueList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;

    }

//    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
//        this.onBottomReachedListener = onBottomReachedListener;
//    }

    @Override
    public SelfDueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_self_due, parent, false);
        SelfDueAdapter.ViewHolder viewHolder = new SelfDueAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SelfDueAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

//        if (position == mData.size() - 1) {
//            onBottomReachedListener.onBottomReached(position);
//        }
        final SelfDueList todayOfferModel = mData.get(position);
        holder.name.setText("Name:\t\t"+todayOfferModel.getAgentName());
        holder.mobile.setText("Month:\t\t"+todayOfferModel.getForMonth());
        holder.mobile2.setText("Due:\t\t"+todayOfferModel.getDue()+"৳");
        holder.company.setText("Invoice:\t\t"+todayOfferModel.getInvoice());
        holder.due.setText("Mobile:\t\t"+todayOfferModel.getAgentMobile());

        holder.due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + todayOfferModel.getAgentMobile()));
                mActivity.startActivity(callIntent);
            }
        });

        holder.tvGetPaid.setOnClickListener(new View.OnClickListener() {
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
        public TextView name,mobile,mobile2,company,due,tvGetPaid;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.mobile = itemView.findViewById(R.id.mobileTV);
            this.mobile2 = itemView.findViewById(R.id.mobile2TV);
            this.company = itemView.findViewById(R.id.companyTV);
            this.due = itemView.findViewById(R.id.dueTV);
            this.tvGetPaid = itemView.findViewById(R.id.tvGetPaid);


        }

    }
    public void setOnBlockListener(OnBlockListener blockListener) {
        this.blockListener = blockListener;
    }

}