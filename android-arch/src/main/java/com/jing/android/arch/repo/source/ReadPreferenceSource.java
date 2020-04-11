package com.jing.android.arch.repo.source;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 *
 * @author JingTuo
 */
public class ReadPreferenceSource implements SingleOnSubscribe<Map<String, ?>> {

    private Context context;

    private String name;

    private int mode;

    public ReadPreferenceSource(Context context, String name, int mode) {
        this.context = context;
        this.name = name;
        this.mode = mode;
    }

    public ReadPreferenceSource(Context context, String name) {
        this(context, name, Context.MODE_PRIVATE);
    }

    public ReadPreferenceSource(Context context) {
        this(context, context.getPackageName(), Context.MODE_PRIVATE);
    }


    @Override
    public void subscribe(@NonNull SingleEmitter<Map<String, ?>> emitter) throws Throwable {
        SharedPreferences preferences = context.getSharedPreferences(name, mode);
        emitter.onSuccess(preferences.getAll());
    }

}
