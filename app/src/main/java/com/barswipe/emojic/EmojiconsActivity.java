/*
 * Copyright (c) 2016 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barswipe.emojic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.barswipe.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconPage;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.EmojiconsView;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class EmojiconsActivity extends AppCompatActivity {

    @BindView(R.id.input)
    EmojiconEditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emojicons);
        ButterKnife.bind(this);

        EmojiconsView emojiconsView = (EmojiconsView) findViewById(R.id.emojicons_view);
        //
        emojiconsView.setPages(Arrays.asList(
                new EmojiconPage(Emojicon.TYPE_PEOPLE, null, false, R.drawable.ic_emoji_people_light),
                new EmojiconPage(Emojicon.TYPE_NATURE, null, false, R.drawable.ic_emoji_nature_light),
                new EmojiconPage(Emojicon.TYPE_OBJECTS, null, false, R.drawable.ic_emoji_objects_light),
                new EmojiconPage(Emojicon.TYPE_PLACES, null, false, R.drawable.ic_emoji_places_light),
                new EmojiconPage(Emojicon.TYPE_SYMBOLS, null, false, R.drawable.ic_emoji_symbols_light),
                new EmojiconPage(Emojicon.TYPE_Weibo, null, false, R.drawable.ic_emoji_symbols_light)
        ));
        emojiconsView.setListener(new EmojiconGridFragment.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                EmojiconsFragment.input(input, emojicon);
            }
        });
    }
}
