package com.example.libraryandroid.test;

import android.content.Context;
import android.support.annotation.Keep;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.libraryandroid.ANewClass;
import com.example.libraryandroid.NotProguard;

/**
 * 这里是注释数据
 * Created by Soli on 2017/5/12.
 */

public class LibTestAndroid {

    /**
     * @return
     */
    public String getStringFromLib() {
        new ANewClass().testB();
        return "StringFromLibTestAndroid";
    }

    /**我是注释
     * @param context
     * @param msg
     */
    @Keep
    public void toast(Context context, String msg) {
        new ANewClass().testB();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     */
    private void mytest(){
        new ANewClass().testB();
        System.out.println("我是私有方法打印我啊你这是！！！！！！！！！！");
    }
    /**
     *
     */
    public void sout() {
        new ANewClass().testB();
        System.out.println("测试信息，已经调用了sout()方法");
    }

    /**
     *我是注释
     */
    private void testFuck()
    {
        new ANewClass().testB();
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

    public interface testInterface {
        void test();
    }
}
