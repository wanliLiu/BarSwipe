package com.barswipe.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Adapter基类
 * @version V1.0
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

	protected List<T> mList;

	protected Context  ctx;

	protected ListView mListView;

	protected LayoutInflater mInflater;

	public BaseListAdapter(Context context) {
		this.ctx = context;
		mInflater = LayoutInflater.from(ctx);
	}

	public BaseListAdapter(Context context, List<T> list) {
		this.ctx = context;
		this.mList = list;
		mInflater = LayoutInflater.from(ctx);
	}

	/**
	 * 显示Toast
	 * 
	 * @param text
	 *            文本内容
	 */
	protected void MyToast(String text) {
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示Toast
	 * 
	 * @param resId
	 *            string资源id
	 */
	protected void MyToast(int resId) {
		Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList == null ? 0 : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public void setList(List<T> list) {
		mList = new ArrayList<T>();
		this.mList = list;
		notifyDataSetChanged();
	}

	public List<T> getList() {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		return this.mList;
	}

	public void add(T t) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.add(t);
		notifyDataSetChanged();
	}
	
    public void set(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.set(location, t);
        notifyDataSetChanged();
    }	

	public void add(int location, T t) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.add(location, t);
		notifyDataSetChanged();
	}

	public void addAll(List<T> list) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		if (list!=null) {
			mList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void addAllToFirst(List<T> list) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.addAll(0, list);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		if (mList != null) {
			mList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void removeListData(int position) {
		if (mList != null) {
			mList.remove(position);
		}
	}

	public void removeAll() {
		if (mList != null) {
			mList.clear();
			notifyDataSetChanged();
		}
	}

	public void removeAll(List<T> list) {
		if (mList != null) {
			mList.removeAll(list);
			notifyDataSetChanged();
		}
	}

	protected void setText(TextView textView,CharSequence text){
		if(!TextUtils.isEmpty(text)){
			textView.setText(text);
		}else{
			textView.setText("");
		}
	}



	public void setListView(ListView listView) {
		this.mListView = listView;
	}

	public ListView getListView() {
		return this.mListView;
	}
}
