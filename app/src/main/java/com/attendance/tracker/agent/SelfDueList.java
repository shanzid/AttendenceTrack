package com.attendance.tracker.agent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelfDueList {
    @SerializedName("due_id")
    @Expose
    private String dueId;
    @SerializedName("Invoice")
    @Expose
    private String invoice;
    @SerializedName("For_Month")
    @Expose
    private String forMonth;
    @SerializedName("Due")
    @Expose
    private String due;
    @SerializedName("Agent_Name")
    @Expose
    private String agentName;
    @SerializedName("Agent_Mobile")
    @Expose
    private String agentMobile;

    public String getDueId() {
        return dueId;
    }

    public void setDueId(String dueId) {
        this.dueId = dueId;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getForMonth() {
        return forMonth;
    }

    public void setForMonth(String forMonth) {
        this.forMonth = forMonth;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }

}
