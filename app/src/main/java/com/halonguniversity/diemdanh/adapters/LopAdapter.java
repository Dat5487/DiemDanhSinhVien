package com.halonguniversity.diemdanh.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.activities.LopHocTCActivity;
import com.halonguniversity.diemdanh.entities.LoginResponse;
import com.halonguniversity.diemdanh.utils.Constants;

import java.util.List;

public class LopAdapter extends RecyclerView.Adapter<LopAdapter.ViewHolder> {
    Context context;
    List<LoginResponse> responseList;

    public LopAdapter(Context context, List<LoginResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginResponse response = responseList.get(position);
        holder.tvMaHP.setText(response.getMahp() + "");
        holder.tvTenLop.setText(response.getTenltc());
        if (response.isTrangthai()) {
            holder.tvTrangThai.setText("Đang học");
        } else {
            holder.tvTrangThai.setText("Hoàn thành");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.maloptc = response.getMaloptc();
                context.startActivity(new Intent(context, LopHocTCActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaHP, tvTenLop, tvTrangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaHP = itemView.findViewById(R.id.tv_mahp);
            tvTenLop = itemView.findViewById(R.id.tv_tenlop);
            tvTrangThai = itemView.findViewById(R.id.tv_trangthai);
        }
    }
}
