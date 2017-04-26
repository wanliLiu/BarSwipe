/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.barswipe.imagescale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.R.id;
import com.barswipe.imagescale.animation.AnimationActivity;
import com.barswipe.imagescale.basicfeatures.BasicFeaturesActivity;
import com.barswipe.imagescale.configuration.ConfigurationActivity;
import com.barswipe.imagescale.eventhandling.EventHandlingActivity;
import com.barswipe.imagescale.eventhandlingadvanced.AdvancedEventHandlingActivity;
import com.barswipe.imagescale.extension.ExtensionActivity;
import com.barswipe.imagescale.imagedisplay.ImageDisplayActivity;
import com.barswipe.imagescale.viewpager.ViewPagerActivity;

public class MainActivity_imageScale extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setTitle("Subsampling Scale Image View");
        setContentView(R.layout.main_imagescale);
        findViewById(id.basicFeatures).setOnClickListener(this);
        findViewById(id.imageDisplay).setOnClickListener(this);
        findViewById(id.eventHandling).setOnClickListener(this);
        findViewById(id.advancedEventHandling).setOnClickListener(this);
        findViewById(id.viewPagerGalleries).setOnClickListener(this);
        findViewById(id.animation).setOnClickListener(this);
        findViewById(id.extension).setOnClickListener(this);
        findViewById(id.configuration).setOnClickListener(this);

        findViewById(id.github).setOnClickListener(this);
        findViewById(id.self).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == id.basicFeatures) {
            Intent intent = new Intent(this, BasicFeaturesActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.imageDisplay) {
            Intent intent = new Intent(this, ImageDisplayActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.eventHandling) {
            Intent intent = new Intent(this, EventHandlingActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.advancedEventHandling) {
            Intent intent = new Intent(this, AdvancedEventHandlingActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.viewPagerGalleries) {
            Intent intent = new Intent(this, ViewPagerActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.animation) {
            Intent intent = new Intent(this, AnimationActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.extension) {
            Intent intent = new Intent(this, ExtensionActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.configuration) {
            Intent intent = new Intent(this, ConfigurationActivity.class);
            startActivity(intent);
        } else if (view.getId() == id.github) {
            String url = "https://github.com/davemorrissey/subsampling-scale-image-view";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (view.getId() == id.self) {
            String url = "http://www.davemorrissey.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
