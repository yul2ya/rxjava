package com.yul2ya.rxjava;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.Observable;

class RxJavaExample {

    @Test
    void test_return_value_from_observable() throws ExecutionException, InterruptedException {
        String result = getValueFromObservable().get();
        System.out.print(result);
    }

    private Future<String> getValueFromObservable() {
        List<String> list = Arrays.asList("1a", "2a", "3a", "b", "c");
        return Observable.fromIterable(list)
                .filter(item -> item.contains("d"))
                .map(item -> item + "+")
                .first("failed to find proper item")
                .toFuture();
    }

    @Test
    void rxjava_sample_code() {
        class User {
            private String id;

            User(String id) {
                this.id = id;
            }

            String getId() {
                return id;
            }
        }
        List<User> users = Arrays.asList(new User("aaa"), new User("bbb"), new User(null), new User("ccc"));
        Observable.fromIterable(users)
                .filter(user -> user.id != null)
                .map(User::getId)
                .subscribe(System.out::println, Throwable::printStackTrace);

    }
}
