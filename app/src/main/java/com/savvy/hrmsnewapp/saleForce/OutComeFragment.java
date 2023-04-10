package com.savvy.hrmsnewapp.saleForce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.BaseFragment;

public class OutComeFragment extends BaseFragment {

    TextView outComeText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out_come,container,false);

//        outComeText = (TextView)view.findViewById(R.id.outComeTxt);
//
//        outComeText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                OutCome_ExpenseFragment outCome_expenseFragment = new OutCome_ExpenseFragment();
//                FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
//                fragmentTransaction2.replace(R.id.container_body, outCome_expenseFragment);
//                fragmentTransaction2.commit();
//            }
//        });
        return view;
    }
}
