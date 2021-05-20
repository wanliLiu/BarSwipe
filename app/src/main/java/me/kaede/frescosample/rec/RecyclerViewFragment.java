package me.kaede.frescosample.rec;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barswipe.R;

import java.util.List;

import me.kaede.frescosample.ImageApi;


public class RecyclerViewFragment extends Fragment {
    private static final String BUNDLE_INDEX = "BUNDLE_INDEX";

    private int index;


    public static RecyclerViewFragment newInstance(int index) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(BUNDLE_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);


        //find view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        //init
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(index + 1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter adapter = new MyAdapter(index);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        List<String> datas;
        switch (index) {
            case 0:
            default:
                datas = ImageApi.jk.getUrls();
                break;
            case 1:
                datas = ImageApi.girly.getUrls();
                break;
            case 2:
                datas = ImageApi.legs.getUrls();
                break;
        }
        adapter.setDatas(datas);

        return view;
    }

}
