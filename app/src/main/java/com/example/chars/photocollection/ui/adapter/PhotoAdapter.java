package com.example.chars.photocollection.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.chars.photocollection.R;
import com.example.chars.photocollection.modle.data.PhotoItem;
import com.example.chars.photocollection.ui.PhotoCollectionFragment;
import com.example.chars.photocollection.ui.holder.PhotoHolder;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
    private Context mContext;
    private List<PhotoItem> mPhotoItems;


    public PhotoAdapter(Context context, List<PhotoItem> items) {
        mContext = context;
        mPhotoItems = items;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.photo_item, parent, false);
        return new PhotoHolder(view ,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        PhotoItem item = mPhotoItems.get(position);
        String url = item.getUrl();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.itemImage);
        holder.bindPhotoItem(item);

//        Bitmap bitmap = thumbnailDownloader.getCachedImage(item.getUrl());
//            if (bitmap == null) {
//                bitmap = BitmapUtils.getBitmapFromLocal(item.getUrl());
//                if (bitmap == null){
//                    Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
//                    photoHolder.bindPhoto(drawable);
//                    photoHolder.bindPhotoItem(item);
//                    thumbnailDownloader.queueThumbnail(photoHolder, item.getUrl());
//                    Log.i(TAG,"Loaded image from net");
//                } else {
//                    photoHolder.bindPhoto(new BitmapDrawable(getResources(), bitmap));
//                    Log.i(TAG,"Loaded image from disk");
//                }
//            }else {
//                photoHolder.bindPhoto(new BitmapDrawable(getResources(), bitmap));
//                Log.i(TAG,"Loaded image from cache");
//            }
//            preloadAdjacentPhotos(i);
    }

//    private void preloadAdjacentPhotos(int position) {
//            final int photoBufferSize = 10;
//            int startIndex = Math.max(position - photoBufferSize, 0);
//            int endIndex = Math.min(photoBufferSize + position, photoItems.size() - 1);
//            for (int i = startIndex; i < endIndex; i++) {
//                if (i == position)
//                    continue;
//                String url = photoItems.get(i).getUrl();
//                thumbnailDownloader.preloadImage(url);
//            }
//        }

    @Override
    public int getItemCount() {
        return mPhotoItems.size();
    }
}
