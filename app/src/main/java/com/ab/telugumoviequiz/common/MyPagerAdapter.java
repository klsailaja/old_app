package com.ab.telugumoviequiz.common;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private final String teluguHelpStr;
    private final String englishHelpStr;

    public MyPagerAdapter(FragmentManager fragmentManager, String teluguHelpStr, String englishHelpStr) {
        super(fragmentManager);
        this.teluguHelpStr = teluguHelpStr;
        this.englishHelpStr = englishHelpStr;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return 2;
    }

    // Returns the fragment to display for that page
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FirstFragment(teluguHelpStr);
            case 1:
                return new FirstFragment(englishHelpStr);
            default:
                return new FirstFragment(teluguHelpStr);
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Telugu";
        }
        return "English";
    }
}