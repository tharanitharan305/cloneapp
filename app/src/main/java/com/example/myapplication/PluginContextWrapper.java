package com.example.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

/**
 * THE FAKE ID 🎭
 * We wrap the real Android Context. When the Guest App asks "Who am I?",
 * we intercept it and give it the Guest's package name instead of ours!
 */
public class PluginContextWrapper extends ContextWrapper {

    private String guestPackageName;

    public PluginContextWrapper(Context base, String guestPackageName) {
        super(base);
        this.guestPackageName = guestPackageName;
    }

    // --- THE LIES ---

    @Override
    public String getPackageName() {
        // When the app asks for its name, we lie!
        return guestPackageName;
    }

    @Override
    public Resources getResources() {
        // When the app asks for images/colors, we give it the Smuggler's resources!
        return PluginManager.getInstance().getPluginResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        // When the app asks for code, we give it the Smuggler's classloader!
        return PluginManager.getInstance().getPluginClassLoader();
    }

    // In a real clone app, we would also override getFilesDir(), getDatabasePath(), etc.
    // to point to the "0" or "1" virtual folders here!
}