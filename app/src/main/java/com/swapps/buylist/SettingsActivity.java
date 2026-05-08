package com.swapps.buylist;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.swapps.buylist.singleton.Prefs;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroupMode;
    RadioButton modeDefault;
    RadioButton modeLight;
    RadioButton modeDark;

    RadioGroup radioGroupList;
    RadioButton listFirst;
    RadioButton listEnd;

    RadioGroup radioGroupItem;
    RadioButton itemFirst;
    RadioButton itemEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Viewコントロール
        radioGroupMode = findViewById(R.id.settings_radio_group_mode);
        modeDefault = findViewById(R.id.settings_radio_mode_default);
        modeLight = findViewById(R.id.settings_radio_mode_light);
        modeDark = findViewById(R.id.settings_radio_mode_dark);

        radioGroupList = findViewById(R.id.settings_radio_group_list);
        listFirst = findViewById(R.id.settings_radio_list_first);
        listEnd = findViewById(R.id.settings_radio_list_end);

        radioGroupItem = findViewById(R.id.settings_radio_group_item);
        itemFirst = findViewById(R.id.settings_radio_item_first);
        itemEnd = findViewById(R.id.settings_radio_item_end);

        // 戻るボタンの制御
        getOnBackPressedDispatcher().addCallback(this,
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    setResult(RESULT_OK);
                    finish();
                }
            });

        init();
    }

    /**
     * 画面初期化.
     */
    private void init() {
        setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int mode = Prefs.get("mode", 0);
        modeDefault.setChecked(mode == 0);
        modeLight.setChecked(mode == 1);
        modeDark.setChecked(mode == 2);

        radioGroupMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.settings_radio_mode_default) {
                // デフォルト
                Prefs.set("mode", 0);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (checkedId == R.id.settings_radio_mode_light) {
                // ライトテーマ
                Prefs.set("mode", 1);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.settings_radio_mode_dark) {
                // ダークテーマ
                Prefs.set("mode", 2);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        int list = Prefs.get("list", 2);
        listFirst.setChecked(list == 1);
        listEnd.setChecked(list == 2);

        radioGroupList.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.settings_radio_list_first) {
                Prefs.set("list", 1);
            } else if (checkedId == R.id.settings_radio_list_end) {
                Prefs.set("mode", 2);
            }
        });

        int item = Prefs.get("item", 2);
        itemFirst.setChecked(item == 1);
        itemEnd.setChecked(item == 2);

        radioGroupItem.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.settings_radio_item_first) {
                Prefs.set("item", 1);
            } else if (checkedId == R.id.settings_radio_item_end) {
                Prefs.set("item", 2);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}