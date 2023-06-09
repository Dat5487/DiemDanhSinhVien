package com.halonguniversity.diemdanh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.entities.SinhVienLopTC;

import java.util.List;

public class SinhVienTCAdapter extends RecyclerView.Adapter<SinhVienTCAdapter.ViewHolder> {
    Context context;
    List<SinhVienLopTC> sinhVienLopTCList;

    public SinhVienTCAdapter(Context context, List<SinhVienLopTC> sinhVienLopTCList) {
        this.context = context;
        this.sinhVienLopTCList = sinhVienLopTCList;
    }

    @NonNull
    @Override
    public SinhVienTCAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sinh_vien, parent, false);
        return new SinhVienTCAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SinhVienTCAdapter.ViewHolder holder, int position) {
        SinhVienLopTC sinhVienLopTC = sinhVienLopTCList.get(position);
        holder.tvHoTen.setText(sinhVienLopTC.getHoten());
        holder.tvId.setText("Số buổi: " + sinhVienLopTC.getSobuoidd() + "");
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.startActivity(new Intent(context, SinhVienLopTCHocTCActivity.class));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return sinhVienLopTCList.size();
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
