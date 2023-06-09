package com.halonguniversity.diemdanh.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.adapters.SinhVienAdapter;
import com.halonguniversity.diemdanh.adapters.SinhVienTCAdapter;
import com.halonguniversity.diemdanh.entities.SinhVien;
import com.halonguniversity.diemdanh.entities.SinhVienLopTC;
import com.halonguniversity.diemdanh.service.ApiService;
import com.halonguniversity.diemdanh.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachSinhVienActivity extends AppCompatActivity {

    RecyclerView rcvSV;
    SinhVienAdapter adapter;
    List<SinhVien> sinhVienList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_sinh_vien);
        rcvSV = findViewById(R.id.rcv_sv);
        load();
    }

    private void load() {
        ApiService.api.getAllSV().enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    sinhVienList = response.body();
                    adapter = new SinhVienAdapter(getApplicationContext(), sinhVienList);
                    rcvSV.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {

            }
        });
    }
}