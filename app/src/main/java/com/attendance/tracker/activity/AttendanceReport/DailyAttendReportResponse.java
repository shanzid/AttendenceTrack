package com.attendance.tracker.activity.AttendanceReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DailyAttendReportResponse {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("report")
    @Expose
    private ArrayList<DailyAttenReportList> report = null;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<DailyAttenReportList> getReport() {
        return report;
    }

    public void setReport(ArrayList<DailyAttenReportList> report) {
        this.report = report;
    }
}
