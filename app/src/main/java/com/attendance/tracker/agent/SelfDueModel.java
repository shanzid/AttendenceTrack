package com.attendance.tracker.agent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelfDueModel {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("due_list")
    @Expose
    private List<SelfDueList> dueList = null;

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

    public List<SelfDueList> getDueList() {
        return dueList;
    }

    public void setDueList(List<SelfDueList> dueList) {
        this.dueList = dueList;
    }
}
