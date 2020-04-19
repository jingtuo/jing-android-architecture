package com.jing.android.arch.demo.ui.editor.terminal.fragment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jing.android.arch.demo.repo.source.CreateWorkSpaceSource;
import com.jing.android.arch.demo.repo.source.TerminalSource;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 命令行编辑器的VM
 *
 * @author JingTuo
 */
public class TerminalEditorViewModel extends AndroidViewModel {

    private static final String COMMAND_START_FLAG = "$ > ";

    private static final String TAG = TerminalEditorViewModel.class.getSimpleName();

    private MutableLiveData<String> mTerminalResult;

    private Disposable mExecDisposable;

    private Disposable mEnterWorkSpaceDisposable;

    public TerminalEditorViewModel(@NonNull Application application) {
        super(application);
        mTerminalResult = new MutableLiveData<>();
    }

    public LiveData<String> terminalResult() {
        return mTerminalResult;
    }

    public void exec(String command) {
        if (mExecDisposable != null) {
            mExecDisposable.dispose();
        }
        StringBuilder builder = new StringBuilder();
        if (mTerminalResult.getValue() != null) {
            builder.append(mTerminalResult.getValue());
        }
        builder.append(command).append("\n");
        mTerminalResult.setValue(builder.toString());
        mExecDisposable = Single.create(new TerminalSource(new String[]{command}))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    StringBuilder builder2 = new StringBuilder();
                    if (mTerminalResult.getValue() != null) {
                        builder2.append(mTerminalResult.getValue());
                    }
                    builder2.append(s).append("\n");
                    builder2.append(COMMAND_START_FLAG);
                    mTerminalResult.setValue(builder2.toString());
                }, throwable -> {
                    StringBuilder builder2 = new StringBuilder();
                    if (mTerminalResult.getValue() != null) {
                        builder2.append(mTerminalResult.getValue());
                    }
                    builder2.append(throwable.getMessage()).append("\n");
                    builder2.append(COMMAND_START_FLAG);
                    mTerminalResult.setValue(builder2.toString());
                });
    }

    public void enterWorkSpace() {
        if (mEnterWorkSpaceDisposable != null) {
            mEnterWorkSpaceDisposable.dispose();
        }
        mEnterWorkSpaceDisposable = Single.create(new CreateWorkSpaceSource(getApplication(), "terminal"))
                .flatMap((Function<String, SingleSource<String>>) path -> Single.create(new TerminalSource(new String[]{
                        "cd " + path
                })))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.i(TAG, s);
                    mTerminalResult.setValue(COMMAND_START_FLAG);
                }, throwable -> {
                    Log.i(TAG, throwable.getMessage(), throwable);
                    mTerminalResult.setValue(COMMAND_START_FLAG);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mEnterWorkSpaceDisposable != null) {
            mEnterWorkSpaceDisposable.dispose();
        }
        if (mExecDisposable != null) {
            mExecDisposable.dispose();
        }
    }
}
