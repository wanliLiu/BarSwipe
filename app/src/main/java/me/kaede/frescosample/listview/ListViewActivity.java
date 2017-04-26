package me.kaede.frescosample.listview;

import android.os.Bundle;
import android.widget.ListView;

import com.barswipe.BaseActivity;
import com.barswipe.R;

import me.kaede.frescosample.ImageApi;

public class ListViewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);

		ListView listView = (ListView) this.findViewById(R.id.list_main);
		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);

		adapter.setDatas(ImageApi.jk.getUrls());
	}


}
