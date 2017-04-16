package com.rhys.logol.revdupsample.main_page.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rhys.logol.revdupsample.R;

public class FragmentActivity extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static Fragment newInstance(String message) {
        FragmentActivity f = new FragmentActivity();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v = inflater.inflate(R.layout.activity_show_images, container, false);
        return v;
    }
}
