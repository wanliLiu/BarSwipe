package com.barswipe.flowlayout;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barswipe.R;
import com.barswipe.flowlayout.view.FlowLayout;
import com.barswipe.flowlayout.view.TagAdapter;
import com.barswipe.flowlayout.view.TagFlowLayout;

/**
 * Created by zhy on 15/9/10.
 */
public class SimpleFragment extends Fragment {
    private String[] mVals = new String[]
            {"SD存储卡",
                    "是一种基助理（外语缩助理（外语缩于半导体快闪记忆器的新", "一代记忆设备，由于它体积小、数据传输速度助理（外语缩助理（外语缩快",
                    "可热插拔", "Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                    "Android", "Weclome Hello", "Button Text", "TextView"};

    private TagFlowLayout mFlowLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final LayoutInflater mInflater = LayoutInflater.from(getActivity());
        mFlowLayout = (TagFlowLayout) view.findViewById(R.id.id_flowlayout);

        mFlowLayout.setAdapter(new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv, mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
    }
}
