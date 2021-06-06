package com.ab.telugumoviequiz.common;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ab.telugumoviequiz.help.HelpFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private final String teluguHelpStr;
    private final String englishHelpStr;
    private final int orientation;
    private final MessageListener listener;
    private final String helpPreferencesKey;

    public MyPagerAdapter(FragmentManager fragmentManager, String teluguHelpStr,
                          String englishHelpStr, int orientation, MessageListener listener,
                          String helpPreferencesKey) {
        super(fragmentManager);
        this.teluguHelpStr = teluguHelpStr;
        this.englishHelpStr = englishHelpStr;
        this.orientation = orientation;
        this.listener = listener;
        this.helpPreferencesKey = helpPreferencesKey;
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
        if (position == 1) {
            return new HelpFragment(englishHelpStr, orientation, listener, helpPreferencesKey);
        }
        return new HelpFragment(teluguHelpStr, orientation, listener, helpPreferencesKey);
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