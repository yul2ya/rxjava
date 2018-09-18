package com.yul2ya.rxjava;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yul2ya.rxjava.common.Log;
import com.yul2ya.rxjava.rest.TestService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.yul2ya.rxjava.ZipFileManager.unzip;

public class FakeAmbientService {

    @NonNull
    public static FakeTemplateApp getTemplateApp(String authToken) {
        return new FakeTemplateApp("001", "yul2ya/Test/archive/master.zip", "v1", authToken);
    }

    public static TestService buildTestRestApi(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                //.addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient())
                .build().create(TestService.class);
    }

    @NonNull
    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    public static Observable<File> downloadTemplateResource(String authToken, FakeTemplateApp data) throws IllegalArgumentException{
        if (authToken == null || data == null) throw new IllegalArgumentException();
        return buildTestRestApi("https://github.com/")
                .downloadFile(data.getUrl())
                .flatMap(response -> Observable.just(saveToDisk(response, data)));
    }

    public static File saveToDisk(Response<ResponseBody> response, FakeTemplateApp data) throws IOException {
        String fileName = data.getId() + "." + data.getVersion();
        Log.i(fileName);
        Log.i(response.body().source());

        File file = new File("/data/data/com.yul2ya.rxjava/files/", "master.zip");
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeAll(response.body().source());
        sink.close();

        Log.i(file.getAbsolutePath());
        return file;
    }

    private static void logFileContents(File file) throws IOException {
        FileReader reader = new FileReader(file);
        int singleChar = -1;
        StringBuilder contents = new StringBuilder();
        while ((singleChar = reader.read()) != -1) {
            contents.append((char) singleChar);
        }
        Log.i(contents);
        reader.close();
    }

    private static void logZipFileContents(File file) throws IOException {
        if (!unzip(file.getAbsolutePath())) return;
        FileReader reader = new FileReader(file);
        int singleChar = -1;
        StringBuilder contents = new StringBuilder();
        while ((singleChar = reader.read()) != -1) {
            contents.append((char) singleChar);
        }
        Log.i(contents);
        reader.close();
    }
}
