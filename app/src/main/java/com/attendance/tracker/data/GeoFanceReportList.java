package com.attendance.tracker.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoFanceReportList {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("geo")
    @Expose
    private String geo;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("radius")
    @Expose
    private Double radius;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
