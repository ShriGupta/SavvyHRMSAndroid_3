package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

public class OPERequestFicciAdapter extends RecyclerView.Adapter<OPERequestFicciAdapter.MyViewHolder> {

    Context context;
    CustomArraySpinnerAdapter customArraySpinnerAdapter;
    CoordinatorLayout coordinatorLayout;
    ArrayList<HashMap<String, String>> arrayListData;
    ArrayList<HashMap<String, String>> spinnerDataList;
    String[] departmentNameArray;
    String[] departmentIDArray;
    StringBuilder stringBuilder;
    HashMap<Integer, Integer> departmentIdMap = new HashMap<>();
    HashMap<Integer, String> commentsMap = new HashMap<>();
    TreeMap<Integer, Boolean> checkboxStatusMap = new TreeMap<>();
    HashMap<Integer, String> dataMap = new HashMap<>();

    public OPERequestFicciAdapter(Context context, CoordinatorLayout coordinatorLayout, ArrayList<HashMap<String, String>> arrayListData, ArrayList<HashMap<String, String>> spinnerDataList, String[] departmentNameArray, String[] departmentIDArray) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.arrayListData = arrayListData;
        this.spinnerDataList = spinnerDataList;
        this.departmentNameArray = departmentNameArray;
        this.departmentIDArray = departmentIDArray;
        customArraySpinnerAdapter = new CustomArraySpinnerAdapter(context, Arrays.asList(departmentNameArray));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ope_requestficci_row, parent, false));
    }

    @Override
    public void onBindViewHolder(OPERequestFicciAdapter.MyViewHolder holder, int position) {
        HashMap<String, String> map = arrayListData.get(position);
        holder.serialNumber.setText(String.valueOf(position + 1));
        holder.workingStatus.setText(map.get("WORKINGSTATUS"));
        holder.attandanceDate.setText(map.get("EAR_ATTENDANCE_DATE"));
        holder.inTime.setText(map.get("EAR_TIME_IN"));
        holder.outTime.setText(map.get("EAR_TIME_OUT"));
        holder.ammounts.setText(map.get("Amount"));
        holder.opeHours.setText(map.get("OPE_HOURS"));
        holder.empComments.setText("");
        customArraySpinnerAdapter = new CustomArraySpinnerAdapter(context, Arrays.asList(departmentNameArray));
        holder.departmentSpinner.setAdapter(customArraySpinnerAdapter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText empComments;
        CheckBox opeCheckBox1;
        Spinner departmentSpinner;
        CustomTextView workingStatus, attandanceDate, inTime, outTime, opeHours, ammounts, serialNumber;

        public MyViewHolder(View view) {
            super(view);
            empComments = view.findViewById(R.id.empComments);
            opeCheckBox1 = view.findViewById(R.id.opeCheckbox1);
            departmentSpinner = view.findViewById(R.id.departmentSpinner);
            workingStatus = view.findViewById(R.id.workingStatus);
            attandanceDate = view.findViewById(R.id.attandanceDate);
            inTime = view.findViewById(R.id.inTime);
            outTime = view.findViewById(R.id.outTime);
            opeHours = view.findViewById(R.id.opeHours);
            ammounts = view.findViewById(R.id.ammounts);
            serialNumber = view.findViewById(R.id.serialNumber);

            opeCheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (opeCheckBox1.isChecked()) {
                        checkboxStatusMap.put(getAdapterPosition(), isChecked);
                        xmlDataFormat(
                                attandanceDate.getText().toString(),
                                inTime.getText().toString(),
                                outTime.getText().toString(),
                                opeHours.getText().toString(),
                                ammounts.getText().toString(),
                                getAdapterPosition()
                        );
                    } else {
                        checkboxStatusMap.remove(getAdapterPosition());
                        dataMap.remove(getAdapterPosition());
                    }
                }
            });
            departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    departmentIdMap.put(getAdapterPosition(), position);
                    if (position > 0) {
                        departmentIdMap.replace(getAdapterPosition(), position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            empComments.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void afterTextChanged(Editable s) {

                    if (!commentsMap.containsKey(getAdapterPosition())) {
                        commentsMap.put(getAdapterPosition(), s.toString());
                    } else {
                        commentsMap.replace(getAdapterPosition(), s.toString());
                    }
                }
            });
        }
    }


    public void xmlDataFormat(String attandanceDate, String inTime, String outTime, String opeHours, String ammounts, int adapterPosition) {
        dataMap.put(adapterPosition, "@" + attandanceDate + "," + inTime + "," + outTime + "," + opeHours + "," + ammounts + ",");
    }

    public String getxmlData() {
        stringBuilder = new StringBuilder();
        String result = "";
        String id = "";

        if (checkboxStatusMap.size() == 0) {
            return "NoCheckBox Selected";
        } else if (checkboxStatusMap.size() > 0) {
            for (int adapterKey : checkboxStatusMap.keySet()) {
                if (departmentIdMap.get(adapterKey) != null && !(departmentIdMap.get(adapterKey) == 0)) {
                    if (!commentsMap.get(adapterKey).equals("")) {
                        int position = departmentIdMap.get(adapterKey);
                        id = departmentIDArray[position - 1];
                        stringBuilder.append(dataMap.get(adapterKey));
                        stringBuilder.append(id + ",");
                        stringBuilder.append(commentsMap.get(adapterKey));
                    } else {
                        return "commentbox";
                    }
                } else {
                    return "dropdown";
                }
                if (commentsMap.get(adapterKey) == null) {
                    return "emptybox";
                }
            }
            return stringBuilder.toString();
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }
}
