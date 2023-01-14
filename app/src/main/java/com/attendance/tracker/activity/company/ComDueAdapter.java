package com.attendance.tracker.activity.company;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.attendance.tracker.activity.AttendanceReport.Fragment_daily;
import com.attendance.tracker.activity.AttendanceReport.Fragment_monthly;
import com.attendance.tracker.activity.AttendanceReport.Fragment_weekly;
import com.attendance.tracker.activity.company.fragment.DueFragment;
import com.attendance.tracker.activity.company.fragment.PaidFragment;

public class ComDueAdapter extends FragmentStatePagerAdapter {

    public ComDueAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new DueFragment();
        } else if (position == 1) {
            fragment[0] = new PaidFragment();
        }

        return fragment[0];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Due";

        } else if (position == 1) {
            return "Paid";
        }
        return null;
    }

}
