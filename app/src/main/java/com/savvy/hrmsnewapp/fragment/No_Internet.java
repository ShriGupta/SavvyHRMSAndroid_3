package com.savvy.hrmsnewapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.DashBoardActivity;

public class No_Internet extends Fragment {
    Button btn_no;
    private FragmentDrawer drawerFragment;
    View view;
    private Toolbar mToolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_no__internet, container, false);

        btn_no = view.findViewById(R.id.btn_no_connection);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                            startActivity(intent);

                        }
                    }, 1000 * 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
