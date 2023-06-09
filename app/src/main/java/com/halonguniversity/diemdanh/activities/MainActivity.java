package com.halonguniversity.diemdanh.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.activities.DiemDanhActivity;
import com.halonguniversity.diemdanh.adapters.LopAdapter;
import com.halonguniversity.diemdanh.utils.Constants;

public class MainActivity extends AppCompatActivity {

    LopAdapter lopAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcv_lop);
        lopAdapter = new LopAdapter(this, Constants.loginResponses);
        recyclerView.setAdapter(lopAdapter);
        findViewById(R.id.btn_cap_nhat_khuon_mat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DanhSachSinhVienActivity.class));
            }
        });
    }
}