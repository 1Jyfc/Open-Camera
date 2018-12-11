package net.sourceforge.opencamera.NewFunction;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.opencamera.NewFunction.DataBase.DbOperator;
import net.sourceforge.opencamera.NewFunction.FavoriteList.PhotoPath;
import net.sourceforge.opencamera.NewFunction.TypeList.Photo;
import net.sourceforge.opencamera.NewFunction.TypeList.PhotoAdapter;
import net.sourceforge.opencamera.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TypeActivity extends Activity {

    private static String TAG;
    private TextView title;
    public static final int CHOOSE_PHOTO = 1;

    private PhotoAdapter adapter;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private DbOperator dbOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        //分析FavoriteActivity的intent
        Intent intent = getIntent();
        TAG = intent.getStringExtra("typeName");
        title = (TextView)findViewById(R.id.type_layout_title);
        title.setText(TAG);

        //建立数据库
        dbOperator = new DbOperator();
        //初始化表
        initList();

        //初始化RecyclerList适配器
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.type_recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoAdapter(photoList);
        recyclerView.setAdapter(adapter);

        //长按删除
        adapter.setOnItemLongClickListener(new PhotoAdapter.onRecyclerItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                final int p = position;
                final AlertDialog.Builder deleteType = new AlertDialog.Builder(TypeActivity.this);
                deleteType.setCancelable(true);
                deleteType.setTitle("Remove this photo?");
                deleteType.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbOperator.delete(TAG, "path", photoList.get(p).getPhotoPath());
                        adapter.removeItem(p);
                    }
                });
                deleteType.setNegativeButton("Cancel", null);
                deleteType.show();
            }
        });

        //设置相册导入按钮
        setAlbumButton();
    }

    private void setAlbumButton() {
        Button add = (Button)findViewById(R.id.type_add_photo);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(TypeActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //检测权限
                    ActivityCompat.requestPermissions(TypeActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else {
                    openAlbum();
                }
            }
        });
    }

    private void openAlbum() {
        //跳转至相册
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取到权限
                    openAlbum();
                }
                else {
                    //未获取到权限
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    //版本问题导致Uri格式不同，19以上版本需要对Uri进行分析
                    if(Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    }
                    else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
                default:
                    break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            //Uri分类处理
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //document类型：取出document ID
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if("content".equalsIgnoreCase(uri.getScheme())) {
                //content类型：用一般方法处理
                imagePath = getImagePath(uri, null);
            }
            else if("file".equalsIgnoreCase(uri.getScheme())) {
                //file类型直接取出path
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //使用cursor调用query方法查找对应路径的文件
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        //添加照片至数据库及适配器
        if(imagePath != null) {
            PhotoPath photoPath = new PhotoPath(imagePath);

            dbOperator.insert(TAG, photoPath);
            Photo photo = new Photo(imagePath);
            adapter.addItem(photo);
        }
        else {
            Toast.makeText(this, "Fail to get image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initList() {
        //初始化photo表
        ArrayList<String> data = dbOperator.outputList(TAG);
        int size = data.size();
        for(int i = 0; i < size; i++) {
            Photo photo = new Photo(data.get(i));
            photoList.add(photo);
        }
    }
}
