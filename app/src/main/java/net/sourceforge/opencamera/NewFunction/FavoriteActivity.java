package net.sourceforge.opencamera.NewFunction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.NewFunction.DataBase.DbOperator;
import net.sourceforge.opencamera.NewFunction.FavoriteList.Type;
import net.sourceforge.opencamera.R;

import java.util.ArrayList;

public class FavoriteActivity extends Activity {

    private static final String TAG = "FavoriteActivity";
    private ArrayList<String> data;

    private DbOperator dbOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Update to Github...");
        Log.d(TAG, "Hello Github!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //建立数据库
        dbOperator = new DbOperator();

        //初始化表
        initData();

        //ListView适配器
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                FavoriteActivity.this, android.R.layout.simple_list_item_1, data);
        final ListView listView = (ListView)findViewById(R.id.favorite_type_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ListView点击事件
                if(data.get(position).equals("Add new...")) {
                    //添加表项
                    final EditText et = new EditText(FavoriteActivity.this);

                    //AlertDialog弹窗，用于输入新文件夹名
                    AlertDialog.Builder addType = new AlertDialog.Builder(FavoriteActivity.this);
                    addType.setTitle("Add new folder:");
                    addType.setView(et);
                    addType.setCancelable(true);
                    addType.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //确认添加
                            String input = et.getText().toString();
                            if(input.equals("")) {
                                Toast.makeText(getApplicationContext(), "Folder name cannot be empty!" + input, Toast.LENGTH_LONG).show();
                            }
                            else if(data.contains(input)) {
                                //文件夹不可重复
                                Toast.makeText(getApplicationContext(), "Folder exists!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                //在适配器及数据库中作对应信息添加
                                //数据库中除了添加主表表项外，还需要加上新的文件夹对应的新表
                                Type newType = new Type(input);
                                dbOperator.insert("type_list", newType);
                                dbOperator.addList(input);
                                adapter.remove("Add new...");
                                adapter.add(input);
                                adapter.add("Add new...");
                            }
                        }
                    });
                    //取消
                    addType.setNegativeButton("Cancel", null);
                    addType.show();
                }
                else {
                    //进入表项对应的文件夹
                    Intent intent = new Intent(FavoriteActivity.this, TypeActivity.class);
                    intent.putExtra("typeName", data.get(position));
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //ListView长按事件
                //AlertDialog弹窗确认是否删除
                final AlertDialog.Builder deleteType = new AlertDialog.Builder(FavoriteActivity.this);
                deleteType.setCancelable(true);
                deleteType.setTitle("Remove this folder?");
                deleteType.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //在适配器及数据库中作对应信息删除
                        //数据库中除了删除主表表项外，还需要删去该文件夹对应的表
                        dbOperator.delete("type_list", "typeName", data.get(position));
                        dbOperator.deleteList(data.get(position));
                        adapter.remove(data.get(position));
                    }
                });
                deleteType.setNegativeButton("Cancel", null);
                deleteType.show();
                return true;
            }
        });
    }

    public int getSize() {
        //判断运行过程中是否出错，最后一个按钮是否为Add new...
        if(data != null)
            return data.size();
        else
            return -1;
    }

    private void initData() {
        //初始化Activity内的文件夹表
        data = dbOperator.outputList("type_list");
        data.add("Add new...");
    }
}
