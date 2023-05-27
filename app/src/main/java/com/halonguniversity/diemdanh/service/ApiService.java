package com.halonguniversity.diemdanh.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halonguniversity.diemdanh.entities.LoginResponse;
import com.halonguniversity.diemdanh.entities.SinhVien;
import com.halonguniversity.diemdanh.entities.SinhVienLopTC;
import com.halonguniversity.diemdanh.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
//    String BASE_URL = "https://lastsparklycat86.conveyor.cloud/";
    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS).writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();
    ApiService api = new Retrofit.Builder().baseUrl(Constants.url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiService.class);

    @GET("Login")
    Call<List<LoginResponse>> login(@Query("username") String username, @Query("password") String password);

    @GET("GETDSSVofLopTC")
    Call<List<SinhVienLopTC>> getDsLopHocTC(@Query("maloptc") int maloptc);

    @POST("TaoHdDD")
    Call<Void> taoHdDD(@Query("maloptc") int maloptc, @Query("diadiem") String diaDiem);

    @PUT("PutTrangThaiDD")
    Call<Void> diemDanh(@Query("masv") int masv, @Query("maloptc") int maloptc);

    @POST("UpdateSinhVien")
    Call<Void> updateSinhVien(@Body Map<String, Object> thongTin);

    @GET("GetAllSinhviens")
    Call<List<SinhVien>> getAllSV();
}
