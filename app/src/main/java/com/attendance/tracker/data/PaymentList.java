package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentList {
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Method")
    @Expose
    private String method;
    @SerializedName("Charge")
    @Expose
    private String charge;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("accname")
    @Expose
    private String accname;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("acc")
    @Expose
    private String acc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

}
