package com.rhys.logol.revdupsample.main_page.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rhys.logol.revdupsample.R;
import com.rhys.logol.revdupsample.login_page.LoginActivity;

public class VinFragment extends Fragment implements View.OnClickListener {

    Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vin_fragment, container, false);
        logout = (Button) v.findViewById(R.id.logout);

        logout.setOnClickListener(this);
        return v;
    }

    public static VinFragment newInstance(String text) {
        VinFragment f = new VinFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onClick(View view) {
        if(view == logout){
            try {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
