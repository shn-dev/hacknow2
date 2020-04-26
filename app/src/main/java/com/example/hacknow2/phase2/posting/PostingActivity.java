package com.example.hacknow2.phase2.posting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.hacknow2.R;

public class PostingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.postContainer, PostingFragment.newInstance("",""))
                .commit();
    }
}
