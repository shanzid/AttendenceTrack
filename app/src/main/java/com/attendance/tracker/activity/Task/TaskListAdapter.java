package com.attendance.tracker.activity.Task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.attendance.tracker.R;
import com.attendance.tracker.data.TaskData;
import com.attendance.tracker.interfaces.TaskListener;

import java.util.ArrayList;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TaskData> mData;
    private TaskListener taskListener;

    public TaskListAdapter(Context mContext, ArrayList<TaskData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(mData.get(position).getTitle());
        holder.date.setText(mData.get(position).getTime());
        holder.user.setText("Employee: " +mData.get(position).getUser());
        holder.number.setText("Mobile: " +mData.get(position).getUserMobile());
        holder.status.setText("Status: " +mData.get(position).getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, TaskDetailsActivity.class);
                mIntent.putExtra("TaskID", mData.get(position).getTaskId());
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(mIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView title, date,number,user,status;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            date = itemView.findViewById(R.id.tv_time);
            number = itemView.findViewById(R.id.tv_Phone);
            user = itemView.findViewById(R.id.tv_userName);
            status = itemView.findViewById(R.id.tv_Status);


        }
    }
    public void setTaskItemClick(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

}
