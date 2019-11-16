package com.example.feedct.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.feedct.DataManager;
import com.example.feedct.R;
import com.example.feedct.adapters.SectionsPageAdapter;
import com.example.feedct.fragments.CadeiraFragment;
import com.example.feedct.fragments.FeedbackFragment;
import com.example.feedct.pojos.Cadeira;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CadeiraActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Cadeira cadeira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_and_content);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = findViewById(R.id.container);

        TabLayout tabLayout = findViewById(R.id.tabs);
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

        String cadeiraName = getIntent().getStringExtra("Cadeira");
        DataManager.db.collection("cadeiras").whereEqualTo("nome", cadeiraName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                if (documents.size() == 1) {
                    cadeira = documents.get(0).toObject(Cadeira.class);
                    getSupportActionBar().setTitle(cadeira.getSigla());
                    setupViewPager(mViewPager);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new CadeiraFragment(cadeira), "Cadeira");
        adapter.addFragment(new FeedbackFragment(cadeira), "Feedback");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

