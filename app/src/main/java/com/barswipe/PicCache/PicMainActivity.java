package com.barswipe.PicCache;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.barswipe.BaseActivity;
import com.barswipe.PicCache.adapter.GridImageAdapter;
import com.barswipe.R;

import java.util.ArrayList;


public class PicMainActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<String> dataList = new ArrayList<String>();
    private GridImageAdapter gridImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initListener();

    }

    private void init() {
        gridView = (GridView) findViewById(R.id.myGrid);
        dataList.add("camera_default");
        gridImageAdapter = new GridImageAdapter(this, dataList);
        gridView.setAdapter(gridImageAdapter);
    }

    private void initListener() {

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//				if (position == dataList.size() - 1) {

                Intent intent = new Intent(PicMainActivity.this,
                        AlbumActivity.class);
                Bundle bundle = new Bundle();
                // intent.putArrayListExtra("dataList", dataList);
                bundle.putStringArrayList("dataList",
                        getIntentArrayList(dataList));
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);

//				}

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                if (tDataList != null) {
                    if (tDataList.size() < 8) {
                        tDataList.add("camera_default");
                    }
                    dataList.clear();
                    dataList.addAll(tDataList);
                    gridImageAdapter.notifyDataSetChanged();
                }
            }
        } else {

            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(uri, null, null, null, null);
                cursor.moveToFirst();
                String filePath = cursor.getString(1);
                if (!dataList.contains(filePath)) {
                    dataList.add(filePath);
                    gridImageAdapter.notifyDataSetChanged();
                }
                //查询本地图片库图片的字段
//				String[] str = cursor.getColumnNames();
//				for(int i=0; i<str.length; i++){
//					Log.i("ColumNames", str[i]);
//				}
                //得到本地图片库中图片的 id、路径、大小、文件名
//				while(cursor.moveToNext()){
//					Log.i("====_id", cursor.getString(0)+ "");
//					Log.i("====_data", cursor.getString(1)+ "");
//					Log.i("====_size", cursor.getString(2)+ "");
//					Log.i("====_display_name", cursor.getString(3)+ "");
//					
//					Log.i("====mime_type", cursor.getString(4)+ "");
//					Log.i("====title", cursor.getString(5)+ "");
//					Log.i("====date_added", cursor.getString(6)+ "");
//					Log.i("====date_modified", cursor.getString(7)+ "");
//					
//					Log.i("====description", cursor.getString(8)+ "");
//					Log.i("====picasa_id", cursor.getString(9)+ "");
//					Log.i("====isprivate", cursor.getString(10)+ "");
//					Log.i("====latitude", cursor.getString(11)+ "");
//					
//					Log.i("====longitude", cursor.getString(12)+ "");
//					Log.i("====datetaken", cursor.getString(13)+ "");
//					Log.i("====orientation", cursor.getString(14)+ "");
//					Log.i("====mini_thumb_magic", cursor.getString(15)+ "");
//					
//					Log.i("====bucket_id", cursor.getString(16)+ "");
//					Log.i("====bucket_display_name", cursor.getString(17)+ "");
//					Log.i("====width", cursor.getString(18)+ "");
//					Log.i("====height", cursor.getString(19)+ "");
//				}
            }

        }

    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
            if (!s.contains("default")) {
                tDataList.add(s);
            }
        }

        return tDataList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.takelocalpicture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.taklocal) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }

        return true;
    }

}
