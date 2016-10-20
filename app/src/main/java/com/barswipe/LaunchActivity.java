package com.barswipe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by soli on 6/2/16.
 */
public class LaunchActivity extends AppCompatActivity {

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

        Intent intent = new Intent(this, FloatWindowService.class);
        startService(intent);

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
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        if (position == 0) {
                            RxJavaStudy();
                        }
                        ActivityInfo info = adapter.getItem(position);
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), info.name);
                        startActivity(intent);
                    }
                });

        adapter.setList(getAllActivityLists());
        listView.setAdapter(adapter);
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
     * Rxjava学习
     * http://www.jianshu.com/p/88779bda6691
     * http://blog.csdn.net/lzyzsd/article/details/44094895
     * http://blog.chinaunix.net/uid-20771867-id-5187376.html
     */
    private void RxJavaStudy() {
        final String Tag = "Rxjava学习";
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
                        Log.e(Tag + "----Map--doOnNext", s);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(Tag + "----Map--subscribe", s);
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
                    public Observable<String> call(String s) {
                        return Observable.just("addpre_" + s);
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
                Log.e(Tag + "----create---onNext", item + "");
            }

            @Override
            public void onError(Throwable error) {
                Log.e(Tag + "----create---onError", error.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.e(Tag + "----create--onCompleted", "onCompleted");
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
                .delay(4,TimeUnit.SECONDS)
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
                .delay(3, TimeUnit.SECONDS).repeat(10);
    }

    /**
     * @return
     */
    private Observable<Long> IntervalObserver() {
        return Observable.interval(1, TimeUnit.SECONDS)
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
