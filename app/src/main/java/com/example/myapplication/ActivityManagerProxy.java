package com.example.myapplication;

import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityManagerProxy implements InvocationHandler {

    private static final String TAG = "CloneApp_Hook";
    private Object realActivityTaskManager;

    public ActivityManagerProxy(Object realActivityTaskManager) {
        this.realActivityTaskManager = realActivityTaskManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Intercept modern startActivity calls
        if ("startActivity".equals(method.getName())) {
            Log.e(TAG, "🚦 TRAFFIC COP: I caught an app trying to start a screen!");

            // Find the Intent in the arguments array
            int intentIndex = -1;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        intentIndex = i;
                        break;
                    }
                }
            }

            if (intentIndex != -1) {
                Intent originalIntent = (Intent) args[intentIndex];
                Log.e(TAG, "🚦 TRAFFIC COP: Original destination -> " + originalIntent.getAction());

                // --- PHASE 2: THE BAIT AND SWITCH ---

                // 1. Create a fake Intent pointing to our StubActivity
                Intent stubIntent = new Intent();
                stubIntent.setClassName("com.example.myapplication", "com.example.myapplication.StubActivity");

                // 2. Hide the original Intent inside the Stub Intent's extras
                stubIntent.putExtra("EXTRA_TARGET_INTENT", originalIntent);

                // 3. Swap out the intent in the arguments list!
                // The Android system will now process our stubIntent instead of the original one.
                args[intentIndex] = stubIntent;

                Log.e(TAG, "🚦 TRAFFIC COP: Tricked the OS! Swapped destination to StubActivity.");
            }
        }

        // Pass the modified arguments to the real system
        return method.invoke(realActivityTaskManager, args);
    }
}