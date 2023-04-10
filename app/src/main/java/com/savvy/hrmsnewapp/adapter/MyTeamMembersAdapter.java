package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 20/11/2017.
 */

public class MyTeamMembersAdapter extends RecyclerView.Adapter<MyTeamMembersAdapter.MyViewHolder> {

    Context context;
    List<HashMap<String,String>> arlData;
    String CONSTANT_IP_ADDRESS_PHOTO_CODE = "";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences sharedpreferencesIP;
    SharedPreferences.Editor editor;

    public MyTeamMembersAdapter(Context context, List<HashMap<String,String>> arlData){
        this.arlData = arlData;
        this.context = context;

//        sharedpreferencesIP = context.getSharedPreferences("IP_ADDRESS_CONSTANT",MODE_PRIVATE);
//        Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS","");
//        Constants.IP_ADDRESS_STATUS = sharedpreferencesIP.getBoolean("IP_ADDRESS_STATUS",false);
//        CONSTANT_IP_ADDRESS_PHOTO_CODE = sharedpreferencesIP.getString("IP_ADDRESS_PHOTO_CODE","");

    }

    @Override
    public MyTeamMembersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_member_detail_row,parent,false);
        return new MyTeamMembersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTeamMembersAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=arlData.get(position);

//        holder.txt_empName_1.setText(mapdata.get("EMPLOYEE_NAME"));
//        holder.txt_Detail_1.setText(mapdata.get("EMPLOYEE_CODE"));
//        holder.txt_margin_1.setText(mapdata.get("D_DESIGNATION"));

        holder.txt_empName_1.setText("Pankaj Vinay Ojha");
        holder.txt_Detail_1.setText("Software Engineer");
        holder.txt_margin_1.setText("ORA0003");

        String empCode1 = mapdata.get("EMPLOYEE_CODE");
        String photourl = "http://savvyhrms.com/savvyhrms/Images/EmployeePhoto/ORA0003.jpg";

//        if(Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
//            photourl = "http://"+CONSTANT_IP_ADDRESS_PHOTO_CODE+"/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
//        } else{
//            photourl = "http://"+Constants.PHOTO_CODE_IP_ADDRESS+"/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
//        }
        Picasso.get().load(photourl).into(holder.img_member_1);

    }

    @Override
    public int getItemCount() {
        Toast.makeText(context,""+arlData.size(), Toast.LENGTH_LONG).show();
//        return arlData.size();
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_member_1,img_member_2;
        CustomTextView txt_empName_1, txt_Detail_1, txt_margin_1;
        CustomTextView txt_empName_2, txt_Detail_2, txt_margin_2;
        public MyViewHolder(View view) {
            super(view);

            img_member_1 = view.findViewById(R.id.img_team_member_1);
            img_member_2 = view.findViewById(R.id.img_team_member_2);

            txt_empName_1 = view.findViewById(R.id.txt_EmployeeName_1);
            txt_empName_2 = view.findViewById(R.id.txt_EmployeeName_2);
            txt_Detail_1 = view.findViewById(R.id.txt_detail_1);
            txt_Detail_2 = view.findViewById(R.id.txt_detail_2);
            txt_margin_1 = view.findViewById(R.id.txt_marginDetail_1);
            txt_margin_2 = view.findViewById(R.id.txt_marginDetail_2);
        }
    }
}
