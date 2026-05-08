package com.swapps.buylist;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swapps.buylist.dto.ChildItem;
import com.swapps.buylist.dto.ParentItem;
import com.swapps.buylist.singleton.Json;
import com.swapps.buylist.singleton.Prefs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MainAdapter.OnStartDragListener,
        MainAdapter.OnItemActionListener {

    private ItemTouchHelper itemTouchHelper;
    private MainAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView addItem;
    private List<ParentItem> list;
    private InterstitialAd interstitialAd;
    private int adCount = 0;

    // 画面遷移用ランチャー.
    private ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // 初回起動時を含めてTOP画面を3開票時したときに1回だけ全面広告を表示する
                        if (++adCount > 1 && result.getResultCode() == RESULT_OK) {
                            if (interstitialAd != null) {
                                interstitialAd.show(MainActivity.this);
                                interstitialAd = null;
                            }
                        } else if(result.getResultCode() == 2) {
                            Toast.makeText(this, getString(R.string.fatal_error_message), Toast.LENGTH_SHORT).show();
                        }
                        init();
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 広告セットアップ
        setAd();

        // SharedPreferences初期化
        Prefs.init(this);

        // Viewコントロール
        recyclerView = findViewById(R.id.main_recycler_view);
        addItem = findViewById(R.id.main_add_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 画面初期化
        init();
    }

    /**
     * 広告ロード処理.
     */
    public void setAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        // バナー型広告リクエスト
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(adRequest);

        // インタースティシャル型広告リクエスト
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    MainActivity.this.interstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    interstitialAd = null;
                }
            });
    }

    /**
     * 画面初期化.
     */
    private void init() {
        int mode = Prefs.get("mode", 0);
        if (mode == 1) {
            // ライト
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (mode == 2) {
            // ダーク
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // データ読み込み
        list = Json.getList(this);

        // リスト表示
        adapter = new MainAdapter(list, this, this);
        recyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new MainDragCallback(adapter, () -> {
            Json.saveList(this, adapter.getItems());
        }));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        addItem.setOnClickListener(v -> showInputDialog());
    }

    /**
     * リスト入力ダイアログ.
     */
    private void showInputDialog() {
        showInputDialog(-1);
    }

    /**
     * リスト編集ダイアログ.
     * @param index
     */
    private void showInputDialog(int index) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this).setIcon(R.drawable.outline_edit_document_24);
        String title;
        if (index >= 0) {
            title = getString(R.string.list_edit);
        } else {
            title = getString(R.string.list_input);
        }
        builder.setTitle(title);

        // 親レイアウト
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 0, 48, 0);

        // EditTextを生成
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.list_name_hint));
        input.setSingleLine(true);
        if (index >= 0) {
            input.setText(list.get(index).getName());
        }

        layout.addView(input);
        builder.setView(layout);

        // OKボタン
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input.getText().toString();
            if (index >= 0) {
                list.get(index).setName(name);
            } else {
                ParentItem newList = new ParentItem(name, new Date(), new ArrayList<>());
                if (Prefs.get("list", 2) == 1) {
                    list.add(0, newList);
                } else {
                    list.add(newList);
                }
            }

            Json.saveList(this, list);
            adapter.notifyDataSetChanged();
        });

        // キャンセル
        builder.setNegativeButton(getString(R.string.button_negative), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * リスト削除ダイアログ.
     * @param index
     */
    private void showDeleteDialog(int index) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this).setIcon(R.drawable.outline_delete_24);
        String title = getString(R.string.list_delete);
        builder.setTitle(title);

        // 親レイアウト
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 0, 48, 0);

        // TextViewを生成
        final TextView text1 = new TextView(this);
        text1.setTextSize(16);
        text1.setPadding(14, 14, 14, 0);
        text1.setText(getString(R.string.list_delete_message));

        final TextView text2 = new TextView(this);
        text2.setTextSize(16);
        text2.setTypeface(null, Typeface.BOLD);
        text2.setTextColor(
            MaterialColors.getColor(text2, com.google.android.material.R.attr.colorPrimary)
        );
        text2.setPadding(14, 14, 14, 0);
        text2.setText(list.get(index).getName());

        final TextView text3 = new TextView(this);
        text3.setTextSize(16);
        text3.setPadding(14, 14, 14, 0);
        text3.setText(getString(R.string.delete_confirm));

        layout.addView(text1);
        layout.addView(text2);
        layout.addView(text3);
        builder.setView(layout);

        // OKボタン
        builder.setPositiveButton("OK", (dialog, which) -> {
            list.remove(index);
            Json.saveList(this, list);
            adapter.notifyDataSetChanged();
        });

        // キャンセル
        builder.setNegativeButton(getString(R.string.button_negative), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            launcher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSelect(int position) {
        Intent intent = new Intent(this, SubActivity.class);
        intent.putExtra("index", position);
        launcher.launch(intent);
    }

    @Override
    public void onEdit(int position) {
        showInputDialog(position);
    }

    @Override
    public void onDelete(int position) {
        showDeleteDialog(position);
    }
}