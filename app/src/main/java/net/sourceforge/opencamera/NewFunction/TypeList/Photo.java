package net.sourceforge.opencamera.NewFunction.TypeList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Photo {

    //文件路径及bitmap文件
    private String photoPath;
    private Bitmap bitmap;
    private final String TAG = "Class Photo";

    public Photo(String photoPath) {
        this.photoPath = photoPath;
        setBitmap();
    }

    private void setBitmap() {
        //通过文件路径初始化bitmap
        if(photoPath != null) {
            bitmap = BitmapFactory.decodeFile(photoPath);
        }
        else {
            Log.e(TAG, "Set bitmap error.");
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
