package com.savvy.hrmsnewapp.saleForce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.adapter.GetCurrentLocationAdapter;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.fragment.BaseFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class GetCurrentLocation extends BaseFragment{

    CustomTextView No_Visit_Scheduled, getCurrentLocation, VisitLocation_empName;
    CircularImageView img_VisitLocation;
    ImageView generate_location;
    RecyclerView recyclerView_custVisit;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;
    ArrayList<HashMap<String,String>> arldata;

    GetCurrentLocationAdapter getCurrentLocationAdapter;

    TrackGPS trackGPS;
    String EmployeeName = "", PhotoCode = "";
    String SAVE_CUR_TYPE = "", SAVE_CUR_CUST_NAME = "",SAVE_CUR_LAT = "",SAVE_CUR_LOC_IDEN = "",SAVE_CUR_TERROTORY = "",SAVE_CUR_INDUSTRY = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        arldata = new ArrayList<>();
        EmployeeName = shared.getString("EmpoyeeName","");
        PhotoCode = shared.getString("EmpPhotoPath","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_current_location,container,false);

        No_Visit_Scheduled = view.findViewById(R.id.No_Visit_Scheduled);
        getCurrentLocation = view.findViewById(R.id.getCurrentLocation);
        VisitLocation_empName = view.findViewById(R.id.VisitLocation_empName);

        img_VisitLocation = view.findViewById(R.id.img_VisitLocation);
        generate_location = view.findViewById(R.id.generate_location);
        recyclerView_custVisit = view.findViewById(R.id.recyclerView_custVisit);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView_custVisit.setLayoutManager(mLayoutManager);
        recyclerView_custVisit.setItemAnimator(new DefaultItemAnimator());

        recyclerView_custVisit.setVisibility(View.VISIBLE);
        No_Visit_Scheduled.setVisibility(View.GONE);

        getCurrentLocationAdapter = new GetCurrentLocationAdapter(getActivity(),arldata);
        recyclerView_custVisit.setAdapter(getCurrentLocationAdapter);

        Picasso.get().load(PhotoCode).into(img_VisitLocation);
        VisitLocation_empName.setText(""+EmployeeName);

        generate_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveVisitDate();
            }
        });

        return view;
    }

    public void SaveVisitDate(){
        try{
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.save_location_dialog);
            dialog.setTitle("Save Location");
            dialog.setCancelable(false);

            Button btn_Save = dialog.findViewById(R.id.btn_SaveLoc_Save);
            Button btn_Cancel = dialog.findViewById(R.id.btn_SaveLoc_Cancel);

            final Spinner spin_saveLoc_Type = dialog.findViewById(R.id.spin_saveLoc_Type);
            final Spinner spin_saveLoc_Territory = dialog.findViewById(R.id.spin_saveLoc_Territory);
            final Spinner spin_saveLoc_Industry = dialog.findViewById(R.id.spin_saveLoc_Industry);

            final EditText edt_saveLoc_EmpName = dialog.findViewById(R.id.edt_saveLoc_EmpName);
            final CustomTextView edt_saveLoc_LatLong = dialog.findViewById(R.id.edt_saveLoc_LatLong);
            final EditText edt_saveLoc_LocIdentifier = dialog.findViewById(R.id.edt_saveLoc_LocIdentifier);


            final LinearLayout linear_Type = dialog.findViewById(R.id.linear_Type);
            final LinearLayout linear_customerDetail = dialog.findViewById(R.id.linear_customerDetail );
            final LinearLayout linear_LatLong = dialog.findViewById(R.id.linear_LatLong);
            final LinearLayout linear_locIdentifier = dialog.findViewById(R.id.linear_locIdentifier);
            final LinearLayout linear_territory = dialog.findViewById(R.id.linear_territory);
            final LinearLayout linear_industry = dialog.findViewById(R.id.linear_industry);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            String[] array = {"Select Type","New Customer","Existing Customer","Office","Home"};
            String[] array1 = {"Select Territory","Unknown"};
            String[] array2 = {"Select Industry","Unknown"};

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,array);
            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,array1);
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,array2);

            spin_saveLoc_Type.setAdapter(arrayAdapter);
            spin_saveLoc_Territory.setAdapter(arrayAdapter1);
            spin_saveLoc_Industry.setAdapter(arrayAdapter2);

            linear_Type.setVisibility(View.VISIBLE);
            linear_customerDetail.setVisibility(View.GONE);
            linear_LatLong.setVisibility(View.VISIBLE);
            linear_locIdentifier.setVisibility(View.GONE);
            linear_territory.setVisibility(View.VISIBLE);
            linear_industry.setVisibility(View.VISIBLE);

            spin_saveLoc_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==1){

                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.VISIBLE);
                        linear_territory.setVisibility(View.VISIBLE);
                        linear_industry.setVisibility(View.VISIBLE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==2){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.VISIBLE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==3){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.GONE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    } else if(position==4){
                        linear_Type.setVisibility(View.VISIBLE);
                        linear_customerDetail.setVisibility(View.GONE);
                        linear_LatLong.setVisibility(View.VISIBLE);
                        linear_locIdentifier.setVisibility(View.GONE);
                        linear_territory.setVisibility(View.GONE);
                        linear_industry.setVisibility(View.GONE);

                        edt_saveLoc_LatLong.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            try {
                trackGPS = new TrackGPS(getActivity());
                if(trackGPS.displayGpsStatus()) {
                    dialog.show();
                    double latitude = trackGPS.getLatitude();
                    double longitude = trackGPS.getLongitude();

                    Log.e("Location","Lat : "+latitude+" Longitude : "+longitude);

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.UK);
                    List<Address> addresses;

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        String cityName = addresses.get(0).getLocality() + "," + addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getPostalCode();
                        edt_saveLoc_LatLong.setText(cityName);
                    } else {
                        edt_saveLoc_LatLong.setText("Lat : " + latitude + "\nLon : " + longitude);
                    }
                } else{
                    trackGPS.alertbox("Gps Status!!", "Your GPS is: OFF");
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            btn_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String SpinSaveType = spin_saveLoc_Type.getSelectedItem().toString();
                    Log.e("Spin Save Location",SpinSaveType);
                    if(SpinSaveType.toUpperCase().trim().equals("SELECT TYPE")){
                        Toast.makeText(getActivity(),"Please Select Any Type",Toast.LENGTH_LONG).show();
                    } else  if(SpinSaveType.toUpperCase().trim().equals("NEW CUSTOMER")){
                        Log.e("New Customer",SpinSaveType);
                        SAVE_CUR_CUST_NAME = edt_saveLoc_EmpName.getText().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();
                        SAVE_CUR_LOC_IDEN = edt_saveLoc_LocIdentifier.getText().toString();
                        SAVE_CUR_TERROTORY = spin_saveLoc_Territory.getSelectedItem().toString();
                        SAVE_CUR_INDUSTRY = spin_saveLoc_Industry.getSelectedItem().toString();

                        if(SAVE_CUR_CUST_NAME.equals("")){
                            Toast.makeText(getActivity(),"Please Enter Customer Name",Toast.LENGTH_LONG).show();
                        } else if(SAVE_CUR_LOC_IDEN.equals("")){
                            Toast.makeText(getActivity(),"Please Enter Location Identifier",Toast.LENGTH_LONG).show();
                        } else if(SAVE_CUR_TERROTORY.toUpperCase().trim().equals("SELECT TERRITORY")){
                            Toast.makeText(getActivity(),"Please Select Territory",Toast.LENGTH_LONG).show();
                        } else if(SAVE_CUR_INDUSTRY.toUpperCase().trim().equals("SELECT INDUSTRY")){
                            Toast.makeText(getActivity(),"Please Enter Industory",Toast.LENGTH_LONG).show();
                        } else{
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                            hashMap.put("SAVE_CUR_CUST_NAME",SAVE_CUR_CUST_NAME);
                            hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);
                            hashMap.put("SAVE_CUR_LOC_IDEN",SAVE_CUR_LOC_IDEN);
                            hashMap.put("SAVE_CUR_TERROTORY",SAVE_CUR_TERROTORY);
                            hashMap.put("SAVE_CUR_INDUSTRY",SAVE_CUR_INDUSTRY);

                            arldata.add(hashMap);
                            RecyclerDialog(arldata,dialog);
                        }
                    } else if(SpinSaveType.toUpperCase().trim().equals("EXISTING CUSTOMER")){
                        Log.e("Existing Customer",SpinSaveType);
                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_CUST_NAME = edt_saveLoc_EmpName.getText().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        if(SAVE_CUR_CUST_NAME.equals("")){
                            Toast.makeText(getActivity(),"Please Enter Customer Name",Toast.LENGTH_LONG).show();
                        } else{
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                            hashMap.put("SAVE_CUR_CUST_NAME",SAVE_CUR_CUST_NAME);
                            hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);

                            arldata.add(hashMap);
                            RecyclerDialog(arldata,dialog);
                        }

                    }  else if(SpinSaveType.toUpperCase().trim().equals("OFFICE")){
                        Log.e("Office",SpinSaveType);
                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                        hashMap.put("SAVE_CUR_CUST_NAME","-");
                        hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);

                        arldata.add(hashMap);
                        RecyclerDialog(arldata,dialog);

                    }  else if(SpinSaveType.toUpperCase().trim().equals("HOME")){
                        Log.e("Home",SpinSaveType);
                        SAVE_CUR_TYPE = spin_saveLoc_Type.getSelectedItem().toString();
                        SAVE_CUR_LAT = edt_saveLoc_LatLong.getText().toString();

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("SAVE_CUR_TYPE",SAVE_CUR_TYPE);
                        hashMap.put("SAVE_CUR_CUST_NAME","-");
                        hashMap.put("SAVE_CUR_LAT",SAVE_CUR_LAT);

                        arldata.add(hashMap);
                        RecyclerDialog(arldata,dialog);
                    }
                }
            });

            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void RecyclerDialog(final ArrayList<HashMap<String,String>> arldata1, final Dialog dialog12){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Visit Now");
        builder.setCancelable(false);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                dialog1.dismiss();
                dialog12.dismiss();
                getCurrentLocationAdapter = new GetCurrentLocationAdapter(getActivity(),arldata1);
                recyclerView_custVisit.setAdapter(getCurrentLocationAdapter);
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {

                getCurrentLocationAdapter = new GetCurrentLocationAdapter(getActivity(),arldata);
                recyclerView_custVisit.setAdapter(getCurrentLocationAdapter);

                SaleForce_Visit saleForce_visit = new SaleForce_Visit();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, saleForce_visit);
                fragmentTransaction.commit();
                dialog12.dismiss();
                dialog1.dismiss();
            }
        });
        builder.show();

    }
    public void SaveCurrentLocation(Spinner spinner,EditText editText){

        try{
            int pos = spinner.getSelectedItemPosition();
            if(pos==1){
                SAVE_CUR_TYPE = spinner.getSelectedItem().toString();
                SAVE_CUR_CUST_NAME = editText.getText().toString();
                SAVE_CUR_LAT = editText.getText().toString();
                SAVE_CUR_LOC_IDEN = editText.getText().toString();
                SAVE_CUR_TERROTORY = spinner.getSelectedItem().toString();
                SAVE_CUR_INDUSTRY = spinner.getSelectedItem().toString();
            } else if(pos==2){
                SAVE_CUR_TYPE = spinner.getSelectedItem().toString();
                SAVE_CUR_CUST_NAME = editText.getText().toString();
                SAVE_CUR_LAT = editText.getText().toString();
            }  else if(pos==3){
                SAVE_CUR_TYPE = spinner.getSelectedItem().toString();
                SAVE_CUR_LAT = editText.getText().toString();
            }  else if(pos==4){
                SAVE_CUR_TYPE = spinner.getSelectedItem().toString();
                SAVE_CUR_LAT = editText.getText().toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
