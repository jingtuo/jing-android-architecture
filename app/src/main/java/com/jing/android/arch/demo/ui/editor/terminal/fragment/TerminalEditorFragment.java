package com.jing.android.arch.demo.ui.editor.terminal.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.jing.android.arch.component.BaseFragment;
import com.jing.android.arch.demo.R;

import java.security.Permission;
import java.security.Permissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TerminalEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author JingTuo
 */
public class TerminalEditorFragment extends BaseFragment {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private TextInputEditText etTerminal;

    private MaterialTextView tvTerminalResult;

    private TerminalEditorViewModel vm;

    public TerminalEditorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static TerminalEditorFragment newInstance() {
        return new TerminalEditorFragment();
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_terminal_editor;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        etTerminal = view.findViewById(R.id.et_terminal);
        tvTerminalResult = view.findViewById(R.id.tv_terminal_result);

        etTerminal.setOnEditorActionListener((v, actionId, event) -> {
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                //执行命令
                if (etTerminal.getText() == null) {
                    return false;
                }
                String command = etTerminal.getText().toString().trim();
                vm.exec(command);
                etTerminal.setText("");
                return true;
            }
            return false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory
                        .getInstance(requireActivity().getApplication()))
                .get(TerminalEditorViewModel.class);
        vm.terminalResult().observe(this, s -> tvTerminalResult.setText(s));
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //已经授权
            vm.enterWorkSpace();
            return;
        }
        boolean flag = shouldShowRequestPermissionRationale(Manifest.permission.INTERNET);
        if (flag) {
            //用户拒绝授权,未勾选不再访问,向用户描述为什么要使用权限,用户同意之后requestPermissions
            showWhyToApplyForWritePermission();
        } else {
            //用户拒绝授权,并且勾选了不再访问
            showOpenWriteStorageInPm();
        }
    }

    /**
     *
     */
    private void showWhyToApplyForWritePermission() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.terminal_prompt)
                .setMessage(R.string.terminal_why_to_apply_for_write_storage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }).setNegativeButton(android.R.string.no, (dialog, which) -> requireActivity().finish())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])
                    && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                vm.enterWorkSpace();
            }

        }
    }

    /**
     * 在权限管理中开启写权限
     */
    private void showOpenWriteStorageInPm() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.terminal_prompt)
                .setMessage(R.string.terminal_open_write_storage_in_pm)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                }).setNegativeButton(android.R.string.no, (dialog, which) -> requireActivity().finish())
                .show();
    }
}
