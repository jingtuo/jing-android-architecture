package com.jing.android.arch.repo.source;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;

/**
 * @param <T> 本地数据
 * @param <R> 服务数据
 * @author JingTuo
 */
public abstract class DataSource<T, R> implements FlowableOnSubscribe<T> {

    private boolean databaseEnabled;

    private boolean serverEnabled;

    private boolean requestServerWhenDbHaveData;

    /**
     *
     * @param databaseEnabled 当提交数据时,不需要存储数据库,此时可以false;当查询数据时,可以存储数据,此时可以为true
     * @param serverEnabled 通常情况下设置为true,需要与服务器交互
     * @param requestServerWhenDbHaveData 针对查询数据的场景,减少网络数据的消耗,true:数据库有数据依然会从服务器请求数据;false:反之
     */
    public DataSource(boolean databaseEnabled, boolean serverEnabled, boolean requestServerWhenDbHaveData) {
        this.databaseEnabled = databaseEnabled;
        this.serverEnabled = serverEnabled;
        this.requestServerWhenDbHaveData = requestServerWhenDbHaveData;
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<T> emitter) throws Throwable {
        T result = null;
        //优先从数据库查找
        if (databaseEnabled) {
            result = getDataFromDatabase();
        }
        if (result != null) {
            emitter.onNext(result);
            if (!requestServerWhenDbHaveData) {
                return;
            }
        }
        //其次从服务器获取数据
        T newResult = null;
        if (serverEnabled) {
            newResult = convert(getDataFromServer(emitter));
        }
        if (newResult != null) {
            if (databaseEnabled) {
                if (result == null) {
                    insertToDatabase(newResult);
                } else {
                    updateToDatabase(newResult);
                }
                //此处再次从数据获取数据，是为了保证数据源的统一
                result = getDataFromDatabase();
                if (result != null) {
                    emitter.onNext(result);
                }
            } else {
                emitter.onNext(newResult);
            }
        }

    }

    protected T getDataFromDatabase() {
        return null;
    }

    /**
     * @param emitter 用于处理服务器数据异常
     * @return
     */
    protected R getDataFromServer(FlowableEmitter<T> emitter) {
        return null;
    }

    protected T convert(R data) {
        return null;
    }

    protected void insertToDatabase(T target) {

    }

    protected void updateToDatabase(T target) {

    }

}
