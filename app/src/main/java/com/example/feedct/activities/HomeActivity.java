package com.example.feedct.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.feedct.JSONManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.fragments.MinhasFragment;
import com.example.feedct.fragments.NotificacoesFragment;
import com.example.feedct.fragments.TodasFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_and_content);
        getSupportActionBar().setTitle("Minhas");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
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

        new JSONManager(getResources());
        new Session("af.moura@campus.fct.unl.pt");
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MinhasFragment(), "Minhas");
        adapter.addFragment(new TodasFragment(), "Todas");
        adapter.addFragment(new NotificacoesFragment(), "Notificações");
        viewPager.setAdapter(adapter);
    }
}
