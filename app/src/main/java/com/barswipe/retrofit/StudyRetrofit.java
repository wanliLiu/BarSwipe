package com.barswipe.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.retrofit.entity.Subject;
import com.barswipe.retrofit.http.HttpMethods;
import com.barswipe.retrofit.subscribers.ProgressSubscriber;
import com.barswipe.retrofit.subscribers.SubscriberOnNextListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Soli on 2016/9/27.
 * <p>
 * http://blog.csdn.net/jackiandroid/article/details/44564697
 */
public class StudyRetrofit extends BaseActivity {

    /**
     * 学习研究 地址：http://gank.io/post/56e80c2c677659311bed9841
     * retrofit study---------http://square.github.io/retrofit/
     */
    @Bind(R.id.click_me_BN)
    Button clickMeBN;
    @Bind(R.id.result_TV)
    clickTextview resultTV;

    private SubscriberOnNextListener getTopMovieOnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_retrofit);
        ButterKnife.bind(this);

        getTopMovieOnNext = new SubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {
//                resultTV.setText(subjects.toString());
                resultTV.append(subjects.toString());
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.click_me_BN)
    public void onClick() {
        getMovie();
    }

    @OnClick(R.id.test)
    public void onViewclick() {
        String temp = "aA我是010x1F602 我想问一下\uD83D\uDE33";
        char[] chars = temp.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            Log.e("chars", chars[i] + "");
        }
        toCodePointArray(temp);
        toCodePointArray_1(temp);
        char[] test = Character.toChars(65);
        for (int i = 0; i < test.length; i++) {
            Log.e("chars", test[i] + "");
        }
    }

    private int[] toCodePointArray(String str) { // Example 1-1
        int len = str.length();          // the length of str
        int[] acp = new int[len];        // an array of code points

        for (int i = 0, j = 0; i < len; i++) {
            acp[j++] = str.charAt(i);
        }
        return acp;
    }

    private int[] toCodePointArray_1(String str) { // Example 1-2
        int len = str.length();          // the length of str
        int[] acp;                       // an array of code points
        int surrogatePairCount = 0;      // the count of surrogate pairs

        for (int i = 1; i < len; i++) {
            if (Character.isSurrogatePair(str.charAt(i - 1), str.charAt(i))) {
                surrogatePairCount++;
                i++;
            }
        }
        acp = new int[len - surrogatePairCount];
        for (int i = 0, j = 0; i < len; i++) {
            char ch0 = str.charAt(i);         // the current char
            if (Character.isHighSurrogate(ch0) && i + 1 < len) {
                char ch1 = str.charAt(i + 1); // the next char
                if (Character.isLowSurrogate(ch1)) {
                    acp[j++] = Character.toCodePoint(ch0, ch1);
                    i++;
                    continue;
                }
            }
            acp[j++] = ch0;
        }
        return acp;
    }

    //进行网络请求
    private void getMovie() {

        HttpMethods.getInstance().getTopMovie(new ProgressSubscriber(getTopMovieOnNext, this), 0, 10);

//        String baseUrl = "https://api.douban.com/v2/movie/";
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        MovieService movieService = retrofit.create(MovieService.class);
//        Call<MovieEntity> call = movieService.getTopMovie(0, 10);
//        call.enqueue(new Callback<MovieEntity>() {
//            @Override
//            public void onResponse(Call<MovieEntity> call, Response<MovieEntity> response) {
//                resultTV.setText(JSON.toJSONString(response.body()));
//            }
//
//            @Override
//            public void onFailure(Call<MovieEntity> call, Throwable t) {
//                resultTV.setText(t.getMessage());
//            }
//        });

        /******************************************
         * 用Rxjava 主要是用addCallAdapterFactory
         * *****************************************/
//        String baseUrl = "https://api.douban.com/v2/movie/";
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        MovieService movieService = retrofit.create(MovieService.class);
//
//        movieService.getTopMovie12(0, 10)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<MovieEntity>() {
//                    @Override
//                    public void onCompleted() {
//                        Toast.makeText(StudyRetrofit.this, "Get Top Movie Completed", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        resultTV.setText(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(MovieEntity movieEntity) {
//                        resultTV.setText(movieEntity.toString());
//                    }
//                });
    }
}
