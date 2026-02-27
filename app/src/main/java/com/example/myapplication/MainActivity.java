package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        // 1. Hook the ATM to fake out the OS
        HookHelper.hookActivityManager();

        // 2. Hook Instrumentation to swap the screen back before it draws
        HookHelper.hookInstrumentation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Building a simple UI programmatically so you don't have to edit XML files right now
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView title = new TextView(this);
        title.setText("Clone App: PoC Phase 1");
        title.setTextSize(24f);
        layout.addView(title);

        Button testButton = new Button(this);
        testButton.setText("Launch Ghost App");
        layout.addView(testButton);

        setContentView(layout);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We are no longer testing Settings.
                // We are going to explicitly try to open our unregistered TargetActivity!
                Intent intent = new Intent(MainActivity.this, TargetActivity.class);
                startActivity(intent);
            }
        });
    }
}