package net.sourceforge.opencamera.NewFunction.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static DbHelper dbHelper;

    //数据库创建字段
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String CREATE_TYPELIST_SQL = "type_list "
            + "(typeName text);";
    private static final String CREATE_TYPE_SQL = " "
            + "(path text);";

    //构造器
    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //简化构造器
    private DbHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    //将自定义的数据库创建类单例
    public static  synchronized  DbHelper getInstance(Context context) {
        if(dbHelper==null){
            dbHelper = new DbHelper(context, "JYFdb");//数据库名称为JYF_db
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库
        sqLiteDatabase.execSQL(CREATE_TABLE + CREATE_TYPELIST_SQL);
        sqLiteDatabase.execSQL(CREATE_TABLE + "favorite" + CREATE_TYPE_SQL);
    }

    //添加表
    public void addSQL(SQLiteDatabase sqLiteDatabase, String SQLText) {
        sqLiteDatabase.execSQL(CREATE_TABLE + SQLText + CREATE_TYPE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //升级数据库

    }
}