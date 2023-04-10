package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by orapc7 on 6/9/2017.
 */

public class CustomerSpinnerTesting extends BaseAdapter {

    Context context;
    Button btn_over;
    ArrayList<HashMap<String, String>> statusarraylist;
    protected ArrayList<CharSequence> selectedColours = new ArrayList<CharSequence>();

    public CustomerSpinnerTesting(Context context, ArrayList<HashMap<String, String>> status, Button btn_over) {
        this.statusarraylist = status;
        this.context = context;
        this.btn_over = btn_over;
    }

    @Override
    public int getCount() {
        return statusarraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean[] checkedColours = new boolean[statusarraylist.size()];

        int count = statusarraylist.size();

        for (int i = 0; i < count; i++)
            checkedColours[i] = selectedColours.contains(statusarraylist.get(position).get("reason"));

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked)

                    selectedColours.add(statusarraylist.get(which).get("reason"));

                else

                    selectedColours.remove(statusarraylist.get(which).get("reason"));

                onChangeSelectedColours();

            }

        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Select Colours");

        builder.setMultiChoiceItems(null, checkedColours, coloursDialogListener);

        AlertDialog dialog = builder.create();

        dialog.show();


        return null;
    }
    protected void onChangeSelectedColours() {

        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence colour : selectedColours)

            stringBuilder.append(colour + ",");

        btn_over.setText(stringBuilder.toString());

    }
}







