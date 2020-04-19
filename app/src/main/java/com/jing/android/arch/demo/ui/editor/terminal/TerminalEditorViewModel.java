package com.jing.android.arch.demo.ui.editor.terminal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jing.android.arch.demo.repo.source.TerminalHelpSource;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 命令行编辑器的VM
 *
 * @author JingTuo
 */
public class TerminalEditorViewModel extends AndroidViewModel {

    private MutableLiveData<String> mHelp;

    private Disposable mHelpDisposable;

    public TerminalEditorViewModel(@NonNull Application application) {
        super(application);
        mHelp = new MutableLiveData<>();
    }

    public LiveData<String> help() {
        return mHelp;
    }

    public void loadHelp() {
        if (mHelpDisposable != null) {
            mHelpDisposable.dispose();
        }
        mHelpDisposable = Single.create(new TerminalHelpSource())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mHelp.setValue(s);
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mHelpDisposable != null) {
            mHelpDisposable.dispose();
        }
    }
}
