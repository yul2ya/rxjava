package com.yul2ya.rxjava;

import android.util.Pair;

import com.yul2ya.rxjava.common.GsonHelper;
import com.yul2ya.rxjava.common.Log;
import com.yul2ya.rxjava.common.OkHttpHelper;
import com.yul2ya.rxjava.common.Shape;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.yul2ya.rxjava.FakeAmbientService.buildTestRestApi;
import static com.yul2ya.rxjava.FakeAmbientService.downloadTemplateResource;
import static org.junit.jupiter.api.Assertions.assertEquals;



class JUnit5Basic {
    @DisplayName("JUnit 5 First Example")
    @Test
    void first() {
        int expected = 3;
        int actual = 1 + 2;
        Log.i("test", "first() is working...");
        assertEquals(expected, actual);
    }

    @DisplayName("test getShape() Observable")
    @Test
    void testGetShapeObservable() {
        String[] data = {"1", "2-R", "3-T"};
        Observable<String> source = Observable.fromArray(data)
                .map(Shape::getShape);

        String[] expected = {Shape.BALL, Shape.RECTANGLE, Shape.TRIANGLE};
        List<String> actual = new ArrayList<>();
        source.doOnNext(Log::d)
                .subscribe(actual::add);

        assertEquals(Arrays.asList(expected), actual);
    }

    @DisplayName("#1: using TestObserver for Shape.getShape()")
    @Test
    void testGetShapeObservable2() {
        String[] data = {"1", "2-R", "3-T"};
        Observable<String> source = Observable.fromArray(data)
                .map(Shape::getShape);

        String[] expected = {Shape.BALL, Shape.RECTANGLE, Shape.TRIANGLE};
        source.doOnNext(Log::d)
                .test()
                .assertResult(expected)
                .assertComplete();
    }

    @DisplayName("#assertFailure() example")
    @Test
    void assertFailureExample() {
        String[] data = {"100", "200", "%300"};
        Observable<Integer> source = Observable.fromArray(data)
                .map(Integer::parseInt);

        source.doOnNext(Log::d)
                .test()
                .assertFailure(NumberFormatException.class, 100, 200);
    }

    @DisplayName("async observable test")
    @Test
    void interval() throws InterruptedException {
        Observable<Integer> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .take(5)
                .map(Long::intValue);

        source.doOnNext(Log::d)
                .test()
                //.awaitDone(1L, TimeUnit.SECONDS)
                .await()
                .assertResult(0, 1, 2, 3, 4);
    }

    @DisplayName("test http")
    @Test
    void testHttp() {
        final String url = "https://api.github.com/users/yul2ya";
        Observable<String> source = Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get)
                .doOnNext(Log::d)
                .map(json -> GsonHelper.parseValue(json, "name"))
                .observeOn(Schedulers.newThread());

        String expected = "Seongyul Kim";
        source.doOnNext(Log::i)
                .test()
                .awaitDone(3, TimeUnit.SECONDS)
                .assertResult(expected);
    }

    @DisplayName("test get templateApp")
    @Test
    void templateApp() throws InterruptedException {
        Observable<String> authToken = Observable.just("authToken");
        authToken.doOnNext(Log::i).test().assertResult("authToken");

        FakeTemplateApp data = FakeAmbientService.getTemplateApp("authToken");
        buildTestRestApi("https://github.com/").downloadFile(data.getUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(response -> FakeAmbientService.saveToDisk(response, data))
                .test()
                .await()
                .assertValueCount(1)
                .assertNoErrors()
                .assertComplete();

        Observable<FakeTemplateApp> downloadResource = authToken
                .flatMap((String token) -> Observable.just(FakeAmbientService.getTemplateApp(token)),
                        (token, response) -> {
                            downloadTemplateResource(token, response);
                            return response;});
        downloadResource.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .doOnNext(Log::i)
                .test()
                .await()
                .assertResult(FakeAmbientService.getTemplateApp("authToken"));
    }

    @DisplayName("test get templateApp2")
    @Test
    void templateApp2() throws InterruptedException {
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
