package com.jing.android.arch.demo.repo.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/**
 * 访问彩票
 *
 * @author JingTuo
 */
@Dao
public abstract class LotteryDao {

    /**
     * 查询所有的彩票
     * @return
     */
    @Query("SELECT * FROM Lottery")
    public abstract List<Lottery> queryAllLottery();

    /**
     * 将所有彩票插入数据库
     * @param data
     * @return
     */
    @Insert
    public abstract long[] insertAll(List<Lottery> data);

    /**
     * 删除所有的彩票
     */
    @Query("DELETE FROM Lottery")
    abstract void deleteAll();


    @Transaction
    public void insertAllAfterDelete(List<Lottery> data) {
        deleteAll();
        insertAll(data);
    }

}
