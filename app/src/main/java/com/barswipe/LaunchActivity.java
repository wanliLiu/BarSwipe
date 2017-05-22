package com.barswipe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.barswipe.ExpandableTextView.ExpandableTextView;
import com.barswipe.FloatView.FloatWindowService;
import com.barswipe.jni.Jnidemo;
import com.barswipe.model.DataBaseManager;
import com.barswipe.model.Student;
import com.barswipe.volume.wave.HeadPhonesRecivier;
import com.example.LibJavaTest;
import com.example.libraryandroid.LibTestAndroid;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//import com.soli.jnistudy.JniTest;

/**
 * Created by soli on 6/2/16.
 */
public class LaunchActivity extends BaseActivity {

    final String Tag = "Rxjava学习";

    private ListView listView;

    private activityListAdapter adapter;

    private Observable<Long> defer, just, interval;
    private Observer<Long> intervalSubscriber;
    private Disposable disposable;

    private boolean isClipToPadding = false;
    private HeadPhonesRecivier recivier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        setSwipeBackEnable(false);

        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Android 学习汇总");

        defer = DeferObserver();
        just = JustObserver();
        interval = IntervalObserver();
        intervalSubscriber = getTntervalSubscriber("Rxjava学习");

        studySpecialGrantPermisson();

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
                .compose(bindToLifecycle())
                .throttleFirst(ViewConfiguration.getDoubleTapTimeout(), TimeUnit.MILLISECONDS)
                .subscribe(position -> RxPermissions.getInstance(LaunchActivity.this)
                        .request(
                                Manifest.permission.CAMERA,
                                Manifest.permission.SYSTEM_ALERT_WINDOW,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.BODY_SENSORS,
                                Manifest.permission.WRITE_CALENDAR,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_SETTINGS
                        )
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                dealAciont(position);
                            } else
                                Toast.makeText(LaunchActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                        }));

        adapter.setList(getAllActivityLists());
        listView.setAdapter(adapter);


        MakeItRunOnUIThread();

        dbflowDest();

        Log.e("Jni测试", "Jni测试--from-app---" + Jnidemo.getStringFromJni());
