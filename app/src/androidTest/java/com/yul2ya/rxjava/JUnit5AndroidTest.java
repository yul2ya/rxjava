package com.yul2ya.rxjava;


import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.yul2ya.rxjava.common.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.yul2ya.rxjava.FakeAmbientService.downloadTemplateResource;
import static com.yul2ya.rxjava.ZipFileManager.unzip;

@RunWith(AndroidJUnit4.class)
public class JUnit5AndroidTest {
    @Test
    public void templateApp2() throws InterruptedException {
        Observable<String> authToken = Observable.just("authToken");

        Observable<Pair<String, FakeTemplateApp>> getTemplateApp = authToken
                .flatMap(token -> Observable.just(FakeAmbientService.getTemplateApp(token)),
                        Pair::new);

        getTemplateApp.doOnNext(Log::i)
                .test()
                .assertComplete();

        Observable<Pair<FakeTemplateApp, File>> downloadResources = authToken
                .flatMap((String token) -> Observable.just(FakeAmbientService.getTemplateApp(token)),
                        Pair::new)
                .flatMap(pair -> downloadTemplateResource(pair.first, pair.second),
                        (pair, file) -> new Pair<>(pair.second, file));

        Observable<FakeTemplateApp> unzipResources = downloadResources
                .flatMap(pair -> {
                    unzip(pair.second.getAbsolutePath());
                    return Observable.just(pair.first);
                });

        unzipResources.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .doOnNext(Log::i)
                .test()
                .await()
                .assertResult(FakeAmbientService.getTemplateApp("authToken"));
    }
}
