package com.barswipe.retrofit;

import android.os.Bundle;
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
 */

public class StudyRetrofit extends BaseActivity {

    /**
     * 学习研究 地址：http://gank.io/post/56e80c2c677659311bed9841
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
                resultTV.setText(subjects.toString());
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
