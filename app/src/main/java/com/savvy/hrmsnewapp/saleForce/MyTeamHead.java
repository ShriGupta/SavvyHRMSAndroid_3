package com.savvy.hrmsnewapp.saleForce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class MyTeamHead extends Fragment {
    CustomTextView MyTeamHeadProfile, NextVisit,MyTeamHead_Call, MyTeamHead_Message;
    CircularImageView MyTeamHeadImage;
    SharedPreferences shared;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String EmployeeName = "", PhotoCode = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        EmployeeName = shared.getString("EmpoyeeName","");
        PhotoCode = shared.getString("EmpPhotoPath","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_team_head,container,false);

        MyTeamHeadProfile = view.findViewById(R.id.MyTeamHeadProfile);
        NextVisit = view.findViewById(R.id.NextVisit);
        MyTeamHead_Call = view.findViewById(R.id.MyTeamHead_Call);
        MyTeamHead_Message = view.findViewById(R.id.MyTeamHead_Message);

        MyTeamHeadImage = view.findViewById(R.id.MyTeamHeadImage);

        Picasso.get().load(PhotoCode).into(MyTeamHeadImage);
        MyTeamHeadProfile.setText(""+EmployeeName);

        NextVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleForce_Visit saleForce_visit = new SaleForce_Visit();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, saleForce_visit);
                fragmentTransaction.commit();
            }
        });

        MyTeamHead_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String number = "tel:8968660422";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
//                    intent.putExtra("Tel","gfjsgs");
                    startActivity(intent);
//                    Intent intent = new Intent(Intent.ACTION_CALL);
//                    intent.setData(Uri.parse(number));
//                    startActivity(intent);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        MyTeamHead_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("smsto:9771158029");
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", "The SMS text");
                    startActivity(it);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
