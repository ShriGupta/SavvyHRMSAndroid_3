package com.savvy.hrmsnewapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.savvy.hrmsnewapp.fragment.ODRequestFragment;
import com.savvy.hrmsnewapp.fragment.OTRequestFragment;

import java.util.ArrayList;

/**
 * Created by orapc7 on 5/11/2017.
 */

public class OdFragmentAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    int tabCount;

    public void addFragment(Fragment fragment , String Title)
    {
        this.fragments.add(fragment);
        this.tabTitles.add(Title);
    }

//    public OdFragmentAdapter(FragmentManager fm) {
//        super(fm);
//    }

    public OdFragmentAdapter(FragmentManager fm,int tabCount)
    {
        super(fm);
        this.tabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
                ODRequestFragment tab1 = new ODRequestFragment();
                return tab1;

            case 1:
                OTRequestFragment tab2 = new OTRequestFragment();
                return  tab2;

            default:
                return null;
        }


        //return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabCount;
        //return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String Title = "";
        switch (position)
        {
            case 0:
                Title = "OD Request";
                return Title;

            case 1:
                Title = "OD Status";
                return Title;
            default:
                return null;
        }

        //return tabTitles.get(position);
    }
}
