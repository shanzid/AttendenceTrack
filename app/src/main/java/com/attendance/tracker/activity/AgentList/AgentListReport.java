package com.attendance.tracker.activity.AgentList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentListReport {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Mobile1")
    @Expose
    private String mobile1;
    @SerializedName("Mobile2")
    @Expose
    private String mobile2;
    @SerializedName("Company_Count")
    @Expose
    private String companyCount;
    @SerializedName("Sale")
    @Expose
    private String sale;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCompanyCount() {
        return companyCount;
    }

    public void setCompanyCount(String companyCount) {
        this.companyCount = companyCount;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

}
