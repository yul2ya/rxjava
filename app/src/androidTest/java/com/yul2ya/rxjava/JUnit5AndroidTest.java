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

@RunWith(AndroidJUnit4.class)
public class JUnit5AndroidTest {
    @Test
    public void templateApp2() throws InterruptedException {
        Observable<String> authToken = Observable.just("authToken");

        Observable<Pair<String, FakeTemplateApp>> getTemplateApp = authToken
                .flatMap((String token) -> Observable.just(FakeAmbientService.getTemplateApp(token)),
                        (token, data) -> {
                            return new Pair<>(token, data);
                        });

        getTemplateApp.doOnNext(Log::i)
                .test();

        Observable<Pair<FakeTemplateApp, File>> downloadResources = authToken
                .flatMap((String token) -> Observable.just(FakeAmbientService.getTemplateApp(token)),
                        (token, data) -> {
                            return new Pair<>(token, data);
                        })
                .flatMap((Pair<String, FakeTemplateApp> pair) -> {
                            return downloadTemplateResource(pair.first, pair.second);
                        },
                        (Pair<String, FakeTemplateApp> pair, File file) -> new Pair<>(pair.second, file));

        Observable<FakeTemplateApp> unzipResources = downloadResources
                .flatMap(pair -> {
                    FakeAmbientService.unpackZip(pair.second.getAbsolutePath());
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
