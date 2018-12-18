package com.example.chars.photocollection;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class photoCollectionActivity extends abstarctRootActivity {

    @Override
    protected Fragment createFragment() {
        return photoCollectionFragment.newInstance();
    }
}
