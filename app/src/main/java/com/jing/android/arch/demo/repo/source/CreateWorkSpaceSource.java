package com.jing.android.arch.demo.repo.source;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 在华为P30 Pro手机上{@link Context#getFilesDir()}获的目录是/data/user/0/packageName/files,
 * 使用{@link File#exists()}是true,但是使用cd命令提示No such file or directory
 * 在AndroidStudio的Device File Explorer中找不到目录/data/user
 *
 * @author JingTuo
 */
public class CreateWorkSpaceSource implements SingleOnSubscribe<String> {

    private Application application;

    private String wsName;

    public CreateWorkSpaceSource(Application application, String wsName) {
        this.application = application;
        this.wsName = wsName;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<String> emitter) throws Throwable {
        File file = application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (file != null) {
            //优先选择应用的工具目录
            File ws = new File(file.getParentFile(), wsName);
            if (!ws.exists()) {
                boolean flag = ws.mkdirs();
                if (flag) {
                    emitter.onSuccess(ws.getAbsolutePath());
                    return;
                }
            } else {
                emitter.onSuccess(ws.getAbsolutePath());
                return;
            }
        }
        file = Environment.getRootDirectory();
        //手动创建应用的工具目录
        String path = file.getAbsolutePath();
        File result = new File(path + File.separator + "/Android/data/" + application.getPackageName() + File.separator + wsName);
        if (!result.exists()) {
            boolean flag = result.mkdirs();
            if (flag) {
                emitter.onSuccess(result.getAbsolutePath());
            } else {
                emitter.onError(new Exception("Create WorkSpace Failure"));
            }
        } else {
            emitter.onSuccess(result.getAbsolutePath());
        }
    }
}
