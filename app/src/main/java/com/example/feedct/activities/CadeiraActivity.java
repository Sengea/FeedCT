package com.example.feedct.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.fragments.CadeiraFragment;
import com.example.feedct.jsonpojos.Cadeira;
import com.google.android.material.tabs.TabLayout;

public class CadeiraActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Cadeira cadeira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cadeira = JSONManager.cadeiraByName.get(getIntent().getStringExtra("Cadeira"));
        setContentView(R.layout.tabs_and_content);

        getSupportActionBar().setTitle(cadeira.getSigla());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportActionBar().setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new CadeiraFragment(cadeira), "Cadeira");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
