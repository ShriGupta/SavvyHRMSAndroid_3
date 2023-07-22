package com.savvy.hrmsnewapp.fragment.CompanyDirectory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.fragment.CompanyDirectory.model.MyHierarchyBasedOnRolePostResult;
import com.savvy.hrmsnewapp.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CompanyDirectryAdapter extends RecyclerView.Adapter<CompanyDirectryAdapter.MyViewHolder> {
    List<MyHierarchyBasedOnRolePostResult> list;
    Context context;

    ItemClickListener itemClickListener;
    public CompanyDirectryAdapter(List<MyHierarchyBasedOnRolePostResult> list, Context context, ItemClickListener itemClickListener){
        this.list=list;
        this.context=context;
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public CompanyDirectryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_company_directory_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyDirectryAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getEmployeeName());
        holder.tvEmpCode.setText(list.get(position).getEmployeeCode());
        holder.tvDepartment.setText(list.get(position).getDepartment());
        holder.tvDesignation.setText(list.get(position).getDesignation());
        if(!list.get(position).getPhotoCode().equals("")){
            Picasso.get().load(list.get(position).getPhotoCode()).error(R.drawable.profile_rounded).into(holder.circleImageView);
        }
        holder.itemView.setOnClickListener(v -> {
            String data=list.get(holder.getAdapterPosition()).getEmployeeId()+"@"+list.get(position).getPhotoCode();
            itemClickListener.onClickItem(position,data);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvEmpCode,tvDepartment,tvDesignation;
        CircleImageView circleImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_empname);
            tvEmpCode=itemView.findViewById(R.id.tv_empcode);
            tvDepartment=itemView.findViewById(R.id.tv_department);
            tvDesignation=itemView.findViewById(R.id.tv_designation);
            circleImageView=itemView.findViewById(R.id.imageView2);
        }
    }
}
