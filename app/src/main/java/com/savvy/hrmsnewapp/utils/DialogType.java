package com.savvy.hrmsnewapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.DialogAdapterList;

/**
 * Created by orapc7 on 6/28/2017.
 */

public class DialogType {
    Context context;
    RecyclerView recyclerView;
    DialogAdapterList madapter;
    Spinner spin;

    Button btn_cancel;
    String[] str = new String[]{"Please Select", "Two Wheeler", "Three Wheeler", "Four Wheeler", "Bus"};

    public DialogType(Context context) {
        this.context = context;
    }

    public void addConveyanceDialog() {
        final Dialog dialog = new Dialog(context);
        String PullBackTitle = "<font color=\"#b52f2f\"><bold>" + "Add Conveyance" + "</bold></font>";
        dialog.setTitle(Html.fromHtml(PullBackTitle));
        dialog.setContentView(R.layout.add_convayance_row);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        btn_cancel = dialog.findViewById(R.id.btn_cancel);

        spin = dialog.findViewById(R.id.add_conveyance_spin);
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.spinner_item, str);
        spin.setAdapter(arrayAdapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.closeOptionsMenu();
                dialog.cancel();
            }
        });


        dialog.show();
    }

    public void myDialog(String token) {

        String PullBackTitle = "<font color=\"#b52f2f\"><bold>" + "Details of Token. No" + "</bold></font>";

        Dialog dialog = new Dialog(context);
        dialog.setTitle(Html.fromHtml(PullBackTitle) + ": " + token);

        dialog.setContentView(R.layout.request_details_row);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        try {
            recyclerView = dialog.findViewById(R.id.popup_recycler);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            madapter = new DialogAdapterList(context);
            recyclerView.setAdapter(madapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        dialog.show();

    }

    public void cameraDialog() {
        final View v = LayoutInflater.from(context).inflate(R.layout.camera, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        builder.show();

    }

    public SpannableStringBuilder Validation(String str) {
        SpannableStringBuilder builder = null;
        String str34 = str;
        try {
            Log.d("String valid", str);
            builder = new SpannableStringBuilder();

            String str1 = "  *";
            SpannableString redSpannable = new SpannableString(str1);
            redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#df1a42")), 0, str1.length(), 0);
            builder.append(redSpannable);

            SpannableString blackSpannable = new SpannableString(str34);
            redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#303030")), 0, str34.length(), 0);
            builder.append(blackSpannable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }

}
