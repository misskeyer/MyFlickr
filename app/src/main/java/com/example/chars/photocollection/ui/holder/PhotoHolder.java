package com.example.chars.photocollection.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.chars.photocollection.R;
import com.example.chars.photocollection.modle.data.PhotoItem;
import com.example.chars.photocollection.ui.PhotoCollectionFragment;
import com.example.chars.photocollection.ui.PhotoPageActivity;

public class PhotoHolder extends RecyclerView.ViewHolder {
    public ImageView itemImage;
    private PhotoItem photoItem;

    public PhotoHolder(@NonNull View itemView, final Context context) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.fragment_photo_collection_image_view);
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
    }

    public void bindPhoto(Drawable drawable) {
        itemImage.setImageDrawable(drawable);
    }
}
