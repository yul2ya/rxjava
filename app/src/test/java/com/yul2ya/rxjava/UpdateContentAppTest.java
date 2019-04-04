package com.yul2ya.rxjava;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class UpdateContentAppTest {

    @Test
    void test() throws InterruptedException {
        PublishSubject<Long> subject = PublishSubject.create();

        Observable.interval(100, TimeUnit.MILLISECONDS)
                .doOnNext(onNext -> subject.onNext(onNext/*String.valueOf(onNext)*/))
                .test();

        Observable.just("x")
                .concatWith(subject.filter(i -> i > 6).map(String::valueOf).take(4))
                //.concatWith(subject.filter(s -> s.equals("7")))
                .subscribe(onNext -> System.out.println(onNext),
                        throwable -> System.out.println(throwable.getClass().getSimpleName()));

        Thread.sleep(2000);
    }

    @Test
    void testOnChangedAndOnNext() {
        PublishSubject<String> subject = PublishSubject.create();

        Observable.just("OCF_RESOURCE_CHANGED")
                .concatWith(subject.firstOrError())
                .subscribe(System.out::println,
                        throwable -> System.out.println(throwable.getClass().getSimpleName()));

        Observable<String> ocfOk = Observable.just("OCF_OK").repeat(5);
        ocfOk.subscribe(onNext -> {
            System.out.println("ocfOk:" + onNext);
            subject.onNext(onNext);
        });
    }
}
