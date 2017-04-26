package me.kaede.frescosample.snippet;

import android.os.Bundle;

import com.barswipe.BaseActivity;
import com.barswipe.R;

import me.kaede.widget.markdownview.MarkdownView;

public class SnippetActivity extends BaseActivity {

    public static final String EXTRA_FILE = "EXTRA_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snippet);
        String file = getIntent().getStringExtra(EXTRA_FILE);
        if (file!=null&&file.length()>0){
            MarkdownView markdownView = (MarkdownView) this.findViewById(R.id.markdownview);
            markdownView.loadMarkdownFile("file:///android_asset/"+file);
        }
    }
}
