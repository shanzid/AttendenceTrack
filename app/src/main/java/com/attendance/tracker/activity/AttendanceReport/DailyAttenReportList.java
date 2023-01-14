package com.attendance.tracker.activity.AttendanceReport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyAttenReportList {
    @SerializedName("Employee_id")
    @Expose
    private String employeeId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Mobile")
    @Expose
    private String mobile;
    @SerializedName("Ins")
    @Expose
    private String ins;
    @SerializedName("Out")
    @Expose
    private String out;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIns() {
        return ins;
    }

    public void setIns(String ins) {
        this.ins = ins;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
