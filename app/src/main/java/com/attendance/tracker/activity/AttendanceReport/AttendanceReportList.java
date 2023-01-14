package com.attendance.tracker.activity.AttendanceReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttendanceReportList {
    @SerializedName("Day")
    @Expose
    private String day;
    @SerializedName("Week")
    @Expose
    private String week;
    @SerializedName("Month")
    @Expose
    private String month;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("Total_Employee")
    @Expose
    private String totalEmployee;
    @SerializedName("Present")
    @Expose
    private String present;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTotalEmployee() {
        return totalEmployee;
    }

    public void setTotalEmployee(String totalEmployee) {
        this.totalEmployee = totalEmployee;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }
}
