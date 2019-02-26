package jason.com.rxremvplib.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jason on 18/7/12.
 */

public class RxTestUtils {

    //map是一对一的转化，flatmap可以一对多后再组合（无序） ，注意传入的参数和传出的结果对象类型，
    //flatmap是返回一个observable,可以再次发送
    private void test1() {
        Observable.range(0, 5)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(@NonNull Integer integer) throws Exception {
                        //将发射的数据变化后返回
                        return integer + "^2=" + integer * integer;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println(s);
                    }
                });

        //输出结果123456789  ，对每组元素进行函数fromarry后逐一发射
        Integer nums1[] = new Integer[]{1, 2, 3, 4};
        Integer nums2[] = new Integer[]{5, 6};
        Integer nums3[] = new Integer[]{7, 8, 9};
        Observable.just(nums1, nums2, nums3)
                .flatMap(new Function<Integer[], Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(@NonNull Integer[] integers) throws Exception {
                        return Observable.fromArray(integers);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });

    }

    private void test2() {
        //可以将数据交错发射
        Integer nums1[] = new Integer[]{5, 6, 7, 8, 9};
        Observable.just(1, 2, 3, 4, 5)                      //一个observable用just发射数据，另一个observable用fromArray逐一发射
                .mergeWith(Observable.fromArray(nums1))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });

        //数据合并单不交错
        Observable.just(1, 2, 3, 4, 5)
                .concatWith(Observable.fromArray(nums1))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });
    }


    private void test3() {  //聚合数据  两组数据位置对应组合，最后数据少的中断发射
        String names[] = new String[]{"红娃", "橙娃", "黄娃", "绿娃", "青蛙", "蓝蛙", "紫娃"};
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .zipWith(Observable.fromArray(names), new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                        return integer + s;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println(s);
                    }
                });

    }

    //https://blog.csdn.net/johnny901114/article/details/80032801
    //subscribeOn()它的位置随意，但只有第一次有效，只会对没有指定线程的observable（上游发送数据）有效，而observeOn()每次都可以切换线程

    //除了subscribeOn指定线程是在observable后的随意位置，只有第一次有效，其他操作符的使用，都要在操作符前切换指定线程用subscribeOn()
    //subscribeOn来指定对数据的处理运行在特定的线程调度器Scheduler上，直到遇到observeOn改变线程调度器。若多次设定，则只有一次起作用。
    //observeOn指定下游操作运行在特定的线程调度器Scheduler上。若多次设定，每次均起作用。
    private void test4() {  //线程调度，异步函数处理
        //通过subscribeOn(Schedulers.io())指定Observable在Schedulers.io( )调度器的线程中，
        //每隔1秒发射一次数据，通过observeOn(AndroidSchedulers.mainThread())指定Observer在Android UI线程中接收数据
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 5; i++) {
                    System.out.println("发射线程:" + Thread.currentThread().getName() + "----->" + "发射:" + i);
                    Thread.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())       //无论多次调用，只有第一次有效
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println("接收线程:" + Thread.currentThread().getName() + "----->" + "接收:" + integer);
                    }
                });

        //一次subscribeOn 两次observeOn
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 2; i++) {
                    System.out.println("发射线程:" + Thread.currentThread().getName() + "----->" + "发射：" + i);
                    Thread.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())//设置可观察对象在Schedulers.io()的线程中发射数据
                .observeOn(AndroidSchedulers.mainThread())  //指定map操作符在Schedulers.newThread()的线程中处理数据
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer i) throws Exception {
                        System.out.println("处理线程:" + Thread.currentThread().getName() + "----->" + "处理:" + i);
                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())  //设置观察者在AndroidSchedulers.mainThread()的线程中处理数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println("接收线程：" + Thread.currentThread().getName() + "---->" + "接收：" + integer);
                    }
                });
    }

    //在当前线程立即执行任务，如果当前线程有任务在执行，则会将其暂停，等插入进来的任务执行完之后，再将未完成的任务接着执行。
    private void test6() { //在前面异步处理数据会有滞后的
        //输出 发射0接收0 发射1接收1。。 22 33 44 55
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 5; i++) {
                    System.out.println("发射数据：" + Thread.currentThread().getName() + "---->" + "发射:" + i);
                    Thread.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())       //设置可观察对象在Schedulers.io()的线程发射数据
                .observeOn(Schedulers.trampoline()) //设置观察者在当前线程中接收数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Thread.sleep(2000);// 休息2s后再处理数据
                        System.out.println("接收线程:" + Thread.currentThread().getName() + "----->" + "接收:" + integer);
                    }
                });

        //通过Schedulers.single()将数据的发射，处理，接收在Schedulers.single()的线程单例中排队执行，
        // 当此线程中有任务执行时，其他任务将会按照先进先出的顺序依次执行。
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 3; i++) {
                    System.out.println("发射线程:" + Thread.currentThread().getName() + "----->" + "发射:" + i);
                    Thread.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.single()) //设置可观察对象在Schedulers.single()的线程中发射数据
                .observeOn(Schedulers.single()) //指定map操作符在Schedulers.single()的线程中处理数据
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        System.out.println("处理线程:" + Thread.currentThread().getName() + "---->" + "处理:" + integer);
                        return integer;
                    }
                })
                .observeOn(Schedulers.single())     //设置观察者在Schedulers.single()的线程中接收数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println("接收线程:" + Thread.currentThread().getName() + "---->" + "接收:" + integer);
                    }
                });
    }

    //背压解决-只有上下游运行在各自的线程中，且上游发射数据速度大于下游接收处理数据的速度时，才会产生背压问题
    //Flowable是Publisher与Subscriber这一组观察者模式中Publisher的典型实现，Observable是ObservableSource/Observer这一组观察者模式中ObservableSource的典型实现；
    private void test7() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + "开始发射数据:" + System.currentTimeMillis());
                for (int i = 0; i <= 500; i++) {
                    System.out.println(threadName + "发射-----》" + i);
                    e.onNext(i);
                    Thread.sleep(100);
                }
                System.out.println(threadName + "发射数据结束" + System.currentTimeMillis());
                e.onComplete();
            }
        }, BackpressureStrategy.DROP)       //对于大于128，对于存满缓存池的数据会丢弃
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        //设置下游对数据的请求数量，上游可以根据下游的需求量，按需发送数据，默认0-只成功发射不接受
                        s.request(Long.MAX_VALUE); //假如 request（2），不管发射多少数据，只接收2个
