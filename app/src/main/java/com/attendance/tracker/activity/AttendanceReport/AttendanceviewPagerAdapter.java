package com.attendance.tracker.activity.AttendanceReport;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AttendanceviewPagerAdapter extends FragmentStatePagerAdapter {

    public AttendanceviewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new Fragment_daily();
        } else if (position == 1) {
            fragment[0] = new Fragment_weekly();
        }
        else if (position == 2) {
            fragment[0] = new Fragment_monthly();

        }
        return fragment[0];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Daily";

        } else if (position == 1) {
            return "Weekly";
        }
        else if (position == 2) {
            return "Monthly";
        }
        return null;
    }

}