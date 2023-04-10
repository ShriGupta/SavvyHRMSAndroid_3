package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.customwidgets.CircularImageView;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by orapc7 on 20/11/2017.
 */

public class TeamMembersDemoAdapter extends RecyclerView.Adapter<TeamMembersDemoAdapter.MyViewHolder>{

    List<HashMap<String,String>> data ;
    private LayoutInflater inflater;
    private Context context;
    SharedPreferences shared;

    public TeamMembersDemoAdapter(Context context,List<HashMap<String, String>> data) {
        this.context    = context;
        this.data       = data;
    }


    @Override
    public TeamMembersDemoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_member_detail_row, parent, false);
        return new TeamMembersDemoAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(TeamMembersDemoAdapter.MyViewHolder holder, int position) {
        HashMap<String,String> mapdata=data.get(position);

        holder.txt_empName_1.setText(mapdata.get("employeeName"));
        holder.txt_Detail_1.setText(mapdata.get("employeeCode"));
        holder.txt_margin_1.setText(mapdata.get("designation"));
        holder.txt_department.setText(mapdata.get("department"));

        String dep = mapdata.get("depth");
        if(!dep.equals("0")){
            holder.txtDepth.setVisibility(View.VISIBLE);
            holder.txtDepth.setText(mapdata.get("depth")+"+");
        } else{
            holder.txtDepth.setText("0");
            holder.txtDepth.setVisibility(View.INVISIBLE);
        }

//        holder.txt_empName_1.setText(mapdata.get("EMPLOYEE_NAME"));
//        holder.txt_Detail_1.setText(mapdata.get("EMPLOYEE_CODE"));
//        holder.txt_margin_1.setText(mapdata.get("D_DESIGNATION"));

//        holder.txt_empName_1.setText("Pankaj Vinay Ojha");
//        holder.txt_Detail_1.setText("Software Engineer");
//        holder.txt_margin_1.setText("ORA0003");

        String photourl = mapdata.get("photoCode");

//        if(Constants.COMPANY_STATUS_PHOTO_CODE.equals("GENERAL")) {
//            photourl = "http://"+""+"/savvyhrms/Images/EmployeePhoto/" + empCode1 + ".jpg";
//        } else{
//            photourl = "http://"+Constants.PHOTO_CODE_IP_ADDRESS+"/savvyhrms/Images/EmployeePhoto/" + "ora0004" + ".jpg";
//        }

//        if(position%2==0)
//            photourl = "http://"+Constants.PHOTO_CODE_IP_ADDRESS+"/savvyhrms/Images/EmployeePhoto/" + "ora0003" + ".jpg";
//        else
//            photourl = "http://"+Constants.PHOTO_CODE_IP_ADDRESS+"/savvyhrms/Images/EmployeePhoto/" + "ora0004" + ".jpg";
        Picasso.get().load(photourl).into(holder.img_member_1);
    }

    @Override
    public int getItemCount() {
        return data.size();
//        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularImageView img_member_1,img_member_2;
        CustomTextView txt_empName_1, txt_Detail_1, txt_margin_1,txt_department, txtDepth;
        CustomTextView txt_empName_2, txt_Detail_2, txt_margin_2;

        public MyViewHolder(View view) {
            super(view);

            img_member_1 = view.findViewById(R.id.img_team_member_1);
//            img_member_2 = (ImageView)view.findViewById(R.id.img_team_member_2);

            txt_empName_1 = view.findViewById(R.id.txt_EmployeeName_1);
//            txt_empName_2 = (CustomTextView)view.findViewById(R.id.txt_EmployeeName_2);
            txt_Detail_1 = view.findViewById(R.id.txt_detail_1);
//            txt_Detail_2 = (CustomTextView)view.findViewById(R.id.txt_detail_2);
            txt_margin_1 = view.findViewById(R.id.txt_marginDetail_1);
            txt_department = view.findViewById(R.id.txt_Department);
            txtDepth = view.findViewById(R.id.txtDepth);
//            txt_margin_2 = (CustomTextView)view.findViewById(R.id.txt_marginDetail_2);

            txtDepth.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}