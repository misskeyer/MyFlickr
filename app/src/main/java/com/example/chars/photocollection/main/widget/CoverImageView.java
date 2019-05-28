package com.example.chars.photocollection.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.example.chars.photocollection.R;

public class CoverImageView extends AppCompatImageView {
    private float width = 1;
    private float height = 0.6f;
    private int[] coverSize;

    private boolean dynamicSize = true;

    public CoverImageView(Context context) {
        super(context);
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context c, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.CoverImageView,
                defStyleAttr, 0);
        dynamicSize = typedArray.getBoolean(R.styleable.CoverImageView_civ_dynamic_size, true);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (width >= 0 && height >= 0) {
            if (!dynamicSize)
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            else {
                int[] size = getMeasureSize(MeasureSpec.getSize(widthMeasureSpec), width, height);
                Log.i("CoverImage", "size1: " + size[0] + "  size2: " + size[1]);
                setMeasuredDimension(size[0], size[1]);
            }
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int[] getMeasureSize(int measureWidth, float width, float height) {
        return new int[]{measureWidth, (int) (measureWidth * height / width)};
    }

    public int[] getSize() {
        return coverSize;
    }

    public void setSize(int w, int h) {
        if (dynamicSize) {
            width = w;
            height = h;
            requestLayout();
        }
    }
}
