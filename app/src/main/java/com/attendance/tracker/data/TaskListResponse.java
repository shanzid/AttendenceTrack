package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskListResponse {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("task_list")
    @Expose
    private ArrayList<TaskData> taskList = null;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(String errorReport) {
        this.errorReport = errorReport;
    }

    public ArrayList<TaskData> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<TaskData> taskList) {
        this.taskList = taskList;
    }
}
