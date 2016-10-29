package io.github.laucherish.purezhihud.db;

/**
 * Created by laucherish on 16/3/14.
 */
public class DBConstant {
    public static final String DB_NAME = "purezhihu";
    public static final int DB_VERSION = 1;

    public static final String TABLE_READ = "read";
    public static final String COLUMN_ID = "newid";

    public static final String CREATE_TABLE_READ = "create table " + TABLE_READ + "(" + COLUMN_ID + " text)";
}
