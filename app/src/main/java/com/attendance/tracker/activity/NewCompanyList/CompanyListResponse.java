package com.attendance.tracker.activity.NewCompanyList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CompanyListResponse {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("total_comapny")
    @Expose
    private Integer totalComapny;
    @SerializedName("report")
    @Expose
    private ArrayList<CompanyReportList> report = null;

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

    public ArrayList<CompanyReportList> getReport() {
        return report;
    }

    public void setReport(ArrayList<CompanyReportList> report) {
        this.report = report;
    }
}
