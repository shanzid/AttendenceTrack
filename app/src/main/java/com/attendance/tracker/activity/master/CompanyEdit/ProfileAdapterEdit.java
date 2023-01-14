package com.attendance.tracker.activity.master.CompanyEdit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.R;
import com.attendance.tracker.data.ProfileList;
import com.attendance.tracker.interfaces.OnDeleteListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.utils.AppSessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileAdapterEdit extends RecyclerView.Adapter<ProfileAdapterEdit.MyViewHolder> {

    private Context mContext;
    private ArrayList<ProfileList> mData;
    double latitude;
    double longitude;
    private AppSessionManager appSessionManager;
    private OnUserClickListener onCategoryItemClick;
    private OnDeleteListener onDeleteListener;

    public ProfileAdapterEdit(Context mContext, ArrayList<ProfileList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        appSessionManager = new AppSessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_company_edit, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.companyName.setText("Company : "+mData.get(position).getCompany());
        holder.name.setText("Name : "+mData.get(position).getName());
        holder.MobileTV.setText("Mob : "+mData.get(position).getMobile());
        holder.number2TV.setText("Mob2 : "+mData.get(position).getMobile2());
        holder.employeesTV.setText("Address : "+mData.get(position).getAddress());

        String userType = appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY);

        //delete profile for company code here

        if (userType.equals("3")) {
            holder.deleteProfile.setVisibility(View.VISIBLE);
            holder.deleteProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteListener.itemDeleteClick(view, position);
                }
            });

        }
        else if(userType.equals("2")){
            holder.deleteProfile.setVisibility(View.VISIBLE);
            holder.deleteProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteListener.itemDeleteClick(view, position);
                }
            });
        }
        else {
            holder.deleteProfile.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryItemClick.itemUserClick(view, position);
            }
        });

        holder.MobileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  mData.get(position).getMobile(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        holder.number2TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  mData.get(position).getMobile2(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView companyName,name, MobileTV, number2TV,employeesTV;
        ImageView deleteProfile;


        public MyViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyTV);
            name = itemView.findViewById(R.id.nameTV);
            MobileTV = itemView.findViewById(R.id.MobileTV);
            number2TV = itemView.findViewById(R.id.number2TV);
            employeesTV = itemView.findViewById(R.id.employeesTV);
            deleteProfile = itemView.findViewById(R.id.deleteProfile);
        }
    }


    public void SetItemClick(OnUserClickListener onCategoryItemClick) {
        this.onCategoryItemClick = onCategoryItemClick;
    }

    public void deleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }


}
