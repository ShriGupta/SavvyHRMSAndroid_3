package com.savvy.hrmsnewapp.fragment;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

public class ExpenseApprovalHolder extends BaseFragment implements View.OnClickListener {

    LinearLayout ll_expenseRequest,ll_expenseStatus;
    ImageView img_expenseRequest,img_expenseStatus;
    CustomTextView txt_expenseRequest,txt_expenseStatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_approval_holder, container, false);
        ll_expenseRequest = view.findViewById(R.id.expense_linear1);
        ll_expenseStatus = view.findViewById(R.id.expense_linear2);
        img_expenseRequest = view.findViewById(R.id.line_status);
        img_expenseStatus = view.findViewById(R.id.line_status1);

        txt_expenseRequest = view.findViewById(R.id.expense_tab1);
        txt_expenseStatus = view.findViewById(R.id.expense_tab2);

        ll_expenseStatus.setOnClickListener(this);
        ll_expenseRequest.setOnClickListener(this);

        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            ExpenseApprovalFragment expenseApprovalFragment = new ExpenseApprovalFragment();
            transaction.replace(R.id.frame_expense_holder, expenseApprovalFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            txt_expenseStatus.setTextColor(0xFF5082E6);
            txt_expenseRequest.setTextColor(0xFF7B7979);

            img_expenseStatus.setVisibility(View.VISIBLE);
            img_expenseRequest.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;

    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId())
        {
            case R.id.expense_linear1:
                try {
                    ExpenseApprovalVoucherFragment expenseApprovalVoucherFragment = new ExpenseApprovalVoucherFragment();
                    transaction.replace(R.id.frame_expense_holder, expenseApprovalVoucherFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_expenseRequest.setTextColor(0xFF5082E6);
                    txt_expenseStatus.setTextColor(0xFF7B7979);

                    img_expenseRequest.setVisibility(View.VISIBLE);
                    img_expenseStatus.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.expense_linear2:
                try {
                    ExpenseApprovalFragment expenseApprovalFragment = new ExpenseApprovalFragment();
                    transaction.replace(R.id.frame_expense_holder, expenseApprovalFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    txt_expenseStatus.setTextColor(0xFF5082E6);
                    txt_expenseRequest.setTextColor(0xFF7B7979);

                    img_expenseStatus.setVisibility(View.VISIBLE);
                    img_expenseRequest.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
}
