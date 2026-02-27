package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

/**
 * Our Second Hook.
 * This intercepts the moment Android tries to create a screen object in memory.
 */
public class PluginInstrumentation extends Instrumentation {
    private static final String TAG = "CloneApp_Hook";

    // The real instrumentation we are replacing
    private Instrumentation base;

    public PluginInstrumentation(Instrumentation base) {
        this.base = base;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.e(TAG, "🕵️ INSTRUMENTATION: OS is trying to create -> " + className);

        // 1. Check if the intent has our hidden "backpack"
        Intent targetIntent = intent.getParcelableExtra("EXTRA_TARGET_INTENT");

        if (targetIntent != null && targetIntent.getComponent() != null) {
            String realClassName = targetIntent.getComponent().getClassName();
            Log.e(TAG, "🕵️ INSTRUMENTATION: Found hidden intent! Changing " + className + " back to " + realClassName);

            // 2. THE UN-SWITCH! We tell the system to build the hidden TargetActivity instead of the StubActivity
            className = realClassName;
        }

        // 3. Let the system actually build the screen using our new class name
        return super.newActivity(cl, className, intent);
    }
}