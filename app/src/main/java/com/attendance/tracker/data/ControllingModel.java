package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ControllingModel {
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("error_report")
    @Expose
    private String errorReport;
    @SerializedName("server_status")
    @Expose
    private Integer serverStatus;
    @SerializedName("user_block")
    @Expose
    private Integer userBlock;
    @SerializedName("due_warning_date")
    @Expose
    private Integer dueWarningDate;
    @SerializedName("due_block")
    @Expose
    private Integer dueBlock;

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

    public Integer getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(Integer serverStatus) {
        this.serverStatus = serverStatus;
    }

    public Integer getUserBlock() {
        return userBlock;
    }

    public void setUserBlock(Integer userBlock) {
        this.userBlock = userBlock;
    }

    public Integer getDueWarningDate() {
        return dueWarningDate;
    }

    public void setDueWarningDate(Integer dueWarningDate) {
        this.dueWarningDate = dueWarningDate;
    }

    public Integer getDueBlock() {
        return dueBlock;
    }

    public void setDueBlock(Integer dueBlock) {
        this.dueBlock = dueBlock;
    }
}
