package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DateSearchGeoData {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("search_found")
    @Expose
    private Integer searchFound;
    @SerializedName("report")
    @Expose
    private ArrayList<SearchGeoList> report = null;

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

    public Integer getSearchFound() {
        return searchFound;
    }

    public void setSearchFound(Integer searchFound) {
        this.searchFound = searchFound;
    }

    public ArrayList<SearchGeoList> getReport() {
        return report;
    }

    public void setReport(ArrayList<SearchGeoList> report) {
        this.report = report;
    }
}
