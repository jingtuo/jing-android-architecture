package com.jing.android.arch.demo.repo.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * 彩票
 *
 * @author JingTuo
 */
@Database(entities = {Lottery.class, LotteryResult.class}, version = 1)
public abstract class LotteryDatabase extends RoomDatabase {

    /**
     * 彩票-Dao
     *
     * @return
     */
    public abstract LotteryDao lotteryDao();
}
