package com.nikunj.bubblefloatingview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nikunj.bubblefloatingview.R;

import com.nikunj.bubblefloatingview.bubbles.BubbleService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startBubblesService();
    }
    private void startBubblesService(){
        startService(new Intent(this, BubbleService.class));
    }
}
