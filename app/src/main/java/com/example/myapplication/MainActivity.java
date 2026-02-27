package com.example.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        HookHelper.hookActivityManager();
        HookHelper.hookInstrumentation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Tell the Smuggler to load the APK you uploaded!
        PluginManager.getInstance().loadPlugin(this, "plugin.apk");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        Button testButton = new Button(this);
        testButton.setText("Launch EXTERNAL Plugin App");
        layout.addView(testButton);

        setContentView(layout);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 2. CHANGE THESE STRINGS to match your Hello World APK's package name!
                // Look at the top of the MainActivity.java in your Hello World project to find this.
                String pluginPackageName = "com.example.gostapp"; // <-- REPLACE THIS!

                // Safety check so we don't try to launch the placeholder string
                if (pluginPackageName.equals("com.example.yourguestapp")) {
                    Toast.makeText(MainActivity.this, "⚠️ Please replace 'pluginPackageName' in the code with your real Plugin's package name!", Toast.LENGTH_LONG).show();
                    return;
                }

                String pluginClassName = pluginPackageName + ".MainActivity";

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(pluginPackageName, pluginClassName));
                startActivity(intent);
            }
        });
    }
}