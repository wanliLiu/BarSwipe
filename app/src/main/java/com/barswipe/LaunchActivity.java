package com.barswipe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.barswipe.ExpandableTextView.ExpandableTextView;
import com.barswipe.FloatView.FloatWindowService;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by soli on 6/2/16.
 */
public class LaunchActivity extends RxAppCompatActivity {

    final String Tag = "Rxjava学习";

    private ListView listView;

    private activityListAdapter adapter;

    private Observable<Long> defer, just, interval;
    private Subscriber<Long> intervalSubscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        defer = DeferObserver();
        just = JustObserver();
        interval = IntervalObserver();
        intervalSubscriber = getTntervalSubscriber("Rxjava学习");

        studyPressionsRequest();

        listView = (ListView) findViewById(R.id.lis);


        adapter = new activityListAdapter();


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ActivityInfo info = adapter.getItem(position);
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), info.name);
//                startActivity(intent);
//            }
//        });

        RxAdapterView.itemClicks(listView)
                .compose(this.<Integer>bindToLifecycle())
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        switch (position) {
                            case 0:
                                RxJavaCreatingObservables();
                                break;
                            case 1:
                                RxJavaTransformingObservables();
                                break;
                            case 2:
                                RxJavaFilteringObservables();
                                break;
                            case 3:
                                RxJavaCombiningObservables();
                                break;
                        }
                        ActivityInfo info = adapter.getItem(position);
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), info.name);
                        startActivity(intent);
                    }
                });

        adapter.setList(getAllActivityLists());
        listView.setAdapter(adapter);


        MakeItRunOnUIThread();

    }

    /**
     *借鉴
     * http://www.open-open.com/lib/view/open1450578678148.html
     * http://www.cnblogs.com/cr330326/p/5181283.html
     *
     */
    private void studyPressionsRequest() {

//        RxPermissions.getInstance(this)
//                .request("android.permission.SYSTEM_ALERT_WINDOW")
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (aBoolean)
//                        {
//                            Intent intent = new Intent(LaunchActivity.this, FloatWindowService.class);
//                            startService(intent);
//                        }else {
//                            Toast.makeText(LaunchActivity.this,"android.permission.SYSTEM_ALERT_WINDOW",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


        Intent intent = new Intent(LaunchActivity.this, FloatWindowService.class);
        startService(intent);
    }

    /**
     * 运行于ui线程
     */
    private void MakeItRunOnUIThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {

        } else {
            AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                @Override
                public void call() {

                }
            });
        }
    }

    /**
     * @return
     */
    private List<ActivityInfo> getAllActivityLists() {
        List<ActivityInfo> list = new ArrayList<>();

        try {

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);

            ActivityInfo[] info = packageInfo.activities;

            for (int i = info.length - 1; info != null && i >= 0; i--) {
                if (!TextUtils.isEmpty(info[i].nonLocalizedLabel)) {
                    list.add(info[i]);
                }
            }
        } catch (Exception e) {
        }

        return list;
    }

    /**
     * @param index
     * @return
     */
    private Observable<Integer> createObserver(final int index) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i < 6; i++) {
                    subscriber.onNext(i * index);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    /**
     * RxJava操作符（十）自定义操作符-------------http://mushuichuan.com/2016/02/05/rxjava-operator-10/
     * http://blog.chinaunix.net/uid-20771867-id-5197584.html
     */
    private void RxJavaCombiningObservables() {

        //CombineLatest
        //满足条件1的时候任何一个Observable发射一个数据，就将所有Observable最新发射的数据按照提供的函数组装起来发射出去。
        Observable.combineLatest(createObserver(1), createObserver(2), new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer num1, Integer num2) {
                Log.e(Tag, "combineLatest--call---left:" + num1 + " right:" + num2);
                return num1 + num2;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag, "combineLatest--result:" + integer);
            }
        });

        List<Observable<Integer>> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            list.add(createObserver(i));
        }
        Observable.combineLatest(list, new FuncN<Integer>() {
            @Override
            public Integer call(Object... args) {
                int temp = 0;
                for (Object i : args) {
                    Log.e(Tag, "combineLatest--list:" + i);
                    temp += (Integer) i;
                }
                return temp;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag, "combineLatest--list--result:" + integer);
            }
        });

        //join
        Observable.just("Left-").join(
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        for (int i = 1; i < 5; i++) {
                            subscriber.onNext("Right-" + i);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.newThread()),
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String integer) {
                        return Observable.timer(3000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String Long) {
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String, String, String>() {
                    @Override
                    public String call(String left, String Right) {
                        return left + Right;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag, "join:" + s);
                    }
                });

        //groupJoin
        Observable.just("Left-").groupJoin(
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        for (int i = 1; i < 5; i++) {
                            subscriber.onNext("Right-" + i);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.newThread()),
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String integer) {
                        return Observable.timer(3000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String Long) {
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String, Observable<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String sB, Observable<String> stringObservable) {
                        return stringObservable.map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return sB + s;
                            }
                        });
                    }
                })
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.e(Tag, "groupJoin:" + s);
                            }
                        });
                    }
                });

        //concat
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag, "concat-:" + integer);
            }
        });

        //Merge
        Observable.merge(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag, "merge-:" + integer);
            }
        });

        //mergeDelayError
        Observable.mergeDelayError(
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for (int i = 0; i < 5; i++) {
                            if (i == 3) {
                                subscriber.onError(new Throwable("error"));
                            }
                            subscriber.onNext(i);
                        }
                    }
                }),
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for (int i = 0; i < 5; i++) {
                            subscriber.onNext(5 + i);
                        }
                        subscriber.onCompleted();
                    }
                }))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(Tag, "mergeDelayError-:" + integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(Tag, "mergeDelayError-:" + throwable.getMessage());
                    }
                });


    }

    /**
     * http://blog.chinaunix.net/uid-20771867-id-5194384.html
     */
    private void RxJavaFilteringObservables() {

        //throttleWithTimeOut
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

                for (int i = 0; i < 18; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(i);
                    }

                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
//                .debounce(200,TimeUnit.MILLISECONDS)
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(Tag, "throttleWithTimeout-" + String.valueOf(integer));
                    }
                });


        //debounce
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//                .debounce(200, TimeUnit.MILLISECONDS)
                .debounce(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(final Integer integer) {
                        Log.e(Tag, "debounce-过滤前" + String.valueOf(integer));
                        return Observable.create(new Observable.OnSubscribe<Integer>() {
                            @Override
                            public void call(Subscriber<? super Integer> subscriber) {
                                Log.e(Tag, "debounce-complete:" + String.valueOf(integer));
                                if (integer % 2 == 0 && !subscriber.isUnsubscribed()) {
                                    subscriber.onNext(integer);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(Tag, "debounce-结果:" + String.valueOf(integer));
                    }
                });
    }

    /**
     * http://blog.chinaunix.net/uid-20771867-id-5192193.html
     */
    private void RxJavaTransformingObservables() {

        //Buffer map flatmap flatmapIterable
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return Observable.just(integer + 1);
                    }
                })
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer + 1;
                    }
                })
                .flatMapIterable(new Func1<Integer, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> call(Integer integer) {
                        ArrayList<Integer> s = new ArrayList<>();
                        for (int i = 0; i < integer; i++) {
                            s.add(i);
                        }

                        return s;
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                })
//                .subscribe(new Action1<Integer>() {
//                    @Override
//                    public void call(Integer integer) {
//                        Log.e(Tag + "buffer", integer + "");
//                    }
//                });
                .buffer(5)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Log.e(Tag + "buffer", integers.toString());
                    }
                });

        Observable.interval(300, TimeUnit.MILLISECONDS)