//                        s.cancel();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(300);  //每隔300ms接收一次数据
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " 接收------》" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println(Thread.currentThread().getName() + "接收-----》完成");
                    }
                });

        //终极方案-上游按需发送数据，异步线程，不会丢失数据，内存平稳
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                int i = 0;
                while (true) {
                    if (e.requested() == 0) continue;//让flowable按需发送数据/当前未完成数量=0会暂停发送数据
                    System.out.println("发射---->" + i);
                    i++;
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    private Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);       //设置初始请求数据量为1
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(50);
                            System.out.println("接收----》" + integer);
                            mSubscription.request(1);   //每接收一条数据增加一条请求量
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //操作符
    //flatmap 无序 一个observable转令一个Observable ，有序concatMap
    public static void textCode() {
        Observable.just(1, 2, 3)
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Integer integer) throws Exception {
                        List<String> list = new ArrayList<String>();
                        list.add("hello" + integer);
                        return Observable.fromIterable(list).delay(0, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        System.out.println("" + o);
                    }
                });

        //按长度分割为集合
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(@NonNull List<Integer> integers) throws Exception {
                        //[1,2,3]   [3,4,5]   [5]
                    }
                });

        //处理最多4个observable，前一个结束才读下一个  串联发送
        Observable.concat(Observable.just(1, 2).delay(1, TimeUnit.SECONDS),
                Observable.just("a"))
                .subscribe(new Consumer<Serializable>() {
                    @Override
                    public void accept(@NonNull Serializable serializable) throws Exception {
                        // 1s sout: 1 2 a
                    }
                });

        //并联输出
        Observable.merge(Observable.just(1, 2).delay(1, TimeUnit.SECONDS),
                Observable.just("b"))
                .subscribe(new Consumer<Serializable>() {
                    @Override
                    public void accept(@NonNull Serializable serializable) throws Exception {
                        // sout: a (1s) 1 2
                    }
                });

        //每次用一个方法处理一个值，可以有一个seed作为初始值
        Observable.just(1, 2, 3, 4)
                .reduce(8, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        //sout: 18   8+1=9   9+2=11  11+3=14 14+4=18
                    }
                });
        //一个页面显示的数据需要多个来源于接口
        Observable.zip(Observable.just(1, 2), Observable.just(3, 4), new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                return "zip=" + integer + integer2;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println(s);
                    }
                });
        //发送list数据
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        Observable.fromIterable(list)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println("data=" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Log.v("TAG", "onNext>>>" + aLong);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //使用flatmap代替多重循环  需求：将person对象中plan的action打印出啦
        List<Person> personList = new ArrayList<>();
        //map:
        Observable.fromIterable(personList)
                .map(new Function<Person, List<Plan>>() {
                    @Override
                    public List<Plan> apply(@NonNull Person person) throws Exception {
                        return person.getPlanList();
                    }
                })
                .subscribe(new Consumer<List<Plan>>() {
                    @Override
                    public void accept(@NonNull List<Plan> plen) throws Exception {
                        for (Plan plan : plen) {
                            List<String> planActionList = plan.getActionList();
                            for (String action : planActionList) {
                                Log.v("TAG", "onNext_" + action);
                            }
                        }
                    }
                });
        //flatMap:无序
        Observable.fromIterable(personList)
                .flatMap(new Function<Person, ObservableSource<Plan>>() {
                    @Override
                    public ObservableSource<Plan> apply(@NonNull Person person) throws Exception {
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .flatMap(new Function<Plan, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull Plan plan) throws Exception {
                        return Observable.fromIterable(plan.getActionList());
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.v("TAG", "onNext__+" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Flowable.just(list)
                .flatMap(new Function<List<Integer>, Publisher<Integer>>() {
                    @Override
                    public Publisher<Integer> apply(@NonNull List<Integer> integers) throws Exception {
                        return Flowable.fromIterable(integers);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });

        //buffer(count,skip)
        Observable.just(1, 2, 3, 4, 5)
                .buffer(2, 1)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(@NonNull List<Integer> integers) throws Exception {
                        Log.v("TAG", "缓冲区大小:" + integers.size());
                        for (Integer integer : integers) {
                            Log.v("TAG", "--元素：" + integer);
                        }
                        //sout: 12 23 34 45 5
                    }
                });
        //并行发送
        Observable.merge(
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long aLong) throws Exception {
                        return "A" + aLong;
                    }
                }),
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long aLong) throws Exception {
                        return "B" + aLong;
                    }
                }))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.v("TAG", "onNext>" + s);
                    }
                });
        //过滤不符合该类型的事件
        Observable.just(1, 2, 3, "kkk", "kl;;")
                .ofType(Integer.class)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        //sout: 1 2 3
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //过滤 小于2的发射事件
        Observable.just(1, 2, 3)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer integer) throws Exception {
                        return integer < 2;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        //sout: 1
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //发送方法，在主线程执行
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("a");
                e.onNext("b");
                e.onComplete();
            }
        });

        //一个简单的链式订阅过程
        Observable.just("a", "b")    //新建被观察者-事件
                .subscribeOn(Schedulers.io())   //被观察者创建在io线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() { //新建观察者
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s == "-1") {    //“-1”为异常数据，解除订阅
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    Button button;

    public void onCodeClick(final int count) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        button.setEnabled(false);
                        button.setTextColor(Color.GRAY);
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        button.setText(aLong + "s重发");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        button.setEnabled(true);
                        button.setTextColor(Color.RED);
                        button.setText("发送验证码");
                    }
                });
    }

        EditText et_des;
    public void editLimit() {
        RxTextView.textChangeEvents(et_des)
                .subscribe(new Consumer<TextViewTextChangeEvent>() {
                    @Override
                    public void accept(@NonNull TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        if (et_des.getText().toString().length() > 500) {
//                            TopSnackbarUtils.showWarning(RecruiteditAty.this, String.format(getString(R.string.input_limit_warning), 500));
                            String str_input = et_des.getText().toString().substring(0, 500);
                            et_des.setText(str_input);
                            et_des.setSelection(str_input.length());
                        }
                    }
                });
    }

    //https://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492706&idx=1&sn=d7d213a1db9c8ae3a5b0525d45863518&chksm=8eec871db99b0e0bc4d4d1aa2b7ed5d7c32e5299aee0f0818a798c2deb2996f40f8971c7a6a2&mpshare=1&scene=1&srcid=0604KTpOKtDrtWcbbTP24foP#rd
    public class Person {
        private String name;
        private List<Plan> planList = new ArrayList<>();

        public Person(String name, List<Plan> planList) {
            this.name = name;
            this.planList = planList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Plan> getPlanList() {
            return planList;
        }

        public void setPlanList(List<Plan> planList) {
            this.planList = planList;
        }
    }

    public class Plan {
        private String time;
        private String content;
        private List<String> actionList = new ArrayList<>();

        public Plan(String time, String content) {
            this.time = time;
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getActionList() {
            return actionList;
        }

        public void setActionList(List<String> actionList) {
            this.actionList = actionList;
        }
    }
}
