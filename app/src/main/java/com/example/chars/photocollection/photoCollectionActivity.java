package com.example.chars.photocollection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class photoCollectionActivity extends abstarctRootActivity {

    @Override
    protected Fragment createFragment() {
        return photoCollectionFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, photoCollectionActivity.class);
    }
}
