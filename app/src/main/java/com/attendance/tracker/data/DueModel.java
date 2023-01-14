package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DueModel {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("total_comapny")
    @Expose
    private Integer totalComapny;
    @SerializedName("total_due")
    @Expose
    private String totalDue;
    @SerializedName("report")
    @Expose
    private List<DueList> report = null;

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

    public Integer getTotalComapny() {
        return totalComapny;
    }

    public void setTotalComapny(Integer totalComapny) {
        this.totalComapny = totalComapny;
    }

    public String getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(String totalDue) {
        this.totalDue = totalDue;
    }

    public List<DueList> getReport() {
        return report;
    }

    public void setReport(List<DueList> report) {
        this.report = report;
    }

}
