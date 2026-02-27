package com.example.myapplication;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import android.app.Instrumentation; // Add this import

/**
 * Updated for Android 10+ (API 29 to API 36)
 * We now hook the ActivityTaskManager instead of the old ActivityManager.
 */
public class HookHelper {

    private static final String TAG = "CloneApp_Hook";

    public static void hookActivityManager() {
        try {
            // 1. On modern Android, the "Traffic Cop" is ActivityTaskManager
            Class<?> activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");

            // 2. Find the hidden field that holds the Singleton manager
            Field singletonField = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            singletonField.setAccessible(true); // Break the lock!
            Object singletonObject = singletonField.get(null);

            // 3. Inside the Singleton, find 'mInstance' which holds the REAL manager
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true); // Break the lock!

            // Get the REAL Activity Task Manager
            Object realActivityTaskManager = mInstanceField.get(singletonObject);

            // 4. Create our Fake Secretary, giving it the real manager
            ActivityManagerProxy proxyHandler = new ActivityManagerProxy(realActivityTaskManager);

            // 5. Generate the Proxy object using the modern IActivityTaskManager interface
            Class<?> iActivityTaskManagerInterface = Class.forName("android.app.IActivityTaskManager");
            Object proxyActivityTaskManager = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iActivityTaskManagerInterface},
                    proxyHandler
            );

            // 6. THE SWAP! Overwrite the real manager with our fake one
            mInstanceField.set(singletonObject, proxyActivityTaskManager);

            Log.e(TAG, "✅ SUCCESS: Hooked the modern ActivityTaskManager!");

        } catch (Exception e) {
            Log.e(TAG, "❌ FAILED ATM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- NEW METHOD FOR PHASE 3 ---
    public static void hookInstrumentation() {
        try {
            // 1. Find the main thread of our app
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object currentActivityThread = sCurrentActivityThreadField.get(null);

            // 2. Find the real Instrumentation object inside the thread
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation realInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);

            // 3. Create our custom wrapper
            PluginInstrumentation pluginInstrumentation = new PluginInstrumentation(realInstrumentation);

            // 4. THE SWAP! Overwrite the real instrumentation
            mInstrumentationField.set(currentActivityThread, pluginInstrumentation);

            Log.e(TAG, "✅ SUCCESS: Hooked Instrumentation!");

        } catch (Exception e) {
            Log.e(TAG, "❌ FAILED Instrumentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}