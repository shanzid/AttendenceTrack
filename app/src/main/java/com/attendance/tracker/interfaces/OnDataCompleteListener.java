package com.attendance.tracker.interfaces;

import com.attendance.tracker.activity.user.MyLatLong;

import java.util.List;

public interface OnDataCompleteListener {
    void LoadLocationSuccess(List<MyLatLong> area);
    void LoadLocationFail(String message);

}
