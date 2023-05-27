package com.halonguniversity.diemdanh.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halonguniversity.diemdanh.R;
import com.halonguniversity.diemdanh.entities.LoginResponse;
import com.halonguniversity.diemdanh.service.ApiService;
import com.halonguniversity.diemdanh.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        initView();
        login();
    }

    private void login() {
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setTitle("Đang đăng nhập");
                dialog.show();
                String user = edtUsername.getText().toString();
                String pass = edtPassword.getText().toString();
                ApiService.api.login(user, pass).enqueue(new Callback<List<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {
                        if (response.isSuccessful()) {
                            Constants.loginResponses = response.body();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("TAG", "onFailure: " + t.getMessage());
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void initView() {
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
    }
}