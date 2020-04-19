package com.jing.android.arch.demo.ui.editor.terminal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.constant.Keys;

/**
 * 命令行编辑器
 *
 * @author JingTuo
 */
public class TerminalEditorActivity extends BaseActivity {

    TerminalEditorViewModel vm;

    SharedPreferences preferences;

    ViewPager viewPager;

    TerminalEditorAdapter mAdapter;

    private boolean firstEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_editor);
        setActionBar(R.id.tool_bar, null, true);
        viewPager = findViewById(R.id.view_pager);
        mAdapter = new TerminalEditorAdapter(getSupportFragmentManager());
        mAdapter.setCount(1);
        viewPager.setAdapter(mAdapter);
        vm = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(TerminalEditorViewModel.class);
        vm.help().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showHelp(s);
            }
        });
        //Activity页面自身的Preference不使用后台线程读取,有两个原因:存储内容少,需要将Activity传给VM
        preferences = getPreferences(Context.MODE_PRIVATE);
        firstEnter = preferences.getBoolean(Keys.FIRST_ENTER_TERMINAL, true);
        if (firstEnter) {
            vm.loadHelp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.terminal_editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            vm.loadHelp();
            return true;
        }
        if (item.getItemId() == R.id.menu_add) {
            mAdapter.setCount(mAdapter.getCount() + 1);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelp(CharSequence msg) {
        if (firstEnter) {
            preferences.edit().putBoolean(Keys.FIRST_ENTER_TERMINAL, false).apply();
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.terminal_prompt)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
