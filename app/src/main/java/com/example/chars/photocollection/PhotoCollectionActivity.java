package com.example.chars.photocollection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoCollectionActivity extends abstarctRootActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoCollectionFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoCollectionActivity.class);
    }
}