//                .compose(this.<Long>bindToLifecycle())
                .buffer(3, TimeUnit.SECONDS)
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> longs) {
                        Log.e(Tag + "bufferTime", longs.toString());
                    }
                });

        Observable.interval(300, TimeUnit.MILLISECONDS)
//                .compose(this.<Long>bindToLifecycle())
                .window(3, TimeUnit.SECONDS)
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> longObservable) {
                        Log.e(Tag + "window 集合开始", longObservable.toString());
                        longObservable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Log.e(Tag + "window", aLong + "");
                            }
                        });
                    }
                });

        //GroupBy
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .groupBy(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer % 2;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Integer, Integer>>() {
                    @Override
                    public void call(final GroupedObservable<Integer, Integer> groupedObservable) {
                        groupedObservable.count().subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.e(Tag + "groupBy", "key-" + groupedObservable.getKey() + ",contains:" + integer);
                            }
                        });
                    }
                });
//                .subscribe(new Subscriber<GroupedObservable<Integer, Integer>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(final GroupedObservable<Integer, Integer> groupedObservable) {
//                        groupedObservable.count().subscribe(new Action1<Integer>() {
//                            @Override
//                            public void call(Integer integer) {
//                                Log.e(Tag + "groupBy", "key-" + groupedObservable.getKey() + "contains:" + integer);
//                            }
//                        });
//
//                    }
//                });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .groupBy(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer % 2;
                    }
                }, new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "groupByKeyValue:" + integer;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Integer, String>>() {
                    @Override
                    public void call(GroupedObservable<Integer, String> integerStringGroupedObservable) {
                        if (integerStringGroupedObservable.getKey() == 0) {
                            integerStringGroupedObservable.subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    Log.e(Tag + "groupBy", s);
                                }
                            });
                        }
                    }
                });

        //cast Cast将Observable发射的数据强制转化为另外一种类型，属于Map的一种具体的实现
        Observable.just(getAnimal()).cast(Dog.class).subscribe(new Action1<Dog>() {
            @Override
            public void call(Dog dog) {
                log("Cast:" + dog.getName());
            }
        });


        //Scan
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        Observable.from(list).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer x, Integer y) {
                return x * y;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag, "scan---result:" + integer);
            }
        });

    }

    private Animal getAnimal() {
        return new Dog();
    }

    /**
     * @param tag
     */
    private void log(String tag) {
        Log.e("Rxjava学习" + "cast", tag);
    }


    private class Animal {
        protected String name = "Animal";

        Animal() {
            log("create " + name);
        }

        String getName() {
            return name;
        }
    }

    private class Dog extends Animal {
        Dog() {
            name = getClass().getSimpleName();
            log("create " + name);
        }
    }

    /**
     * Rxjava学习
     * http://blog.chinaunix.net/uid-20771867-id-5187376.html
     */
    private void RxJavaCreatingObservables() {

        //Map
        Observable.just("Hellp Map Operator")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return 156;
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "Secon" + String.valueOf(integer);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag, "----Map--doOnNext-" + s);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag, "----Map--subscribe-" + s);
                    }
                });

        //From  一个一个发送出去
        List<String> s = Arrays.asList("Java", "Android", "Ruby", "Ios", "Swift");
        Observable.from(s)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag + "----From", s);
                    }
                });

        //FlatMap
        Observable.just(s)
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String s) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext("addpre_" + s);
                                    subscriber.onCompleted();
                                }
                            }
                        });
