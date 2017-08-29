package org.libsdl.app.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.libsdl.app.R;
import org.libsdl.app.sdl.SDLActivity;

/**
 * Created by soli on 28/08/2017.
 */

public class StudyFFmpegSDLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdlActivity:
                startActivity(new Intent(this, SDLActivity.class));
                break;
        }
    }
}
