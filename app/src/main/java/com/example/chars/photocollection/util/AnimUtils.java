package com.example.chars.photocollection.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.os.Build;
import android.util.FloatProperty;
import android.util.Property;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class AnimUtils {
    private static Interpolator fastOutSlowIn;

    public static class ObservableColorMatrix extends ColorMatrix {

        private float saturation = 1f;

        public ObservableColorMatrix() {
            super();
        }

        private float getSaturation() {
            return saturation;
        }

        @Override
        public void setSaturation(float saturation) {
            this.saturation = saturation;
            super.setSaturation(saturation);
        }

        public static final Property<ObservableColorMatrix, Float> SATURATION
                = new FloatProperty<ObservableColorMatrix>("saturation") {

            @Override
            public void setValue(ObservableColorMatrix cm, float value) {
                cm.setSaturation(value);
            }

            @Override
            public Float get(ObservableColorMatrix cm) {
                return cm.getSaturation();
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }

}
