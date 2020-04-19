package com.jing.android.arch.demo.repo.source;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * @author JingTuo
 */
public class TerminalHelpSource implements SingleOnSubscribe<String> {

    private static final String TAG = TerminalHelpSource.class.getSimpleName();

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        ProcessBuilder builder = new ProcessBuilder();
        //环境变量
        Map<String, String> environment = builder.environment();
        String path = environment.get("PATH");
        if (TextUtils.isEmpty(path)) {
            emitter.onSuccess("No Find Terminal");
            return;
        }
        String[] pathItem = path.split(":");
        int length = pathItem.length;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            try {
                File file = new File(pathItem[i]);
                if (file.exists()) {
                    File[] fileChildren = file.listFiles();
                    int fileChildrenCount = fileChildren != null ? fileChildren.length : 0;
                    if (fileChildrenCount > 0) {
                        stringBuilder.append("- ").append(pathItem[i]).append("\n");
                        for (int j = 0; j < fileChildrenCount; j++) {
                            String fileChildName = fileChildren[j].getName();
                            if (!fileChildName.contains(".")) {
                                //在华为P30手上出现".sec"文件
                                stringBuilder.append("\t").append("- ")
                                        .append(fileChildName)
                                        .append("\n");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        emitter.onSuccess(stringBuilder.toString());
    }
}
