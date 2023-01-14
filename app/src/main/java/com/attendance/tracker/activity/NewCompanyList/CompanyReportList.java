package com.attendance.tracker.activity.NewCompanyList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyReportList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Company")
    @Expose
    private String company;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Mobile1")
    @Expose
    private String mobile1;
    @SerializedName("Mobile2")
    @Expose
    private String mobile2;
    @SerializedName("Employees")
    @Expose
    private String employees;
    @SerializedName("Monthly_Charge")
    @Expose
    private String monthlyCharge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getEmployees() {
        return employees;
    }

    public void setEmployees(String employees) {
        this.employees = employees;
    }

    public String getMonthlyCharge() {
        return monthlyCharge;
    }

    public void setMonthlyCharge(String monthlyCharge) {
        this.monthlyCharge = monthlyCharge;
    }
}
