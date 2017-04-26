package com.barswipe.model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by lizhangfeng on 16/4/19.
 * description: 数据库定义类
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    //数据库名称
    public static final String NAME = "fans_db";
    //数据库版本号
    /**
     * 注意每次修改数据库，添加或删除
     */
    public static final int VERSION = 3;

}
