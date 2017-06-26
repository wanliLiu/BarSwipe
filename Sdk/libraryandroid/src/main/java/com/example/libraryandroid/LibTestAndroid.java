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

    /**我是注释
     * @param context
     * @param msg
     */
    @NotProguard
    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     */
    private void mytest(){
        System.out.println("我是私有方法打印我啊你这是！！！！！！！！！！");
    }
    /**
     *
     */
    public void sout() {
        System.out.println("测试信息，已经调用了sout()方法");
    }

    /**
     *我是注释
     */
    private void testFuck()
    {
        System.out.println("testFuck");
    }

    /**
     *我是注释
     */
    @NotProguard
    public String testOtherLib() {
        JSONObject object = new JSONObject();
        object.put("code", "12");
        object.put("message", "来自库里面的东西，依赖第三方东西");
        object.put("result", "结果好了");
        mytest();
        testFuck();
        return object.toJSONString();
    }
}
