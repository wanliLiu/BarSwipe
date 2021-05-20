package io.github.laucherish.purezhihud.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import io.github.laucherish.purezhihud.R;
import io.github.laucherish.purezhihud.base.BaseFragment;
import io.github.laucherish.purezhihud.bean.News;
import io.github.laucherish.purezhihud.network.manager.RetrofitManager;
import io.github.laucherish.purezhihud.ui.activity.AboutActivity;
import io.github.laucherish.purezhihud.ui.activity.NewsDetailActivity;
import io.github.laucherish.purezhihud.utils.HtmlUtil;
import io.github.laucherish.purezhihud.utils.L;
import io.github.laucherish.purezhihud.utils.PrefUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by laucherish on 16/3/17.
 */
public class NewsDetailFragment extends BaseFragment {

    @BindView(R.id.iv_header)
    ImageView mIvHeader;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_source)
    TextView mTvSource;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.wv_news)
    WebView mWvNews;
    @BindView(R.id.nested_view)
    NestedScrollView mNestedView;
    @BindView(R.id.tv_load_empty)
    TextView mTvLoadEmpty;
    @BindView(R.id.tv_load_error)
    TextView mTvLoadError;
    @BindView(R.id.pb_loading)
    ContentLoadingProgressBar mPbLoading;

    private News mNews;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mNews = getArguments().getParcelable(NewsDetailActivity.KEY_NEWS);
        setHasOptionsMenu(true);
        init();
        loadData();
    }

    public static Fragment newInstance(News news) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(NewsDetailActivity.KEY_NEWS, news);
        Fragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_action_share:
                share();
                return true;
            case R.id.menu_action_about:
                AboutActivity.start(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setTitleEnabled(true);
    }

    private void loadData() {
        RetrofitManager.builder().getNewsDetail(mNews.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showProgress())
                .subscribe(newsDetail -> {
                    hideProgress();
                    L.object(newsDetail);
                    if (newsDetail == null) {
                        mTvLoadEmpty.setVisibility(View.VISIBLE);
                    } else {
                        Glide.with(getActivity())
                                .load(newsDetail.getImage())
                                .into(mIvHeader);
                        mTvTitle.setText(newsDetail.getTitle());
                        mTvSource.setText(newsDetail.getImage_source());

                        boolean isNight = PrefUtil.isNight();
                        StringBuffer stringBuffer = HtmlUtil.handleHtml(newsDetail.getBody(), isNight);
                        mWvNews.setDrawingCacheEnabled(true);
                        mWvNews.loadDataWithBaseURL("file:///android_asset/", stringBuffer.toString(), "text/html", "utf-8", null);

//                            String htmlData = HtmlUtil.createHtmlData(newsDetail);
//                            mWvNews.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
                        mTvLoadEmpty.setVisibility(View.GONE);
                    }
                    mTvLoadError.setVisibility(View.GONE);
                }, throwable -> {
                    hideProgress();
                    L.e(throwable, "Load news detail error");
                    mTvLoadError.setVisibility(View.VISIBLE);
                    mTvLoadEmpty.setVisibility(View.GONE);
                });
    }

    public void showProgress() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mPbLoading.setVisibility(View.GONE);
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_from) + mNews.getTitle() + "，http://daily.zhihu.com/story/" + mNews.getId());
        startActivity(Intent.createChooser(intent, mNews.getTitle()));
    }

}
