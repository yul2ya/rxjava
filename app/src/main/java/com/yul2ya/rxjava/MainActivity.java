package com.yul2ya.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yul2ya.rxjava.common.CommonUtils;
import com.yul2ya.rxjava.common.OkHttpHelper;
import com.yul2ya.rxjava.rest.TestService;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://github.com/yul2ya/Test/blob/master/";

    private static final Retrofit restrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkHttpClient())
            .build();

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testConcat();
    }

    class Rsp {
        int id;
        String url;

        Rsp() {
            id = 0;
            url = "https://github.com/yul2ya/Yka.git";
        }
    }

    public void testConcat() {
        Observable<String> token = Observable.just("authToken");
        Observable<Rsp> templateApp = Observable.just(new Rsp());
        Observable<File> templatAppResource;

        Observable<String> source = Observable.just(BASE_URL.concat("firstUrl"))
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get)
                .concatWith(Observable.just(BASE_URL.concat("secondUrl"))
                        .map(OkHttpHelper::get));
        source.subscribe(com.yul2ya.rxjava.common.Log::it);
        CommonUtils.sleep(5000);

    }

    public void testRetrofit() {
        TestService service = restrofit.create(TestService.class);
        service.downloadFile("firstUrl")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("onSubscribe", "");
                    }

                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        Log.i("onNext", response.body().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("onError", "", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("onComplete", "");
                    }
                });
    }

    //3-3
    public void testMap() {
        Function<String, Integer> ballToIndex = ball -> {
            switch (ball) {
                case "RED":
                    return 1;
                case "YELLOW":
                    return 2;
                case "GREEN":
                    return 3;
                case "BLUE":
                    return 5;
                default:
                    return -1;
            }
        };

        String[] balls = {"RED", "YELLOW", "GREEN", "BLUE"};
        Observable<Integer> source = Observable.fromArray(balls)
                .map(ballToIndex);
        source.subscribe(data -> System.out.println("value = " + data));
    }

    // 2-24
    public void subjectLikeSubscriber() {
        Float[] temperature = {10.1f, 13.4f, 12.5f};
        Observable<Float> source = Observable.fromArray(temperature);


        AsyncSubject<Float> subject = AsyncSubject.create();
        subject.subscribe(data -> System.out.println("Subscriber #1 => " + data));

        source.subscribe(subject);
    }

    // 2-23
    public void subjectLikeObservable() {
        AsyncSubject<String> subject = AsyncSubject.create();
        subject.subscribe(data -> System.out.println("Subscriber #1 => " + data));
        subject.onNext("1");
        subject.onNext("3");
        subject.subscribe(data -> System.out.println("Subscriber #2 => " + data));
        subject.onNext("5");
        subject.onComplete();
    }

    // 2-19
    public void emitSingle() {
        Single<String> source = Single.just("Hello Single");
        source.subscribe(System.out::println);
    }

    // 2-3
    public void emitCreate() {
        Observable<Integer> source = Observable.create(
                (ObservableEmitter<Integer> emitter) -> {
                    emitter.onNext(100);
                    emitter.onNext(200);
                    emitter.onNext(300);
                    emitter.onComplete();
                }
        );
        //source.subscribe(System.out::println);
        source.subscribe(data -> System.out.println("Result : " + data)); // 2-5
    }

    // 2-1
    public void emitInt() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .subscribe(System.out::println);
    }

    // 1-2
    public void emitString() {
        Observable.just("Hello", "RxJava 2!!")
                .subscribe(System.out::println);
    }
}
