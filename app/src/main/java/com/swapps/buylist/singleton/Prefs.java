package com.swapps.buylist.singleton;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String PREF_NAME = "app_prefs";
    private static SharedPreferences prefs;

    // 初期化（Applicationで1回だけ）
    public static void init(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPrefs() {
        if (prefs == null) {
            throw new IllegalStateException("Prefs not initialized");
        }
        return prefs;
    }

    // ===== 例：getter/setter =====

    public static void set(String key, int value) {
        getPrefs().edit().putInt(key, value).apply();
    }

    public static int get(String key, int def) {
        return getPrefs().getInt(key, def);
    }
}