//        Log.e("Jni测试", "Jni测试--from-jnistudy---" + JniTest.getStringFromJni());

        Log.e("byte大端小段模式", ByteOrder.nativeOrder().toString());

        findAudioRecord();

        registerReciver();

        testStream();

        libSdkTest();
    }

    /**
     *
     */
    private void libSdkTest() {
        Log.e("libTestJava", new LibJavaTest().getStringFromLib());

        LibTestAndroid test = new LibTestAndroid();
        test.toast(this, "用android library来用");
        Log.e("libTestAndroid", test.testOtherLib());
    }

    /**
     *
     */
    private void testStream() {
        String[] names = new String[]{
                "Amy", "Alex", "Bob", "Cindy", "Jeff", "Jack",
                "Sunny", "Sara", "Steven"
        };

        //筛选S开头的人名
        List<String> data = Stream.of(names)
                .filter(name -> name.startsWith("s"))
                .collect(Collectors.toList());

        Log.e("Stream", data.toString());

        //按首字母分组并排序
        List<Map.Entry<String, List<String>>> group = Stream.of(names)
                .groupBy(name -> String.valueOf(name.charAt(0)))
                .sortBy(Map.Entry::getKey)
                .collect(Collectors.toList());
        Log.e("Stream", group.toString());
    }

    /**
     *
     */
    private void registerReciver() {
        recivier = new HeadPhonesRecivier();
        IntentFilter filter = new IntentFilter();
        filter.addAction(recivier.HeadSetAction);
        filter.addAction(recivier.BluetoothHeadSet);
        filter.addAction(recivier.CallPhoneAction);
        filter.addAction(recivier.PhoneStateAction);
        filter.addAction(recivier.AnotherAction);

        registerReceiver(recivier, filter);
    }


    /**
     * @return
     */
    public void findAudioRecord() {
        int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                Log.e("合适的频率", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                            }

                        }
                    } catch (Exception e) {
                        Log.e("合适的频率", rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
    }

    /**
     * dbflow 测试
     */
    private void dbflowDest() {
        DataBaseManager manager = new DataBaseManager();
        Student s1 = new Student();
        s1.name = "hello";
        s1.age = new Random().nextInt(30);
        manager.insertData(s1.name, s1.age);

        List<Student> list = manager.getData();
        if (list != null) {
            for (Student temp : list) {
                Log.e("dbflow 里的数据", temp.toString());
            }
        }
    }

    /**
     * @param position
     */
    private void dealAciont(int position) {

        listView.setClipToPadding(isClipToPadding = !isClipToPadding);

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

    /**
     *
     */
    private void shotFloatView() {
        Intent intent = new Intent(LaunchActivity.this, FloatWindowService.class);
        startService(intent);
    }

    /**
     * 借鉴
     * http://www.open-open.com/lib/view/open1450578678148.html
     * http://www.cnblogs.com/cr330326/p/5181283.html
     * “android.permission.SYSTEM_ALERT_WINDOW”
     * “android.permission.WRITE_SETTINGS”
     */
    private void studySpecialGrantPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this))
                shotFloatView();
            else
                showRationaleDialog("需要开启必要的访问权限");
        } else {
            shotFloatView();
        }
    }

    /**
     * @param messageResId
     */
    private void showRationaleDialog(String messageResId) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        RxPermissions.getInstance(LaunchActivity.this)
                                .setLogging(true)
                                .request(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.BODY_SENSORS,
                                        Manifest.permission.WRITE_CALENDAR,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.WRITE_CONTACTS,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.WRITE_SETTINGS)
                                .subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        shotFloatView();
                                    } else {
                                        showRationaleDialog("需要开启必要的访问权限");
                                    }
                                });
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> finish())
                .setTitle("帮助")
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    /**
     * 运行于ui线程
     */
    private void MakeItRunOnUIThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {

        } else {
            AndroidSchedulers.mainThread().createWorker().schedule(() -> {

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
        return Observable.create((ObservableOnSubscribe<Integer>) subscriber -> {
            for (int i = 1; i < 6; i++) {
                subscriber.onNext(i * index);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            subscriber.onComplete();
        }).subscribeOn(Schedulers.newThread());
    }

    /**
     * RxJava操作符（十）自定义操作符-------------http://mushuichuan.com/2016/02/05/rxjava-operator-10/
     * http://blog.chinaunix.net/uid-20771867-id-5197584.html
     */
    private void RxJavaCombiningObservables() {

        //CombineLatest
        //满足条件1的时候任何一个Observable发射一个数据，就将所有Observable最新发射的数据按照提供的函数组装起来发射出去。
        Observable.combineLatest(createObserver(1), createObserver(2), (num1, num2) -> {
            Log.e(Tag, "combineLatest--call---left:" + num1 + " right:" + num2);
            return num1 + num2;
        }).subscribe(integer -> Log.e(Tag, "combineLatest--result:" + integer));

        List<Observable<Integer>> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            list.add(createObserver(i));
        }
        Observable.combineLatest(list, args -> {
            int temp = 0;
            for (Object i : args) {
                Log.e(Tag, "combineLatest--list:" + i);
                temp += (Integer) i;
            }
            return temp;
        }).subscribe(integer -> Log.e(Tag, "combineLatest--list--result:" + integer));

        //join
        Observable.just("Left-").join(
                Observable.create((ObservableOnSubscribe<String>) subscriber -> {
                    for (int i = 1; i < 5; i++) {
                        subscriber.onNext("Right-" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriber.onComplete();
                }).subscribeOn(Schedulers.newThread()),
                s -> Observable.interval(3000, TimeUnit.MILLISECONDS),
                tRight -> Observable.interval(2000, TimeUnit.MILLISECONDS),
                (left, Right) -> left + Right)
                .subscribe(s -> Log.e(Tag, "join:" + s));

        //groupJoin
        Observable.just("Left-").groupJoin(
                Observable.create((ObservableOnSubscribe<String>) subscriber -> {
                    for (int i = 1; i < 5; i++) {
                        subscriber.onNext("Right-" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriber.onComplete();
                }).subscribeOn(Schedulers.newThread()),
                s -> Observable.interval(3000, TimeUnit.MILLISECONDS),
                s -> Observable.timer(2000, TimeUnit.MILLISECONDS),
                (s, stringObservable) -> stringObservable.map(s1 -> s + s1))
                .subscribe(stringObservable -> stringObservable.subscribe(s -> Log.e(Tag, "groupJoin:" + s)));


        //concat
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(integer -> Log.e(Tag, "concat-:" + integer));

        //Merge
        Observable.merge(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(integer -> Log.e(Tag, "merge-:" + integer));

        //mergeDelayError
        Observable.mergeDelayError(
                Observable.create((ObservableOnSubscribe<Integer>) subscriber -> {
                    for (int i = 0; i < 5; i++) {
                        if (i == 3) {
                            subscriber.onError(new Throwable("error"));
                        }
                        subscriber.onNext(i);
                    }
                }),
                Observable.create(subscriber -> {
                    for (int i = 0; i < 5; i++) {
                        subscriber.onNext(5 + i);
                    }
                    subscriber.onComplete();
                }))
                .subscribe(integer -> Log.e(Tag, "mergeDelayError-:" + integer), throwable -> Log.e(Tag, "mergeDelayError-:" + throwable.getMessage()));


    }

    /**
     * http://blog.chinaunix.net/uid-20771867-id-5194384.html
     */
    private void RxJavaFilteringObservables() {

        //throttleWithTimeOut
        Observable.create((ObservableOnSubscribe<Integer>) subscriber -> {
            for (int i = 0; i < 18; i++) {
                if (!subscriber.isDisposed()) {
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
            subscriber.onComplete();
        }).subscribeOn(Schedulers.computation())
//                .debounce(200,TimeUnit.MILLISECONDS)
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.e(Tag, "throttleWithTimeout-" + String.valueOf(integer)));


        //debounce
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//                .debounce(200, TimeUnit.MILLISECONDS)
                .debounce(integer -> {
                    Log.e(Tag, "debounce-过滤前" + String.valueOf(integer));
                    return Observable.create(subscriber -> {
                        Log.e(Tag, "debounce-complete:" + String.valueOf(integer));
                        if (integer % 2 == 0 && !subscriber.isDisposed()) {
                            subscriber.onNext(integer);
                            subscriber.onComplete();
                        }
                    });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.e(Tag, "debounce-结果:" + String.valueOf(integer)));
    }

    /**
     * http://blog.chinaunix.net/uid-20771867-id-5192193.html
     */
    private void RxJavaTransformingObservables() {

        //Buffer map flatmap flatmapIterable
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .flatMap(integer -> Observable.just(integer + 1))
                .map(integer -> integer + 1)
                .flatMapIterable(integer -> {
                    ArrayList<Integer> s = new ArrayList<>();
                    for (int i = 0; i < integer; i++) {
                        s.add(i);
                    }
                    return s;
                })
                .filter(integer -> integer % 2 == 0)
//                .subscribe(new Action1<Integer>() {
//                    @Override
//                    public void call(Integer integer) {
//                        Log.e(Tag + "buffer", integer + "");
//                    }
//                });
                .buffer(5)
                .subscribe(integers -> Log.e(Tag + "buffer", integers.toString()));

        Observable.interval(300, TimeUnit.MILLISECONDS)
//                .compose(this.<Long>bindToLifecycle())
                .buffer(3, TimeUnit.SECONDS)
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longs -> Log.e(Tag + "bufferTime", longs.toString()));

        Observable.interval(300, TimeUnit.MILLISECONDS)
//                .compose(this.<Long>bindToLifecycle())
                .window(3, TimeUnit.SECONDS)
                .take(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longObservable -> {
                    Log.e(Tag + "window 集合开始", longObservable.toString());
                    longObservable.subscribe(aLong -> Log.e(Tag + "window", aLong + ""));
                });

        //GroupBy
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .groupBy(integer -> integer % 2)
                .subscribe(groupedObservable -> groupedObservable.count().subscribe(data -> Log.e(Tag + "groupBy", "key-" + groupedObservable.getKey() + ",contains:" + data)));
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
                .groupBy(integer -> integer % 2, integer -> "groupByKeyValue:" + integer)
                .subscribe(integerStringGroupedObservable -> {
                    if (integerStringGroupedObservable.getKey() == 0) {
                        integerStringGroupedObservable.subscribe(s -> Log.e(Tag + "groupBy", s));
                    }
                });

        //cast Cast将Observable发射的数据强制转化为另外一种类型，属于Map的一种具体的实现
        Observable.just(getAnimal()).cast(Dog.class).subscribe(dog -> log("Cast:" + dog.getName()));


        //Scan
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        Observable.fromIterable(list)
                .scan((x, y) -> x * y)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.e(Tag, "scan---result:" + integer));

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
                .map(s -> 156)
                .map(integer -> "Secon" + String.valueOf(integer))
                .doOnNext(s -> Log.e(Tag, "----Map--doOnNext-" + s))
                .subscribe(s -> Log.e(Tag, "----Map--subscribe-" + s));

        //From  一个一个发送出去
        List<String> data = Arrays.asList("Java", "Android", "Ruby", "Ios", "Swift");
        Observable.fromIterable(data)
                .subscribe(s1 -> Log.e(Tag + "----From", s1));

        //FlatMap
        Observable.just(data)
                .flatMap(strings -> Observable.fromIterable(strings))
                .flatMap(s1 -> Observable.create((ObservableOnSubscribe<String>) subscriber -> {
                    if (!subscriber.isDisposed()) {
                        subscriber.onNext("addpre_" + s1);
                        subscriber.onComplete();
                    }
                }))
                .filter(s12 -> s12.contains("a"))
                .take(3)
                .subscribe(str -> Log.e(Tag + "----FlatMap", str));
        //just more  将整个对象一起全部发送出去
        Observable.just("23", 233)
                .filter(serializable -> {
                    Object tes = (Object) serializable;
                    if (tes != null && tes instanceof String)
                        return true;
                    else
                        return false;
                })
                .subscribe(serializable -> Log.e(Tag + "----just-more", serializable.toString()));

        //Create
        Observable.create((ObservableOnSubscribe<Integer>) subscriber -> {
            if (!subscriber.isDisposed()) {
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
                        subscriber.onComplete();
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
                        Log.e(Tag, "----create---onNext-" + integer + "");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable error) {
                        Log.e(Tag, "--create--onError-" + error.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(Tag, "----create--onCompleted onCompleted");
                    }
                });

        //range
        Observable.range(23, 10).subscribe(range -> Log.e(Tag + "----range", String.valueOf(range)));

        //defer  repeat
        deferJust(Tag);

        //Interval
        interval.subscribe(intervalSubscriber);

        //Timer
        Observable.timer(1, TimeUnit.SECONDS)
                .delay(4, TimeUnit.SECONDS)
                .repeat(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> Log.e(Tag + "----timer", String.valueOf(aLong)));
    }

    /**
     * @param Tag
     * @return
     */
    private Observer<Long> getTntervalSubscriber(final String Tag) {
        return new Observer<Long>() {

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                disposable = d;
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
        return Observable.defer(() -> Observable.just(System.currentTimeMillis())).delay(3, TimeUnit.SECONDS).repeat(10);
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
        defer.subscribe(t -> Log.e(Tag + "----defer-repeat", String.valueOf(t)));
        just.subscribe(aLong -> Log.e(Tag + "----defer-just-repeat", String.valueOf(aLong)));
    }

    /**
     * @param test
     */
    @Subscribe
    public void onEvent(NotificationEvent test) {
//        deferJust(test.event);
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (recivier != null)
            unregisterReceiver(recivier);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

}
