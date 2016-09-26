package com.marduc812.workcalculator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by marduc on 28/05/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    MainActivity mainActivity;


    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> Tabtitles = new ArrayList<>();

    public void addFragments(Fragment fragments, String titles)
    {
        this.fragments.add(fragments);
        this.Tabtitles.add(titles);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Tabtitles.get(position);
        //return null; // An 8elw mono eikones gyrizw null
    }
}
