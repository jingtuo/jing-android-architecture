package com.jing.android.arch.repo.source;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 实现Preference在非UI线程进行编辑的功能
 * @author JingTuo
 */
public class WritePreferenceSource implements SingleOnSubscribe<Boolean> {

    private Context context;

    private String name;

    private int mode;

    public WritePreferenceSource(Context context, String name, int mode) {
        this.context = context;
        this.name = name;
        this.mode = mode;
    }

    public WritePreferenceSource(Context context, String name) {
        this(context, name, Context.MODE_PRIVATE);
    }

    public WritePreferenceSource(Context context) {
        this(context, context.getPackageName(), Context.MODE_PRIVATE);
    }


    @Override
    public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Throwable {
        SharedPreferences preferences = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = preferences.edit();
        write(editor);
        emitter.onSuccess(editor.commit());
    }

    /**
     * 方法内editor不需要执行commit或者apply
     * @param editor
     */
    protected void write(SharedPreferences.Editor editor) {

    }

}
