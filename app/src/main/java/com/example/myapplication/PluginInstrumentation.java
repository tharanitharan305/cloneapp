package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;

public class PluginInstrumentation extends Instrumentation {
    private static final String TAG = "CloneApp_Hook";
    private Instrumentation base;

    public PluginInstrumentation(Instrumentation base) {
        this.base = base;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Intent targetIntent = intent.getParcelableExtra("EXTRA_TARGET_INTENT");

        if (targetIntent != null && targetIntent.getComponent() != null) {
            String realClassName = targetIntent.getComponent().getClassName();

            if (PluginManager.getInstance().getPluginClassLoader() != null) {
                cl = PluginManager.getInstance().getPluginClassLoader();
            }
            className = realClassName;
        }
        return super.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {

        // --- PHASE 5: APPLYING THE FAKE ID ---
        Intent intent = activity.getIntent();
        Intent targetIntent = intent.getParcelableExtra("EXTRA_TARGET_INTENT");

        if (targetIntent != null && targetIntent.getComponent() != null) {
            String guestPackageName = targetIntent.getComponent().getPackageName();

            try {
                // 1. Create the Fake ID using the Guest's real package name
                PluginContextWrapper fakeContext = new PluginContextWrapper(activity.getBaseContext(), guestPackageName);

                // 2. Break into the Activity's brain (mBase) and swap the real Context for our Fake ID
                Field mBaseField = ContextWrapper.class.getDeclaredField("mBase");
                mBaseField.setAccessible(true);
                mBaseField.set(activity, fakeContext);

                Log.e(TAG, "🎭 FAKE ID APPLIED: The app now legally thinks its name is: " + activity.getPackageName());

            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to apply Fake ID: " + e.getMessage());
            }
        }

        // Let the Activity start normally (it is now wearing our mask!)
        super.callActivityOnCreate(activity, icicle);
    }
}