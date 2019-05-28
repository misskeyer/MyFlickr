package com.example.chars.photocollection.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chars.photocollection.PhotoCollection;
import com.example.chars.photocollection.R;
import com.example.chars.photocollection.network.FlickrFetchr;
import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.network.json.PhotoSizes;
import com.example.chars.photocollection.main.holder.PhotoHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

    private static final String TAG = "PhotoAdapter";

    private Context mContext;
    private List<PhotoItem> mPhotoItems;


    public PhotoAdapter(Context context, List<PhotoItem> items) {
        mContext = context;
        mPhotoItems = items;
//        Log.i("PhotoAdapter", String.valueOf(mPhotoItems.get(1).getOriginWidth()));
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
        Map<String, String> Options = new HashMap<>();
        Options.put("api_key", FlickrFetchr.API_KEY);
        Options.put("format", "json");
        Options.put("nojsoncallback", "1");

        PhotoItem item = mPhotoItems.get(position);

        Disposable disposable = PhotoCollection.getInstance().getApi()
                .requestRxSizesOb(Options, FlickrFetchr.GET_SIZES, item.getId())
                .map(new Function<PhotoSizes, PhotoItem>() {
                    @Override
                    public PhotoItem apply(PhotoSizes photoSizes) throws Exception {
                        List<PhotoSizes.SizesBean.SizeBean> listSize = photoSizes.getSizes().getSize();
                        int camp = listSize.size();
                        Log.i(TAG, "ListSize " + camp);
                        for (int i = 0; i < camp; i++) {
                            if (listSize.get(i).getLabel().equals("Original")) {
                                item.setOriginWidth(listSize.get(i).getWidth());
                                item.setOriginHeight(listSize.get(i).getHeight());
                            }
                            if (listSize.get(i).getLabel().equals("Thumbnail"))
                                item.setThumbnailUrl(listSize.get(i).getSource());
                            if (listSize.get(i).getLabel().equals("Medium 800"))
                                item.setUrl(listSize.get(i).getSource());
                        }
                        return item;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PhotoItem>() {
                    @Override
                    public void accept(PhotoItem photoItem) throws Exception {
                        holder.bindPhotoItem(item);
                        Log.i(TAG, "Id: " + photoItem.getId());
                        Log.i(TAG, "OriginWidth: " + String.valueOf(photoItem.getOriginWidth()));
                        Log.i(TAG, "OriginHeight: " + String.valueOf(photoItem.getOriginHeight()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "RxConnectionSizeError");
                        Log.i(TAG, "throwable e:" + throwable);
                    }
                });

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
    public void onViewRecycled(@NonNull PhotoHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return mPhotoItems.size();
    }
}
