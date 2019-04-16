package com.yul2ya.rxjava;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

class RxJavaTest {
    @Test
    void groupByTest() {
        Integer[] array = {1, 1, 1, 2, 3, 4, 5, 6, 6, 4, 34, 3, 2, 1};
        Observable<Integer> ob = Observable.fromArray(array);
        Observable<GroupedObservable<Integer, Integer>> ob2 = ob.groupBy(integer -> integer % 2);
        Observable ob3 = ob2.map(integerIntegerGroupedObservable -> integerIntegerGroupedObservable.getKey());

        ob2.subscribe(integerIntegerGroupedObservable -> {
            integerIntegerGroupedObservable
                    .doOnSubscribe(disposable -> System.out.println("subscribe " + integerIntegerGroupedObservable.getKey()))
                    .subscribe(integer -> System.out.print("(" + integerIntegerGroupedObservable.getKey() +"," + integer + ")"),
                            throwable -> System.out.println(throwable.getMessage()),
                            () -> System.out.println("complete " + integerIntegerGroupedObservable.getKey()));
        }, throwable -> System.out.println(throwable.getMessage()),
                () -> System.out.println("complete ob2"));

        /*ob3.subscribe(i -> System.out.println("고유값 " + i));

        ob3.subscribe(i -> {
            System.out.println("=======" + i + "==========");
            ob.filter(i::equals)
                    .subscribe(i2 -> System.out.println("filter: " + i2.toString()));
        });*/
    }

    static class Response {
        List<String> idList;
        String newId;

        Response(List<String> idList, String newId) {
            this.idList = idList;
            this.newId = newId;
        }
    }
    @Test
    void getTemplateConfigId() throws InterruptedException {
        Observable<Response> response = Observable.just(new Response(Arrays.asList("id1", "id2"), "idNew"));
        Observable<String> idList = response.map(res -> res.idList).flatMap(Observable::fromIterable);
        Observable<String> idNew = response.map(res -> res.newId);
        //idList.subscribe(System.out::println);
        //idNew.subscribe(System.out::println);

        //Observable.concat(idList, idNew).subscribe(System.out::println);

        response.map(res -> res.newId)
                .doOnSubscribe(disposable -> idList.subscribe(onNext -> System.out.println(Thread.currentThread().getName() + ": " + onNext)))
                .flatMap(newId -> Observable.just(newId + " is real New"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(onNext -> System.out.println(Thread.currentThread().getName() + ": " + onNext));

        Thread.sleep(1000);

        //???
        /*response.publish(res -> Observable.concat(res.map(r -> r.newId), res.flatMap(r -> Observable.fromIterable(r.idList))))
                .subscribe(System.out::println);*/

    }

    @Test
    void connectTest() {
        Flowable<Integer> source = Flowable.range(1, 5);

        ConnectableFlowable<Integer> cf = source.publish();

        cf.filter(v -> v % 2 == 0).subscribe(v -> System.out.println("Even: " + v));

        cf.filter(v -> v % 2 != 0).subscribe(v -> System.out.println("Odd: " + v));

        cf.connect(); // subscribe started when connect is called
    }
}
