package com.attendance.tracker.activity.company.fragment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.agent.OnBottomReachedListener;
import com.attendance.tracker.agent.SelfDueList;
import com.attendance.tracker.data.PaymentList;
import com.attendance.tracker.interfaces.OnBlockListener;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private ArrayList<PaymentList> mData;
    private Context mActivity;
    private OnBlockListener blockListener;
    OnBottomReachedListener onBottomReachedListener;

    // RecyclerView recyclerView;
    public PaymentAdapter(ArrayList<PaymentList> mData, Context mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;

    }

//    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
//        this.onBottomReachedListener = onBottomReachedListener;
//    }

    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_payment, parent, false);
        PaymentAdapter.ViewHolder viewHolder = new PaymentAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PaymentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PaymentList todayOfferModel = mData.get(position);
        holder.payment_method_name.setText(todayOfferModel.getMethod());
        holder.payment_method_acc.setText(todayOfferModel.getAcc());

        switch (todayOfferModel.getMethod()){
            case "bKash":
                holder.payment_method_icon.setImageResource(R.drawable.ic_bkash);
                break;
            case "Nagad":
                holder.payment_method_icon.setImageResource(R.drawable.nogod);
                break;
            case "Rocket":
                holder.payment_method_icon.setImageResource(R.drawable.roket);
                break;
            case "Bank Account":
                holder.payment_method_icon.setImageResource(R.drawable.bank);
                break;
                case "Upay":
                    holder.payment_method_icon.setImageResource(R.drawable.upay);
                break;
        }


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
        public ImageView payment_method_icon;
        public TextView payment_method_name,payment_method_acc;


        public ViewHolder(View itemView) {
            super(itemView);
            this.payment_method_icon = itemView.findViewById(R.id.payment_method_icon);
            this.payment_method_name = itemView.findViewById(R.id.payment_method_name);
            this.payment_method_acc = itemView.findViewById(R.id.payment_method_acc);
        }

    }
    public void setOnBlockListener(OnBlockListener blockListener) {
        this.blockListener = blockListener;
    }

}