//                        return Observable.just("addpre_" + s);
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.contains("a");
                    }
                })
                .take(3)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag + "----FlatMap", s);
                    }
                });
        //just more  将整个对象一起全部发送出去
        Observable.just("23", 233)
                .filter(new Func1<Serializable, Boolean>() {
                    @Override
                    public Boolean call(Serializable serializable) {
                        Object tes = (Object) serializable;
                        if (tes != null && tes instanceof String)
                            return true;
                        else
                            return false;
                    }
                })
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e(Tag + "----just-more", serializable.toString());
                    }
                });

        //Create
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    for (int i = 0; i < 5; i++) {
                        int temp = new Random().nextInt(10);
                        if (temp > 8) {
                            //if value>8, we make an error
                            subscriber.onError(new Throwable("value >8"));
                            break;
                        } else {
                            subscriber.onNext(temp);
                        }
                        // on error,complete the job
                        if (i == 4) {
                            subscriber.onCompleted();
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.e(Tag, "----create---onNext-" + item + "");
            }

            @Override
            public void onError(Throwable error) {
                Log.e(Tag, "--create--onError-" + error.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.e(Tag, "----create--onCompleted onCompleted");
            }
        });

        //range
        Observable.range(23, 10).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(Tag + "----range", String.valueOf(integer));
            }
        });

        //defer  repeat
        deferJust(Tag);

        //Interval
        interval.subscribe(intervalSubscriber);

        //Timer
        Observable.timer(1, TimeUnit.SECONDS)
                .delay(4, TimeUnit.SECONDS)
                .repeat(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(Tag + "----timer", String.valueOf(aLong));
                    }
                });
    }

    /**
     * @param Tag
     * @return
     */
    private Subscriber<Long> getTntervalSubscriber(final String Tag) {
        return new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.e(Tag + "----Interval", String.valueOf(aLong));
            }
        };
    }

    /**
     * @return
     */
    private Observable<Long> DeferObserver() {
        return Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                return Observable.just(System.currentTimeMillis());
            }
        }).delay(3, TimeUnit.SECONDS).repeat(10);
    }

    /**
     * @return
     */
    private Observable<Long> JustObserver() {
        return Observable.just(System.currentTimeMillis())
//                .compose(this.<Long>bindToLifecycle())
                .delay(3, TimeUnit.SECONDS).repeat(10);
    }

    /**
     * @return
     */
    private Observable<Long> IntervalObserver() {
        return Observable.interval(1, TimeUnit.SECONDS)
//                .compose(this.<Long>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     *
     */
    private class activityListAdapter extends BaseAdapter {

        private final SparseBooleanArray mCollapsedStatus;
        private List<ActivityInfo> list;

        public activityListAdapter() {
            mCollapsedStatus = new SparseBooleanArray();

        }

        public void setList(List<ActivityInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public ActivityInfo getItem(int position) {
            return list != null ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(LaunchActivity.this, R.layout.activity_list_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ActivityInfo info = getItem(position);
            holder.icon.setImageResource(info.icon);
            holder.expand_text_view.setText(info.nonLocalizedLabel + "\n" + getResources().getString(info.descriptionRes), mCollapsedStatus, position);

            return convertView;
        }


        private class ViewHolder {
            private ImageView icon;
            private ExpandableTextView expand_text_view;

            public ViewHolder(View view) {
                icon = (ImageView) view.findViewById(R.id.icon);
                expand_text_view = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
                view.setTag(this);
            }
        }
    }

    /**
     * @param Tag
     */
    private void deferJust(final String Tag) {
        //defer
        defer.subscribe(new Action1<Long>() {
            @Override
            public void call(Long t) {
                Log.e(Tag + "----defer-repeat", String.valueOf(t));
            }
        });
        just.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(Tag + "----defer-just-repeat", String.valueOf(aLong));
            }
        });
    }

    /**
     * @param test
     */
    public void onEvent(NotificationEvent test) {
//        deferJust(test.event);
        if (intervalSubscriber != null)
            intervalSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
