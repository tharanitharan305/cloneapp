package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StubActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("SUCCESS! You are inside the Stub Activity!");
        text.setTextSize(24f);
        text.setPadding(50, 50, 50, 50);
        setContentView(text);
    }
}