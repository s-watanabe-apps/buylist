package com.swapps.buylist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swapps.buylist.dto.ChildItem;
import com.swapps.buylist.dto.ParentItem;
import com.swapps.buylist.singleton.Json;
import com.swapps.buylist.singleton.Prefs;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubActivity extends AppCompatActivity implements
        SubAdapter.OnStartDragListener,
        SubAdapter.OnItemActionListener {

    private ItemTouchHelper itemTouchHelper;
    private SubAdapter adapter;
    private TextView allSum;
    private TextView checkSum;
    private RecyclerView recyclerView;
    private ImageView addItem;
    private List<ParentItem> list;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Viewコントロール
        recyclerView = findViewById(R.id.sub_recycler_view);
        addItem = findViewById(R.id.sub_add_item);
        allSum = findViewById(R.id.sub_all_sum);
        checkSum = findViewById(R.id.sub_check_sum);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 戻るボタンの制御
        getOnBackPressedDispatcher().addCallback(this,
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    setResult(RESULT_OK);
                    finish();
                }
            });

        // 画面初期化
        init();
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
        index = getIntent().getIntExtra("index", -1);
        if (index < 0 || index >= list.size()) {
            setResult(2);
            finish();
        }

        setTitle(list.get(index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // リスト表示
        adapter = new SubAdapter(list.get(index).getItems(), this, this);
        recyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new SubDragCallback(adapter, () -> {
            list.get(index).setItems(adapter.getItems());
            Json.saveList(this, list);
        }));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        addItem.setOnClickListener(v -> showInputDialog());

        // ヘッダー
        setHeader();
    }

    /**
     * 画面ヘッダー更新処理.
     */
    private void setHeader() {
        Locale locale = Locale.getDefault();
        NumberFormat nf;
        if (Locale.JAPAN.equals(locale) || Locale.JAPANESE.equals(locale)) {
            nf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        } else {
            nf = NumberFormat.getCurrencyInstance(locale);
        }

        String allSumStr = getString(R.string.total) + " ";
        allSumStr += nf.format(list.get(index).getItems()
                .stream()
                .mapToInt(ChildItem::getValue)
                .sum());
        allSum.setText(allSumStr);

        String checkSumStr = "";
        checkSumStr += nf.format(list.get(index).getItems()
                .stream()
                .filter(ChildItem::getCheck)
                .mapToInt(ChildItem::getValue)
                .sum());
        checkSum.setText(checkSumStr);
    }

    /**
     * 商品情報入力ダイアログ.
     */
    private void showInputDialog() {
        showInputDialog(-1);
    }

    /**
     * 商品情報編集ダイアログ.
     * @param pos
     */
    private void showInputDialog(int pos) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this).setIcon(R.drawable.outline_edit_document_24);
        String title;
        if (pos >= 0) {
            title = getString(R.string.item_edit);
        } else {
            title = getString(R.string.item_input);
        }
        builder.setTitle(title);

        // 親レイアウト
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 0, 48, 0);

        // EditTextを生成
        final EditText input1 = new EditText(this);
        input1.setHint(getString(R.string.item_name_hint));
        input1.setSingleLine(true);
        final EditText input2 = new EditText(this);
        input2.setHint(getString(R.string.item_value_hint));
        input2.setSingleLine(true);
        input2.setInputType(InputType.TYPE_CLASS_NUMBER);
        input2.setFilters(new InputFilter[]{
            new InputFilter.LengthFilter(20)
        });
        final EditText input3 = new EditText(this);
        input3.setHint(getString(R.string.item_memo_hint));

        if (pos >= 0) {
            ChildItem item = list.get(index).getItems().get(pos);
            input1.setText(item.getName());
            input2.setText(String.valueOf(item.getValue()));
            input3.setText(item.getMemo());
        }

        layout.addView(input1);
        layout.addView(input2);
        layout.addView(input3);
        builder.setView(layout);

        // OKボタン
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input1.getText().toString();
            String value = input2.getText().toString();
            String memo = input3.getText().toString();
            if (pos >= 0) {
                list.get(index).getItems().get(pos).setName(name);
                list.get(index).getItems().get(pos).setValue(Integer.parseInt(value));
                list.get(index).getItems().get(pos).setMemo(memo);
            } else {
                ChildItem newItem = new ChildItem(name, false, parseInt(value), memo);
                if (Prefs.get("item", 2) == 1) {
                    list.get(index).getItems().add(0, newItem);
                } else {
                    list.get(index).getItems().add(newItem);
                }
            }

            Json.saveList(this, list);
            adapter.notifyDataSetChanged();
            setHeader();
        });

        // キャンセル
        builder.setNegativeButton(getString(R.string.button_negative), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * 商品情報削除ダイアログ.
     * @param pos
     */
    private void showDeleteDialog(int pos) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this).setIcon(R.drawable.outline_delete_24);
        String title = getString(R.string.item_delete);
        builder.setTitle(title);

        // 親レイアウト
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 0, 48, 0);

        // TextViewを生成
        final TextView text1 = new TextView(this);
        text1.setTextSize(16);
        text1.setPadding(14, 14, 14, 0);
        text1.setText(getString(R.string.item_delete_message));

        final TextView text2 = new TextView(this);
        text2.setTextSize(16);
        text2.setTypeface(null, Typeface.BOLD);
        text2.setTextColor(
                MaterialColors.getColor(text2, com.google.android.material.R.attr.colorPrimary)
        );
        text2.setPadding(14, 14, 14, 0);
        text2.setText(list.get(index).getItems().get(pos).getName());

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
            list.get(index).getItems().remove(pos);
            Json.saveList(this, list);
            adapter.notifyDataSetChanged();
            setHeader();
        });

        // キャンセル
        builder.setNegativeButton(getString(R.string.button_negative), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * 数値変換.
     * @param value
     * @return
     */
    private int parseInt(String value) {
        int ret = 0;

        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.value_exception_message), Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSelect(int position) {
        // 未使用
    }

    @Override
    public void onEdit(int position) {
        showInputDialog(position);
    }

    @Override
    public void onDelete(int position) {
        showDeleteDialog(position);
    }

    @Override
    public void onCheck(int position) {
        ChildItem item = list.get(index).getItems().get(position);
        item.setCheck(!item.getCheck());
        Json.saveList(this, list);
        adapter.notifyItemChanged(position);
        setHeader();
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