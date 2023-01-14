package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesReport {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("Direct_Sale")
    @Expose
    private String directSale;
    @SerializedName("Renew_Sale")
    @Expose
    private String renewSale;
    @SerializedName("report")
    @Expose
    private List<SalesReportList> report = null;

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

    public String getDirectSale() {
        return directSale;
    }

    public void setDirectSale(String directSale) {
        this.directSale = directSale;
    }

    public String getRenewSale() {
        return renewSale;
    }

    public void setRenewSale(String renewSale) {
        this.renewSale = renewSale;
    }

    public List<SalesReportList> getReport() {
        return report;
    }

    public void setReport(List<SalesReportList> report) {
        this.report = report;
    }
}
