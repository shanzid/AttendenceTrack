package com.attendance.tracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.data.UserList;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserList> todayOfferModels;
    private OnUserClickListener accClickListener;
    private Context mActivity;


    // RecyclerView recyclerView;
    public UserAdapter(List<UserList> todayOfferModels, Context mActivity) {
        this.todayOfferModels = todayOfferModels;
        this.mActivity = mActivity;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_user, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final UserList todayOfferModel = todayOfferModels.get(position);

        if (todayOfferModel.getName().equals("")){
            holder.name.setText("No User Name");

        }else {
            holder.name.setText(todayOfferModel.getName());

        }
        holder.number.setText(todayOfferModel.getMobile());
        // holder.imageView.setImageResource(todayOfferModel.getImage());
        holder.phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  todayOfferModel.getMobile(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accClickListener.itemUserClick(view, Integer.parseInt(todayOfferModel.getId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return todayOfferModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name,number;
        public LinearLayout phoneCall;


        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.name = itemView.findViewById(R.id.name);
            this.number = itemView.findViewById(R.id.number);
            this.phoneCall = itemView.findViewById(R.id.phoneCall);
        }

    }

    public void setClickListener(OnUserClickListener itemClickListener) {
        this.accClickListener = itemClickListener;
    }
}