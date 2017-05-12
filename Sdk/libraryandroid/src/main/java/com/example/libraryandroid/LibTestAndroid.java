package com.example.libraryandroid;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Soli on 2017/5/12.
 */

public class LibTestAndroid {

    /**
     * @return
     */
    public String getStringFromLib() {
        return "StringFromLibTestAndroid";
    }

    /**
     * @param context
     * @param msg
     */
    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     */
    public void sout() {
        System.out.println("测试信息，已经调用了sout()方法");
    }

    /**
     *
     */
    public String testOtherLib() {
        JSONObject object = new JSONObject();
        object.put("code", "12");
        object.put("message", "来自库里面的东西，依赖第三方东西");
        object.put("result", "结果好了");

        return object.toJSONString();
    }
}
