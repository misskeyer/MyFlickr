package com.example.chars.photocollection.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.chars.photocollection.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoCollectionActivity extends abstarctRootActivity {
//    @BindView(R.id.viewpager)
//    ViewPager mViewPager;
//    @BindView(R.id.tab_layout)
//    TabLayout mTabLayout;
//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;

    @Override
    protected Fragment createFragment() {
        return PhotoCollectionFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoCollectionActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewPager();
        ButterKnife.bind(this);
//        setSupportActionBar(mToolbar);

    }

    private void initViewPager() {
        List<String> titles = new ArrayList<>();
        titles.add("Recent");
        titles.add("待定");
    }
}
