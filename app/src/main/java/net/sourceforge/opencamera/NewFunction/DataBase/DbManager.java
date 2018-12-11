package net.sourceforge.opencamera.NewFunction.DataBase;

import android.database.sqlite.SQLiteDatabase;

import net.sourceforge.opencamera.OpenCameraApplication;

public class DbManager {
    private static DbManager DbManager;
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    //构造器
    private DbManager() {
        //创建数据库
        dbHelper = DbHelper.getInstance(OpenCameraApplication.getContext());
        database = dbHelper.getWritableDatabase();
    }

    //返回manager对象
    public static DbManager newInstances() {
        DbManager = new DbManager();
        return DbManager;
    }

    //返回database对象
    public SQLiteDatabase getDatabase() {
        return database;
    }

    //添加表
    public void addSQLList(SQLiteDatabase sqLiteDatabase, String SQLText) {
        dbHelper.addSQL(sqLiteDatabase,SQLText);
    }
}