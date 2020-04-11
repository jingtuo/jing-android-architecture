package com.jing.android.arch.demo.repo.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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
     *
     * @return
     */
    @Query("SELECT * FROM Lottery")
    public abstract List<Lottery> queryAllLottery();

    /**
     * 将所有彩票插入数据库
     *
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

    /**
     * 根据id查询彩票的开奖结果
     *
     * @param id 彩票id
     * @param pageNo   为了与接口匹配,第一页,pageNo=1
     * @param pageSize 每页数量
     * @return
     */
    @Query("SELECT * FROM LotteryResult WHERE id = :id ORDER BY date DESC LIMIT :pageSize OFFSET (:pageNo - 1) * :pageSize")
    public abstract List<LotteryResult> queryLotteryHistory(String id, int pageNo, int pageSize);


    /**
     * 查询彩票的开奖结果
     * @param data
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insertLotteryHistory(List<LotteryResult> data);

}
