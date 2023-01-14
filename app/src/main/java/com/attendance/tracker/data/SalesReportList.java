package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalesReportList {
    @SerializedName("Day")
    @Expose
    private String day;
    @SerializedName("Direct_Sale")
    @Expose
    private String directSale;
    @SerializedName("Renew")
    @Expose
    private String renew;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
