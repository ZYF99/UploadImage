package com.drift.uploadimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.0.105:18280";

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //选图按钮点击事件
        findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
            }
        });

        //上传按钮点击事件
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }

    //选图
    private void choosePic() {
        PictureSelectUtil.showAlbum(this);
    }

    //上传图片
    private void uploadImage() {
        if (file != null) uploadImage(file);
    }

    //选图之后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureSelectUtil.REQUESTCODE_AVATAR) {
                assert data != null;
                //通过回调的图片路径 得到图片文件
                file = new File(PictureSelector.obtainMultipleResult(data).get(0).getPath());
            }
        }
    }

    //上传图片得请求
    private void uploadImage(File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody photoRequestBody =
                        RequestBody.create(MediaType.parse("image/png"), file);
                MultipartBody.Part photo = MultipartBody.Part.createFormData(
                        "imageFile",
                        file.getName(),
                        photoRequestBody
                );
                //上传请求
                provideApiClient().createService(ApiService.class).upLoadImage(photo).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //成功回调
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        }).start();
    }

    //提供retrofit的客户端对象
    public ApiClient<ApiService> provideApiClient() {
        ApiClient.Builder apiClient = new ApiClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        apiClient.getOkBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor);
        return apiClient.build(BASE_URL);
    }

}