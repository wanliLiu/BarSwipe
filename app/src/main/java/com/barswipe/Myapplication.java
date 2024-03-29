package com.barswipe;

import androidx.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.barswipe.dragger2.DaggerHouseComponent;
import com.barswipe.dragger2.HouseComponent;
import com.barswipe.dragger2.HouseModule;
import com.barswipe.draweePhotoView.lib.FrescoUtil;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoLi on 2015/12/11.
 */
public class Myapplication extends MultiDexApplication {

    private static Myapplication app;
    private static HouseComponent houseComponent;

    public static HouseComponent getHouseComponent() {
        return houseComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        houseComponent = DaggerHouseComponent.builder().houseModule(new HouseModule()).build();
        app = this;

        FrescoUtil.Init(this);

        JSONObject object = new JSONObject();
        object.put("UserToken", "sssssdsdwqeewewe");
        object.put("KeyID", "list");
        object.put("Total", 2);
        object.put("UseLogs", getRecordData(""));
        Log.e("json1", JSON.toJSONString(object));

        object.put("UseLogs", getRecordDatdsa());
        object.put("KeyID", "Array");
        Log.e("json2", JSON.toJSONString(object));

        FlowManager.init(this);

        //Stetho是一个Android应用的调试工具 Chrome Developer Tools
        Stetho.initializeWithDefaults(this);

        LeakCanary.install(this);
    }

    /**
     * @return
     */
    private DoorRecordBean[] getRecordDatdsa() {
        int length = 2;
        DoorRecordBean[] temp = new DoorRecordBean[length];
        while (length > 0) {
            temp[length - 1] = new DoorRecordBean();
            temp[length - 1].setOrderID(15152312);
            temp[length - 1].setDoorID(1526523);
            temp[length - 1].setOpenDoorTime("2015-15-12 12:3" + length);
            length--;
        }
        return temp;
    }

    /**
     * @param data
     * @return
     */
    private List<DoorRecordBean> getRecordData(String data) {
        List<DoorRecordBean> temp = new ArrayList<>();
        int length = 3;
        while (length > 0) {
            DoorRecordBean child = new DoorRecordBean();
            child.setOrderID(152312);
            child.setDoorID(1526523);
            child.setOpenDoorTime("2015-15-12 12:3" + length);
            temp.add(child);
            length--;
        }
        return temp;
    }

    /**
     * @return
     */
    public static Myapplication getApp() {
        return app;
    }
}
