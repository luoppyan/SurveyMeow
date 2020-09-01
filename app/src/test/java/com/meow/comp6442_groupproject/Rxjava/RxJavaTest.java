package com.meow.comp6442_groupproject.Rxjava;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;


public class RxJavaTest {

    private TestScheduler mTestScheduler;

    @Test
    public void testObserver() {

        TestObserver<Integer> testObserver = TestObserver.create();
        testObserver.onNext(1);
        testObserver.onNext(2);
        // assert values are equal or not
        testObserver.assertValues(1, 2);

        testObserver.onComplete();
        // assert complete
        testObserver.assertComplete();
    }

    @Before
    public void setUp() {
        mTestScheduler = new TestScheduler();
    }

    @Test
    public void testJust() {
        // test subscriber
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        Flowable.just("A", "B", "C").subscribe(testSubscriber);
        // assert not appear
        testSubscriber.assertNever("D");
        // assert equality
        testSubscriber.assertValues("A", "B", "C");
        // assert count
        testSubscriber.assertValueCount(3);
        // assert terminate
        testSubscriber.assertTerminated();
    }

    @Test
    public void testError() {
        TestSubscriber testSubscriber = new TestSubscriber();
        Exception exception = new RuntimeException("error");

        Flowable.error(exception).subscribe(testSubscriber);

        testSubscriber.assertError(exception);
        testSubscriber.assertErrorMessage("error");
    }

    @Test
    public void testFrom() {

        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        Flowable.fromIterable(Arrays.asList(1, 2)).subscribe(testSubscriber);

        testSubscriber.assertValues(1, 2);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertTerminated();
    }

    @Test
    public void testRange() {

        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        Flowable.range(3, 3).subscribe(testSubscriber);

        testSubscriber.assertValues(3, 4, 5);
        testSubscriber.assertValueCount(3);
        testSubscriber.assertTerminated();
    }

    @Test
    public void testRepeat() {

        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        Flowable.fromIterable(Arrays.asList(1, 2))
                .repeat(2) // repeat 2 times
                .subscribe(testSubscriber);

        testSubscriber.assertValues(1, 2, 1, 2);
        testSubscriber.assertValueCount(4);
        testSubscriber.assertTerminated();
    }

    @Test
    public void testBuffer() {

        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
        Flowable.just("A", "B", "C", "D")
                .buffer(2)
                .subscribe(testSubscriber);

        testSubscriber.assertResult(Arrays.asList("A", "B"), Arrays.asList("C", "D"));
        testSubscriber.assertValueCount(2);
        testSubscriber.assertTerminated();
    }

    @Test
    public void testInterval() {

        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        Flowable.interval(1, TimeUnit.SECONDS, mTestScheduler)
                .take(10)
                .subscribe(testSubscriber);


        mTestScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        testSubscriber.assertValues(0L, 1L, 2L);
        testSubscriber.assertValueCount(3);
        testSubscriber.assertNotTerminated();

        mTestScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        testSubscriber.assertValues(0L, 1L, 2L, 3L, 4L);
        testSubscriber.assertValueCount(5);
        testSubscriber.assertNotTerminated();

        mTestScheduler.advanceTimeTo(10, TimeUnit.SECONDS);
        testSubscriber.assertValueCount(10);
        testSubscriber.assertTerminated();
    }

    @Test
    public void testTimer() {

        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        Flowable.timer(5, TimeUnit.SECONDS, mTestScheduler)
                .subscribe(testSubscriber);

        mTestScheduler.advanceTimeTo(5, TimeUnit.SECONDS);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertTerminated();
    }

}