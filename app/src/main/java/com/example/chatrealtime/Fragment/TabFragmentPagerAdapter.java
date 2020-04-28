package com.example.chatrealtime.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.database.annotations.Nullable;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    String[]judul= new String[]{"Chat","Groups","Contact","Request"};

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                Fragment fragmentchat = new ChatFragment();
                return fragmentchat;
            case 1:
                Fragment fragmentgroup = new GroupFragment();
                return fragmentgroup;
            case 2:
                Fragment fragmentcontact = new ContactFragment();
                return fragmentcontact;
            case 3:
                Fragment fragmentrequest = new RequestFragment();
                return fragmentrequest;
        }
        return null;
    }

    @Override
    public int getCount() {
        return judul.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return judul[position];
    }
}
