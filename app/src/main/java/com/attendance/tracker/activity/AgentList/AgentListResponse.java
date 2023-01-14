package com.attendance.tracker.activity.AgentList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AgentListResponse {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("total_agent")
    @Expose
    private Integer totalAgent;
    @SerializedName("report")
    @Expose
    private ArrayList<AgentListReport> report = null;

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

    public Integer getTotalAgent() {
        return totalAgent;
    }

    public void setTotalAgent(Integer totalAgent) {
        this.totalAgent = totalAgent;
    }

    public ArrayList<AgentListReport> getReport() {
        return report;
    }

    public void setReport(ArrayList<AgentListReport> report) {
        this.report = report;
    }

}
