package com.example.feedct.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.Session;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.fragments.CadeiraFragment;
import com.example.feedct.fragments.FeedbackFragment;
import com.example.feedct.fragments.GruposFragment;
import com.example.feedct.pojos.Cadeira;
import com.example.feedct.pojos.CadeiraUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CadeiraActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private Cadeira cadeira;
    private SectionsPageAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_and_content);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = findViewById(R.id.container);

        tabLayout = findViewById(R.id.tabs);
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

        updateTabs();
    }

    private void updateTabs() {
        final String cadeiraName = getIntent().getStringExtra("Cadeira");
        DataManager.db.collection("cadeiras").whereEqualTo("nome", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                if (documents.size() == 1) {
                    cadeira = documents.get(0).toObject(Cadeira.class);
                    getSupportActionBar().setTitle(cadeira.getSigla());

                    DataManager.db.collection("cadeiraUser").whereEqualTo("emailUser", Session.userEmail).whereEqualTo("nomeCadeira", cadeira.getNome()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() == 1)
                                setupViewPager();
                            else
                                setupPartialViewPager();
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void setupViewPager() {
        tabsAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        tabsAdapter.addFragment(new CadeiraFragment(cadeira ,this), "Cadeira");
        tabsAdapter.addFragment(new FeedbackFragment(cadeira), "Feedback");
        tabsAdapter.addExtra(new GruposFragment(cadeira), "Grupos");
        mViewPager.setAdapter(tabsAdapter);
    }

    private void setupPartialViewPager() {
        tabsAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        tabsAdapter.addFragment(new CadeiraFragment(cadeira, this), "Cadeira");
        tabsAdapter.addFragment(new FeedbackFragment(cadeira), "Feedback");
        mViewPager.setAdapter(tabsAdapter);
    }

    public void showExtraTabs() {
        tabsAdapter.addExtra(new GruposFragment(cadeira), "Grupos");
        tabsAdapter.notifyDataSetChanged();
    }

    public void hideExtraTabs() {
        tabsAdapter.removeExtras();
        tabsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

