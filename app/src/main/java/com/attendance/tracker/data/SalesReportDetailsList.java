package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalesReportDetailsList {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Invoice")
    @Expose
    private String invoice;
    @SerializedName("Company")
    @Expose
    private String company;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Mobile1")
    @Expose
    private String mobile1;
    @SerializedName("Direct_Sale")
    @Expose
    private String directSale;
    @SerializedName("Renew")
    @Expose
    private String renew;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
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

    public String getDirectSale() {
        return directSale;
    }

    public void setDirectSale(String directSale) {
        this.directSale = directSale;
    }

    public String getRenew() {
        return renew;
    }

    public void setRenew(String renew) {
        this.renew = renew;
    }

}
