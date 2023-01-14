package com.attendance.tracker.activity.master.Adpater;

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
import com.attendance.tracker.interfaces.OnBlockListener;
import com.attendance.tracker.interfaces.OnDeleteListener;
import com.attendance.tracker.interfaces.OnUserClickListener;
import com.attendance.tracker.utils.AppSessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ProfileList> mData;
    double latitude;
    double longitude;
    private AppSessionManager appSessionManager;
    private OnUserClickListener onCategoryItemClick;
    private OnDeleteListener onDeleteListener;
    private OnBlockListener blockListener;

    public ProfileAdapter(Context mContext, ArrayList<ProfileList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        appSessionManager = new AppSessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_company, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String userType = appSessionManager.getUserDetails().get(AppSessionManager.KEY_CATEGORY);
        holder.companyName.setText(mData.get(position).getName());
       // holder.Div.setText(mData.get(position).ge());
        holder.MobileNo.setText(mData.get(position).getMobile());
        holder.tvCompany.setText(mData.get(position).getCompany());
        holder.address.setText(mData.get(position).getAddress());
        //Glide.with(mContext).load(ConstantValue.ImageURL+""+ mData.get(position).getImg()).into(holder.img);

        Glide.with(mContext).load(BuildConfig.BASE_URL+ mData.get(position).getImg()).apply(new RequestOptions().
                error(R.drawable.user).placeholder(R.drawable.user).fitCenter()).into(holder.img);

        //delete profile for company code here
        if (userType.equals("2")){
            holder.blockUser.setVisibility(View.VISIBLE);
            holder.blockUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    blockListener.itemUserBlockClick(view,position);
                }
            });
        }else {
            holder.blockUser.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryItemClick.itemUserClick(view, position);
            }
        });

//        holder.MobileNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  mData.get(position).getMobile(), null));
//                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView companyName, Div, MobileNo,tvCompany,address;
        ImageView blockUser;
        CircleImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.company_nameTV);
           // Div = itemView.findViewById(R.id.divTV);
            MobileNo = itemView.findViewById(R.id.noTV);
            blockUser = itemView.findViewById(R.id.blockUser);
              img = itemView.findViewById(R.id.imageView);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            address = itemView.findViewById(R.id.address);
        }
    }


    public void SetItemClick(OnUserClickListener onCategoryItemClick) {
        this.onCategoryItemClick = onCategoryItemClick;
    }

    public void deleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
    public void setOnBlockListener(OnBlockListener blockListener) {
        this.blockListener = blockListener;
    }


}
