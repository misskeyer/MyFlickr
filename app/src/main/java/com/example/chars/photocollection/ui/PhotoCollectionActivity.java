package com.example.chars.photocollection.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chars.photocollection.R;
import com.example.chars.photocollection.ui.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoCollectionActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
//    @BindView(R.id.viewpager)
//    ViewPager mViewPager;
//    @BindView(R.id.tab_layout)
//    TabLayout mTabLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv_slide)
    NavigationView mNavigationView;

    private ViewPager mViewPager;

//    @Override
//    protected Fragment createFragment() {
//        return PhotoCollectionFragment.newInstance();
//    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoCollectionActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        mViewPager = findViewById(R.id.viewpager);
        initViewPager();
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.openNavigationDrawer, R.string.closeNavigatinDrawer);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initViewPager() {
        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        List<String> titles = new ArrayList<>();
        titles.add("Recent");
        titles.add("待定");
        titles.add("待定");
        titles.add("待定");
        for (int i = 0; i < titles.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        final List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++)
            fragments.add(new PhotoCollectionFragment());
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                fragments, titles);
        mViewPager.setAdapter(fragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
