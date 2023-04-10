package com.savvy.hrmsnewapp.saleForce;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;

public class OutCome_ExpenseFragment extends BaseFragment implements View.OnClickListener{

    CustomTextView out_exp_Outcome, out_exp_Expense, out_exp_activity, out_exp_contact, out_exp_notes;
    LinearLayout linearActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out_come__expense,container,false);

        out_exp_Outcome = view.findViewById(R.id.out_exp_Outcome);
        out_exp_Expense = view.findViewById(R.id.out_exp_Expense);
        out_exp_activity = view.findViewById(R.id.out_exp_activity);
        out_exp_contact = view.findViewById(R.id.out_exp_contact);
        out_exp_notes = view.findViewById(R.id.out_exp_notes);

        linearActivity = view.findViewById(R.id.linear_activity_out);

        out_exp_Outcome.setOnClickListener(this);
        out_exp_Expense.setOnClickListener(this);
        out_exp_activity.setOnClickListener(this);
        out_exp_contact.setOnClickListener(this);
        out_exp_notes.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
       switch(v.getId()){
           case R.id.out_exp_Outcome:
               out_exp_Outcome.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
               out_exp_Outcome.setTextColor(getResources().getColor(R.color.white));

               out_exp_Expense.setBackgroundColor(getResources().getColor(R.color.white));
               out_exp_Expense.setTextColor(getResources().getColor(R.color.black));

               linearActivity.setVisibility(View.GONE);
               OutComeFragment outComeFragment = new OutComeFragment();
               FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
               fragmentTransaction.replace(R.id.out_exp_container, outComeFragment);
               fragmentTransaction.commit();
               break;

           case R.id.out_exp_Expense:
               out_exp_Expense.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
               out_exp_Expense.setTextColor(getResources().getColor(R.color.white));

               out_exp_Outcome.setBackgroundColor(getResources().getColor(R.color.white));
               out_exp_Outcome.setTextColor(getResources().getColor(R.color.black));

               linearActivity.setVisibility(View.GONE);
               ExpenseFragment expenseFragment = new ExpenseFragment();
               FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
               fragmentTransaction1.replace(R.id.out_exp_container, expenseFragment);
               fragmentTransaction1.commit();
               break;

           case R.id.out_exp_activity:
               linearActivity.setVisibility(View.VISIBLE);
               OutExp_ActivityFragment outExp_activityFragment = new OutExp_ActivityFragment();
               FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
               fragmentTransaction2.replace(R.id.out_exp_container, outExp_activityFragment);
               fragmentTransaction2.commit();
               break;

           case R.id.out_exp_contact:
               linearActivity.setVisibility(View.VISIBLE);
               OutExpContactFragment outExpContactFragment = new OutExpContactFragment();
               FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
               fragmentTransaction3.replace(R.id.out_exp_container, outExpContactFragment);
               fragmentTransaction3.commit();
               break;

           case R.id.out_exp_notes:
               linearActivity.setVisibility(View.VISIBLE);
               OutExpNotesFragment outExpNotesFragment = new OutExpNotesFragment();
               FragmentTransaction fragmentTransaction4 = fragmentManager.beginTransaction();
               fragmentTransaction4.replace(R.id.out_exp_container, outExpNotesFragment);
               fragmentTransaction4.commit();
               break;

           default:
               break;
       }
    }
}
