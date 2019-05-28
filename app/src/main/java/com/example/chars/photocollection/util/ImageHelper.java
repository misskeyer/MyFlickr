package com.example.chars.photocollection.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.main.widget.CoverImageView;

import java.util.regex.Pattern;

public class ImageHelper {
    private static final String TAG = "ImageHelper";

    private static class SetEnableListener
            implements RequestListener<String, GlideDrawable> {

        private ImageView view;

        SetEnableListener(ImageView view) {
            this.view = view;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            view.setEnabled(true);
            return false;
        }
    }


    public static void loadRegularPhoto(Context context, CoverImageView view, PhotoItem photoItem) {
        loadRegularPhoto(context, view, photoItem, true);
        photoItem.loadPhotoSuccess = true;
        if (!photoItem.hasFadedIn) {
            photoItem.hasFadedIn = true;
            startSaturationAnimation(context, view);
        }
    }

    public static void loadRegularPhoto(Context context, CoverImageView view, PhotoItem photoItem,
                                        boolean saturation) {
        if (photoItem != null && photoItem.getUrl() != null
                && photoItem.getOriginWidth() != 0 && photoItem.getOriginHeight() != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && !photoItem.hasFadedIn && saturation) {
                AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                matrix.setSaturation(0);
                view.setColorFilter(new ColorMatrixColorFilter(matrix));
            }
            view.setEnabled(false);

            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(photoItem.getThumbnailUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new SetEnableListener(view));

            int[] size = photoItem.getRegularSize(context);
//            int[] size = view.getSize();
            Log.i(TAG, "size1: " + size[0] + "  size2: " + size[1]);
            Log.i(TAG, photoItem.getUrl());
            Glide.with(context)
                    .load(photoItem.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(size[0], size[1])
                    .thumbnail(thumbnailRequest)
                    .animate(new FadeAnimator())
                    .into(view);
        }
    }

    /**
     * Execute a saturation animation to make a image from white and black into color.
     * */
    public static void startSaturationAnimation(Context context, final ImageView target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            target.setHasTransientState(true);
            final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
            final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                    matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
            saturation.addUpdateListener(valueAnimator -> target.setColorFilter(new ColorMatrixColorFilter(matrix)));
            saturation.setDuration(500);
            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(context));
            saturation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    target.clearColorFilter();
                    target.setHasTransientState(false);
                }
            });
            saturation.start();
        }
    }

    public static int computeCardBackgroundColor(String color) {
        if (TextUtils.isEmpty(color)
                || (!Pattern.compile("^#[a-fA-F0-9]{6}").matcher(color).matches()
                && !Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches())) {
            return Color.argb(0, 0, 0, 0);
        } else {
            if (Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches()) {
                color = "#" + color;
            }
            int backgroundColor = Color.parseColor(color);
            int red = ((backgroundColor & 0x00FF0000) >> 16);
            int green = ((backgroundColor & 0x0000FF00) >> 8);
            int blue = (backgroundColor & 0x000000FF);
            return Color.rgb(
                    (int) (red + (255 - red) * 0.7),
                    (int) (green + (255 - green) * 0.7),
                    (int) (blue + (255 - blue) * 0.7));
        }
    }

    public static void releaseImageView(ImageView view) {
        Glide.clear(view);
    }
}
