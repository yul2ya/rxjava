package com.yul2ya.rxjava;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yul2ya.rxjava.common.Log;
import com.yul2ya.rxjava.rest.TestService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        if (!unpackZip(file.getAbsolutePath())) {
            Log.e("Error! unpack Zip file");
            throw new IOException();
        }
        Log.i("unpack Zip file is success!");
        Log.i(file.getAbsolutePath());
        File firstUrl = new File(file.getAbsoluteFile().getParentFile().getPath(), "Test-master/firstUrl");
        logFileContents(firstUrl);

        return firstUrl;
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
        if (!unpackZip(file.getAbsolutePath())) return;
        FileReader reader = new FileReader(file);
        int singleChar = -1;
        StringBuilder contents = new StringBuilder();
        while ((singleChar = reader.read()) != -1) {
            contents.append((char) singleChar);
        }
        Log.i(contents);
        reader.close();
    }

    public static boolean unpackZip(String filePath) {
        InputStream is;
        ZipInputStream zis;
        try {

            File zipFile = new File(filePath);
            String parentFolder = zipFile.getParentFile().getPath();
            String filename;

            is = new FileInputStream(filePath);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                if (ze.isDirectory()) {
                    File fmd = new File(parentFolder + "/" + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(parentFolder + "/" + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
