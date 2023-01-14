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
import com.attendance.tracker.data.blocklistdata;
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.utils.AppSessionManager;

import java.util.ArrayList;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.ViewHolder> {
    private ArrayList<blocklistdata> mData;
    private Context mActivity;
    AppSessionManager appSessionManager;
    OnBlockListener blockListener;

    // RecyclerView recyclerView;
    public BlockListAdapter(ArrayList<blocklistdata> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
        appSessionManager = new AppSessionManager(mActivity);
    }

    @Override
    public BlockListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_block_list, parent, false);
        BlockListAdapter.ViewHolder viewHolder = new BlockListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BlockListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final blocklistdata todayOfferModel = mData.get(position);


        holder.name.setText("Name:\t\t" + todayOfferModel.getName());
        holder.mobile.setText("Mobile:\t\t" + todayOfferModel.getMobile());
        holder.join.setText("Join Date:\t\t" + todayOfferModel.getJoinDate());

        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);

            }
        });


        holder.ImvunBloked.setOnClickListener(new View.OnClickListener() {
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
        public ImageView ImvunBloked;
        public TextView name,mobile,join;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.nameTV);
            this.mobile = itemView.findViewById(R.id.mobileTV);
            this.join = itemView.findViewById(R.id.joindateTV);
            this.ImvunBloked = itemView.findViewById(R.id.ImvunBloked);



        }

    }

    public void setOnBlockListener(OnBlockListener blockListener){
        this.blockListener = blockListener;
    }

}