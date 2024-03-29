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

package com.barswipe.imagescale.extension;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.barswipe.R;
import com.barswipe.R.id;
import com.barswipe.imagescale.extension.views.FreehandView;
import com.davemorrissey.labs.subscaleview.ImageSource;

public class ExtensionFreehandFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.extension_freehand_fragment, container, false);
        rootView.findViewById(id.previous).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ExtensionActivity) getActivity()).previous();
            }
        });
        final FreehandView imageView = (FreehandView) rootView.findViewById(id.imageView);
        imageView.setImage(ImageSource.asset("squirrel.jpg"));
        rootView.findViewById(id.reset).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.reset();
            }
        });
        return rootView;
    }

}
