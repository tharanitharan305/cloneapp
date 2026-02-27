package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * THE SMUGGLER
 * This class loads the code and resources from an external APK file.
 */
public class PluginManager {

    private static final String TAG = "CloneApp_Hook";

    private static PluginManager instance;
    private DexClassLoader pluginClassLoader;
    private Resources pluginResources;

    private PluginManager() {}

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public void loadPlugin(Context context, String apkName) {
        try {
            // 1. Find the APK in our private files folder
            File apkFile = new File(context.getFilesDir(), apkName);
            if (!apkFile.exists()) {
                Log.e(TAG, "❌ ERROR: Could not find " + apkName + " in the files directory!");
                return;
            }

            // Android 14+ (API 34+) Security Rule: Dynamically loaded files MUST be read-only!
            apkFile.setReadOnly();

            // 2. Create the DexClassLoader to read the APK's Java code
            File optDir = context.getDir("dex", Context.MODE_PRIVATE);
            pluginClassLoader = new DexClassLoader(
                    apkFile.getAbsolutePath(),
                    optDir.getAbsolutePath(),
                    null,
                    context.getClassLoader() // Fallback to our app's classloader
            );

            // 3. Create a custom AssetManager to read the APK's images and XML layouts
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, apkFile.getAbsolutePath());

            // 4. Create the custom Resources object
            Resources hostResources = context.getResources();
            pluginResources = new Resources(
                    assetManager,
                    hostResources.getDisplayMetrics(),
                    hostResources.getConfiguration()
            );

            Log.e(TAG, "✅ SUCCESS: Smuggled the Plugin APK into memory!");

        } catch (Exception e) {
            Log.e(TAG, "❌ FAILED to load plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DexClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    public Resources getPluginResources() {
        return pluginResources;
    }
}