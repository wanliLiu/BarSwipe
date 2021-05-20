package com.barswipe.PicCache;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.barswipe.BaseActivity;
import com.barswipe.PicCache.adapter.AlbumGridViewAdapter;
import com.barswipe.PicCache.util.ImageManager;
import com.barswipe.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<String> dataList = new ArrayList<String>();
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
    private ArrayList<String> selectedDataList = new ArrayList<String>();

    //	private String cameraDir = "/DCIM/";
    private String cameraDir = Environment.getExternalStorageDirectory().toString();
    private ProgressBar progressBar;

    private AlbumGridViewAdapter gridImageAdapter;

    private TextView status;
    private LinearLayout selectedImageLayout;
    private Button okButton;
    private HorizontalScrollView scrollview;

    private CheckPicture checkPicture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedDataList = (ArrayList<String>) bundle.getSerializable("dataList");

        init();
        initListener();

    }

    private void init() {

        status = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList);
        gridView.setAdapter(gridImageAdapter);

        selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
        okButton = (Button) findViewById(R.id.ok_button);
        scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

        initSelectImage();

        checkPicture = new CheckPicture();
        checkPicture.execute(cameraDir);
    }

    private void initSelectImage() {
        if (selectedDataList == null)
            return;
        for (final String path : selectedDataList) {
            ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
            selectedImageLayout.addView(imageView);
            hashMap.put(path, imageView);
            ImageManager.from(AlbumActivity.this).displayImage(imageView, path, R.mipmap.camera_default, 100, 100);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    removePath(path);
                    gridImageAdapter.notifyDataSetChanged();
                }
            });
        }
        okButton.setText("完成(" + selectedDataList.size() + "/8)");
    }

    private void initListener() {

        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final ToggleButton toggleButton, int position, final String path, boolean isChecked) {

                if (selectedDataList.size() >= 8) {
                    toggleButton.setChecked(false);
                    if (!removePath(path)) {
                        Toast.makeText(AlbumActivity.this, "只能选择8张图片", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if (isChecked) {
//					try {
//						File file = ImageManager.from(AlbumActivity.this).getPicturePath(path);
//						Intent intent = new Intent(Intent.ACTION_VIEW);
//						intent.setDataAndType(Uri.fromFile(file), "image/*");
//						startActivity(intent);
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}


                    status.setTextSize(10);
                    status.setText(path);
                    if (!hashMap.containsKey(path)) {
                        ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
                        selectedImageLayout.addView(imageView);
                        imageView.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
                                if (off > 0) {
                                    scrollview.smoothScrollTo(off, 0);
                                }

                            }
                        }, 100);

                        hashMap.put(path, imageView);
                        selectedDataList.add(path);
                        ImageManager.from(AlbumActivity.this).displayImage(imageView, path, R.mipmap.camera_default, 100, 100);
                        imageView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                toggleButton.setChecked(false);
                                removePath(path);

                            }
                        });
                        okButton.setText("完成(" + selectedDataList.size() + "/8)");
                    }
                } else {
                    removePath(path);
                    status.setTextSize(20);
                    status.setText("查找图片完毕，图片数量为：" + dataList.size());
                }


            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                // intent.putArrayListExtra("dataList", dataList);
                bundle.putStringArrayList("dataList", selectedDataList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    private boolean removePath(String path) {
        if (hashMap.containsKey(path)) {
            selectedImageLayout.removeView(hashMap.get(path));
            hashMap.remove(path);
            removeOneData(selectedDataList, path);
            okButton.setText("完成(" + selectedDataList.size() + "/8)");
            return true;
        } else {
            return false;
        }
    }

    private void removeOneData(ArrayList<String> arrayList, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(s)) {
                arrayList.remove(i);
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
//    	super.onBackPressed();
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
//    	ImageManager.from(AlbumActivity.this).mDiskCache.clearCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            checkPicture.cancel(true);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkPicture.cancel(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class CheckPicture extends AsyncTask<String, String, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
            status.setTextSize(20);
            status.setText("查找所有的图片。。。。。。");
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            dataList.clear();
            listAlldir(new File(params[0]));
            return null;
        }

        protected void onPostExecute(ArrayList<String> tmpList) {

            if (AlbumActivity.this == null || AlbumActivity.this.isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            dataList.add("http://www.cn486.com/wallpaper-Animal/tu/24Snake/1024/0002.jpg");
            dataList.add("http://e.hiphotos.baidu.com/image/pic/item/562c11dfa9ec8a13bf8a2e45f503918fa1ecc087.jpg");
            dataList.add("http://f.hiphotos.baidu.com/image/pic/item/78310a55b319ebc4327336118026cffc1e17162a.jpg");
            dataList.add("http://a.hiphotos.baidu.com/image/pic/item/0ff41bd5ad6eddc499dd8c1f3bdbb6fd5266331f.jpg");
            dataList.add("http://a.hiphotos.baidu.com/image/pic/item/18d8bc3eb13533fa9a5e33caaad3fd1f40345b6d.jpg");
            dataList.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a8d572820bc9134954082376a3.jpg");
            dataList.add("http://d.hiphotos.baidu.com/image/pic/item/e4dde71190ef76c654c358cf9f16fdfaae5167c5.jpg");
            dataList.add("http://f.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3ceee67679a9045d688d53f20f7.jpg");
            gridImageAdapter.notifyDataSetChanged();

            status.setTextSize(20);
            status.setText("查找图片完毕，图片数量为：" + dataList.size());

            return;

        }

        ;

        protected void onProgressUpdate(String... value) {
            status.setTextSize(10);
            status.setText(value[0]);
            gridImageAdapter.notifyDataSetChanged();
        }

        protected void onCancelled(ArrayList<String> result) {
            status.setTextSize(15);
            status.setText("停止查找图片，已找到图片：" + dataList.size());
            progressBar.setVisibility(View.GONE);
        }

        ;

        /*
         * 找出SD中所有的目录
         */
        private void listAlldir(File nowDir) {
            nowDir = new File(nowDir.getPath());

            if (isCancelled()) {
                return;
            }
            //如果是目录
            if (nowDir.isDirectory()) {
                //列出所有文件
                File[] files = nowDir.listFiles();

                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().substring(0, 1).equals(".")) {
                        continue;
                    }
                    File file = new File(files[i].getPath());
                    if (file.isDirectory()) {
                        publishProgress("查找目录：" + file.toString());
                        listAlldir(file);
                    } else {
                        if (file.getPath().endsWith(".jpg") || file.getPath().endsWith(".png") ||
                                file.getPath().endsWith(".bmp") || file.getPath().endsWith(".jpeg")) {
                            dataList.add(file.getPath());
                            publishProgress("查找到图片：" + file.toString());
                        }
                    }
                }
            } else {
                if (nowDir.getPath().endsWith(".jpg") || nowDir.getPath().endsWith(".png")) {
                    dataList.add(nowDir.getPath());
                    publishProgress("查找到图片：" + nowDir.toString());
                }
            }
        }
    }
}
