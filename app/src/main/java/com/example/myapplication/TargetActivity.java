package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This is our "Ghost App" or "Cloned App" screen.
 * CRITICAL: Do NOT add this to your AndroidManifest.xml!
 * If the OS knew about this, it wouldn't be a trick.
 */
public class TargetActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("🔥 MAGIC! You are inside the UNREGISTERED Target Activity!");
        text.setTextSize(24f);
        text.setPadding(50, 50, 50, 50);
        setContentView(text);
    }
}