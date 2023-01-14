package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommissionModel {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("total_comapny")
    @Expose
    private Integer totalComapny;
    @SerializedName("total_commission")
    @Expose
    private String totalCommission;
    @SerializedName("report")
    @Expose
    private List<CommisionList> report = null;

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

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }

    public List<CommisionList> getReport() {
        return report;
    }

    public void setReport(List<CommisionList> report) {
        this.report = report;
    }

}
