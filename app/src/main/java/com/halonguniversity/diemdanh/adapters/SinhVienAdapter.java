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
import com.halonguniversity.diemdanh.activities.CapNhatFaceActivity;
import com.halonguniversity.diemdanh.activities.DanhSachSinhVienActivity;
import com.halonguniversity.diemdanh.entities.SinhVien;
import com.halonguniversity.diemdanh.utils.Constants;

import java.util.List;

public class SinhVienAdapter extends RecyclerView.Adapter<SinhVienAdapter.ViewHolder> {
    Context context;
    List<SinhVien> sinhVienList;

    public SinhVienAdapter(Context context, List<SinhVien> sinhVienList) {
        this.context = context;
        this.sinhVienList = sinhVienList;
    }

    @NonNull
    @Override
    public SinhVienAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sinh_vien, parent, false);
        return new SinhVienAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SinhVienAdapter.ViewHolder holder, int position) {
        SinhVien sinhVien = sinhVienList.get(position);
        holder.tvHoTen.setText(sinhVien.getHoten());
        holder.tvId.setText("Lớp: " + sinhVien.getMalophc() + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.sinhVien = sinhVien;
                Intent intent = new Intent(context, CapNhatFaceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sinhVienList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHoTen, tvId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoTen = itemView.findViewById(R.id.tv_hoten);
            tvId = itemView.findViewById(R.id.tv_id);
        }
    }
}
