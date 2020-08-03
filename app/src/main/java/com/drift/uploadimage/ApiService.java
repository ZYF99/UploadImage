package com.drift.uploadimage;


import java.io.File;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiService {
    //上传图片的请求
    @Multipart
    @POST("upload")
    Call<ResponseBody> upLoadImage(@Part MultipartBody.Part file);

}