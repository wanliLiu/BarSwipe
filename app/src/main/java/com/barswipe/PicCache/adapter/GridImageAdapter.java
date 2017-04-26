package com.barswipe.PicCache.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.barswipe.PicCache.util.ImageManager;
import com.barswipe.R;
import com.barswipe.materials.widgets.SquareImageView;

import java.util.ArrayList;

public class GridImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> dataList;
    private DisplayMetrics dm;

    public GridImageAdapter(Context c, ArrayList<String> dataList) {

        mContext = c;
        this.dataList = dataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView imageView;
        if (convertView == null) {
            imageView = new SquareImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else
            imageView = (SquareImageView) convertView;
        String path;
        if (dataList != null && position < dataList.size())
            path = dataList.get(position);
        else
            path = "camera_default";
        Log.i("path", "path:" + path + "::position" + position);
        if (path.contains("default"))
            imageView.setImageResource(R.mipmap.camera_default);
        else {
            ImageManager.from(mContext).displayImage(imageView, path, R.mipmap.camera_default, 100, 100);
        }
        return imageView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

}
