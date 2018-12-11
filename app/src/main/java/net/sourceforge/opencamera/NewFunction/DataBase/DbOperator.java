package net.sourceforge.opencamera.NewFunction.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DbOperator {
    private DbManager dbManager;
    private SQLiteDatabase database;
    private static final String TAG = "DbOperator";

    public DbOperator() {
        //创建数据库
        dbManager = DbManager.newInstances();
        database = dbManager.getDatabase();
    }

    //添加表
    public void addList(String SQLText){
        dbManager.addSQLList(database, SQLText);
    }

    //增加数据
    //tableName:数据库表名
    //object:插入的对象
    public void insert(String tableName, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ContentValues contentValues = new ContentValues();

        for(Field field : fields) {
            try {
                if(field.getName().equals("$change") || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                if(field.getType().getCanonicalName().equals("java.lang.String")) {
                    String content = (String) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                else {
                    Log.e(TAG, "Insert: Unknown field occurred.");
                }
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        database.insert(tableName, null, contentValues);
    }

    //删除数据
    //删除的表名
    //删除的字段名
    //删除的字段值
    public void delete(String tableName, String fieldName, String value) {
        database.delete(tableName, fieldName + "=?", new String[]{value});
    }

    //更改数据库数据
    public void update(String tableName, String columnName, String columnValue, Object object) {
        try {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            ContentValues contentValues = new ContentValues();
            for (Field field : fields) {
                if(field.getName().equals("$change") || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                if(field.getType().getCanonicalName().equals("java.lang.String")) {
                    String content = (String) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                else {
                    Log.e(TAG, "Update: Unknown field occurred.");
                }
                field.setAccessible(false);
            }
            database.update(tableName, contentValues, columnName + "=?", new String[]{columnValue});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //查询指定元素，返回是否存在
    public boolean ifexist(String tableName, String object) {
        Cursor cursor = database.rawQuery("select * from " + tableName, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            try {
                if(object.equals(cursor.getString(0))) {
                    return true;
                }
                cursor.moveToNext();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        cursor.close();
        return false;
    }

    //输出数据库内的所有元素
    public ArrayList<String> outputList(String tableName) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from " + tableName, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            try {
                list.add(cursor.getString(0));
                cursor.moveToNext();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return list;
    }

    //清空表
    public void emptyList(String tableName) {
        database.execSQL("DELETE FROM " + tableName);
    }

    //删除表
    public void deleteList(String tableName) {
        database.execSQL("drop table if exists " + tableName);
    }

    public boolean searchTable(String tableName) {
        boolean result = false;
        try { //search.db数据库的名字
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
             Cursor cursor = database.rawQuery(sql, null);
             if (cursor.moveToNext()) {
                 int count = cursor.getInt(0);
                 if (count > 0) { result = true; }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}