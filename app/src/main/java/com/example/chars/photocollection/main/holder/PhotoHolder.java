package com.example.chars.photocollection.main.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.chars.photocollection.R;
import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.main.PhotoPageActivity;
import com.example.chars.photocollection.main.widget.CoverImageView;
import com.example.chars.photocollection.util.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoHolder extends RecyclerView.ViewHolder {
    private CoverImageView itemImage;
    private PhotoItem photoItem;

    @BindView(R.id.item_photo)
    CardView mCardView;

    private Context mContext;

    public PhotoHolder(@NonNull View itemView, final Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = context;

        itemImage = itemView.findViewById(R.id.fragment_photo_collection_cover_image_view);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = PhotoPageActivity
                        .newIntent(context, photoItem.getPhotoPageUri());
                context.startActivity(i);
            }
        });
    }

    public void bindPhotoItem(PhotoItem item) {
        photoItem = item;
        itemImage.setSize(photoItem.getOriginWidth(), photoItem.getOriginHeight());
        Log.i("PhotoHolder", String.valueOf(photoItem.getOriginWidth()));
        ImageHelper.loadRegularPhoto(mContext, itemImage, photoItem);

//        mCardView.setCardBackgroundColor(
//                ImageHelper.computeCardBackgroundColor(photo.color));
    }

    public void onRecycled() {
        ImageHelper.releaseImageView(itemImage);
    }

    public void bindPhoto(Drawable drawable) {
        itemImage.setImageDrawable(drawable);
    }
}
