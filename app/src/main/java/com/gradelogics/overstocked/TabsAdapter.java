package com.gradelogics.overstocked;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                fragment_analysis home = new fragment_analysis();
                return home;
            case 1:
                StockFragment about = new StockFragment();
                return about;
            case 2:
                MainFragment contact = new MainFragment();
                return contact;
            default:
                return null;
        }
    }
}
