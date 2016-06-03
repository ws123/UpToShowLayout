package com.carlos.uptoshowlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.carlos.uptoshow.mylibrary.UpToShowLayout;

public class MainActivity extends AppCompatActivity implements UpToShowLayout.RefreshListener {
    private UpToShowLayout upToShowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upToShowLayout = (UpToShowLayout) findViewById(R.id.upToShowLayout);
        HeaderView headerView = new HeaderView(this, upToShowLayout);
        upToShowLayout.setHeaderView(headerView);
        upToShowLayout.setRefreshListener(this);
    }

    @Override
    public void startRefreshing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                upToShowLayout.stopRefreshing();
            }
        }, 5000);
    }
}
