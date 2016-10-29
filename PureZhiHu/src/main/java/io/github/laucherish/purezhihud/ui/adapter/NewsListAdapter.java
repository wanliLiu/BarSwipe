package io.github.laucherish.purezhihud.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.laucherish.purezhihud.R;
import io.github.laucherish.purezhihud.bean.News;
import io.github.laucherish.purezhihud.db.dao.NewDao;
import io.github.laucherish.purezhihud.ui.activity.NewsDetailActivity;
import io.github.laucherish.purezhihud.utils.DateUtil;
import io.github.laucherish.purezhihud.utils.PrefUtil;

/**
 * Created by laucherish on 16/3/16.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private static final int ITEM_NEWS = 0;
    private static final int ITEM_NEWS_DATE = 1;

    private Context mContext;
    private List<News> mNewsList;
    private long lastPos = -1;
    private NewDao newDao;
    private boolean isAnim = true;
    private boolean isNight = PrefUtil.isNight();

    public NewsListAdapter(Context context, List<News> newsList) {
        this.mContext = context;
        this.mNewsList = newsList;
        this.newDao = new NewDao(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_NEWS;
        }
        String currentDate = mNewsList.get(position).getDate();
        int preIndex = position - 1;
        boolean isDifferent = !mNewsList.get(preIndex).getDate().equals(currentDate);
        return isDifferent ? ITEM_NEWS_DATE : ITEM_NEWS;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_NEWS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_list, parent, false);
            return new NewsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_list_date, parent, false);
            return new NewsDateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        final News news = mNewsList.get(position);
        if (news == null) {
            return;
        }
        if (holder instanceof NewsDateViewHolder) {
            NewsDateViewHolder dateHolder = (NewsDateViewHolder) holder;
            String dateFormat = null;
            dateFormat = DateUtil.formatDate(news.getDate());
            dateHolder.mTvNewsDate.setText(dateFormat);
            if (!isNight) {
                dateHolder.mTvNewsDate.setTextColor(ContextCompat.getColor(mContext, R.color.textColorSecond_Day));
            } else {
                dateHolder.mTvNewsDate.setTextColor(ContextCompat.getColor(mContext, R.color.textColorSecond_Night));
            }
            bindViewHolder(dateHolder, position, news);
        } else {
            bindViewHolder(holder, position, news);
        }
    }

    private void bindViewHolder(final NewsViewHolder holder, int position, final News news) {
        holder.mTvTitle.setText(news.getTitle());
        List<String> images = news.getImages();
        if (images != null && images.size() > 0) {
            Glide.with(mContext).load(images.get(0)).placeholder(R.drawable.ic_placeholder).into(holder.mIvNews);
        }
        if (!isNight) {
            if (!news.isRead()) {
                holder.mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.textColorFirst_Day));
            } else {
                holder.mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.textColorThird_Day));
            }
        } else {
            if (!news.isRead()) {
                holder.mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.textColorFirst_Night));
            } else {
                holder.mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.textColorThird_Night));
            }
        }

        holder.mCvItem.setOnClickListener(getListener(holder, news));

//        if (holder instanceof NewsDateViewHolder) {
//            ((NewsDateViewHolder) holder).mCvItem.setOnClickListener(getListener(holder, news));
//        } else {
//            holder.itemView.setOnClickListener(getListener(holder, news));
//        }
        if (isAnim) {
            startAnimator(holder.mCvItem, position);
        }
    }

    @NonNull
    private View.OnClickListener getListener(final NewsViewHolder holder, final News news) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!news.isRead()) {
                    news.setRead(true);
                    holder.mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.color_read));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            newDao.insertReadNew(news.getId() + "");
                        }
                    }).start();
                }
                NewsDetailActivity.start(mContext, news);
            }
        };
    }

    @Override
    public int getItemCount() {
        return mNewsList == null ? 0 : mNewsList.size();
    }

    private void startAnimator(View view, long position) {
        if (position > lastPos) {
            view.startAnimation(AnimationUtils.loadAnimation(this.mContext, R.anim.item_bottom_in));
            lastPos = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(NewsViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mCvItem.clearAnimation();
    }

    public void changeData(List<News> newsList) {
        mNewsList = newsList;
        notifyDataSetChanged();
    }

    public void addData(List<News> newsList) {
        if (mNewsList == null) {
            changeData(newsList);
        } else {
            mNewsList.addAll(newsList);
            notifyDataSetChanged();
        }
    }

    public void setAnim(boolean anim) {
        isAnim = anim;
    }

    public void setmNewsList(List<News> mNewsList) {
        this.mNewsList = mNewsList;
    }

    public List<News> getmNewsList() {
        return mNewsList;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cv_item)
        CardView mCvItem;

        @Bind(R.id.iv_news)
        ImageView mIvNews;

        @Bind(R.id.tv_title)
        TextView mTvTitle;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class NewsDateViewHolder extends NewsViewHolder {
        @Bind(R.id.tv_news_date)
        TextView mTvNewsDate;

        public NewsDateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
