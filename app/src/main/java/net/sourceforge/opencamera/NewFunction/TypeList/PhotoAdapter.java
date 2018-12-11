package net.sourceforge.opencamera.NewFunction.TypeList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.sourceforge.opencamera.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList<Photo> photoList;

    private onRecyclerItemClickListener mOnItemClickListener;
    private onRecyclerItemLongClickListener mOnItemLongClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;
        View photoView;

        public ViewHolder(View view) {
            super(view);
            photoView = view;
            photoImage = (ImageView)view.findViewById(R.id.photo_image);
        }
    }

    public PhotoAdapter(ArrayList<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.photoImage.setImageBitmap(photo.getBitmap());

        //设置单击事件
        if(mOnItemClickListener !=null){
            holder.photoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
        //长按事件
        if(mOnItemLongClickListener != null){
            holder.photoImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public interface onRecyclerItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface onRecyclerItemLongClickListener{
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(onRecyclerItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(onRecyclerItemLongClickListener onItemLongClickListener){
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void addItem(Photo photo) {
        photoList.add(photo);
        notifyItemInserted(photoList.size() - 1);
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        photoList.remove(position);
    }
}
