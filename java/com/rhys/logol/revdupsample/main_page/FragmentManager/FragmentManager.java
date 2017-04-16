package com.rhys.logol.revdupsample.main_page.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.rhys.logol.revdupsample.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentManager extends AppCompatActivity {
    PageAdapter pageAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager);
        List<Fragment> fragments = getFragments();
        pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(1);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();

        fList.add(FragmentActivity.newInstance("ShowImagesFragment"));
        fList.add(FragmentActivity.newInstance("VinFragment"));
        return fList;
    }
}