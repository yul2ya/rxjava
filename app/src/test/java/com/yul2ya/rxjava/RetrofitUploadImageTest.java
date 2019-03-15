package com.yul2ya.rxjava;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.junit.jupiter.api.Test;

import java.net.URL;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;


public class RetrofitUploadImageTest {
    interface UploadImageApi {
        @POST("/upload")
        Observable<ResponseBody> uploadImage(@Body RequestBody body);
    }

    private static final UploadImageApi uploadImageApi = new Retrofit.Builder()
            .baseUrl("http://localhost:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkHttpClient())
            .build()
            .create(UploadImageApi.class);


    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    @Test
    void test() {
        URL resource = getClass().getResource("20180913_seowoo.jpg");
        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), resource.getFile());
        uploadImageApi.uploadImage(body)
                .subscribe(response -> {
                    System.out.println(response.string());
                });
    }
}
