package com.jing.android.arch.demo.util;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author JingTuo
 */
public abstract class AndroidViewModelFactory<T> implements ViewModelProvider.Factory  {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            Constructor<T> constructor = modelClass.getConstructor(Application.class, Bundle.class);
            return constructor.newInstance(getApplication(), getBundle());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }

    /**
     * 应用
     * @return
     */
    protected abstract Application getApplication();

    /**
     * bundle-传给页面的数据
     * @return
     */
    protected abstract Bundle getBundle();
}
