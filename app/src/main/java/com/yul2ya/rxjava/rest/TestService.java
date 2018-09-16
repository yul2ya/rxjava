package com.yul2ya.rxjava.rest;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface TestService {
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String url);
}
