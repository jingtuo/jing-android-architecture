package com.jing.android.arch.demo.repo.db;

import androidx.room.Dao;
import androidx.room.DatabaseView;
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
    @Query("SELECT * FROM LotteryResult WHERE id = :id ORDER BY resultDate DESC LIMIT :pageSize OFFSET (:pageNo - 1) * :pageSize")
    public abstract List<LotteryResult> queryLotteryResult(String id, int pageNo, int pageSize);


    /**
     * 查询彩票的开奖结果
     * @param data 开奖结果
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insertLotteryHistory(List<LotteryResult> data);

    /**
     * 查询指定日期之前的指定球号出现的次数
     * 双色球:
     * 取红球,start=1,end=6
     * 取蓝球,start=7,end=7
     * @param id 彩票id
     * @param ballNo 球号
     * @param start 第一个球传1
     * @param end 最后一个球
     * @param date 查询指定日期之前的数据
     * @return
     */
    @Query("SELECT COUNT(*) AS frequency FROM LotteryResult WHERE id = :id " +
            "AND SUBSTR(result, (:start - 1) * 3 + 1, ((:end - :start) + 1) * 3 - 1) LIKE '%'||:ballNo||'%' " +
            "AND DATE(resultDate) < DATE(:date)")
    public abstract int queryLotteryBallNoCount(String id, String ballNo, int start, int end, String date);

    /**
     * 查询彩票的开奖次数
     * @param id 彩票id
     * @return
     */
    @Query("SELECT COUNT(*) FROM LotteryResult WHERE id = :id")
    public abstract int queryLotteryResultCount(String id);


    /**
     * 查询彩票和开奖期号查询开奖结果
     * @param id 彩票
     * @param lotteryNo 开奖期号
     * @return
     */
    @Query("SELECT * FROM LotteryResult WHERE id = :id AND lotteryNo = :lotteryNo")
    public abstract LotteryResult queryLotteryResult(String id, String lotteryNo);


    /**
     * 目前用于查询指定日期之前,n天内第n个球出现的球号,用于展示球号的走势
     * 如双色球的蓝色球
     * @param id 彩票id
     * @param position 第一个球是1
     * @param date 查询指定日期之前的数据
     * @param day 查询前n天的数据
     * @return 返回数据按日期升序排列
     */
    @Query("SELECT id AS id, lotteryNo AS lotteryNo, " +
            "SUBSTR(result, (:position - 1) * 3 + 1,  2) AS ballNo, " +
            "resultDate AS resultDate FROM LotteryResult WHERE id = :id " +
            "AND DATE(resultDate) < DATE(:date) AND DATE(resultDate) >= DATE(:date, 'start of day', '-'||:day||' day')" +
            "ORDER BY resultDate ASC")
    public abstract List<LotterySubResult> queryLotterySubResultByDayAsc(String id, int position,
                                                                      String date, int day);


    /**
     * 目前用于查询指定日期之前,n天内第n个球出现的球号,用推测下一次出现的球号
     * 如双色球的蓝色球
     * @param id 彩票id
     * @param position 第一个球是1
     * @param date 查询指定日期之前的数据
     * @param day 查询前n天的数据
     * @return 返回数据按日期降序排列
     */
    @Query("SELECT id AS id, lotteryNo AS lotteryNo, " +
            "SUBSTR(result, (:position - 1) * 3 + 1,  2) AS ballNo, " +
            "resultDate AS resultDate FROM LotteryResult WHERE id = :id " +
            "AND DATE(resultDate) < DATE(:date) AND DATE(resultDate) >= DATE(:date, 'start of day', '-'||:day||' day')" +
            "ORDER BY resultDate DESC")
    public abstract List<LotterySubResult> queryLotterySubResultByDayDesc(String id, int position,
                                                                         String date, int day);


    /**
     * 目前用于查询指定日期之前的第n个球出现的球号
     * 如双色球的蓝色球
     * @param id 彩票id
     * @param position 第一个球是1
     * @param date 查询指定日期之前的数据
     * @return
     */
    @Query("SELECT id AS id, lotteryNo AS lotteryNo, " +
            "SUBSTR(result, (:position - 1) * 3 + 1,  2) AS ballNo, " +
            "resultDate AS resultDate FROM LotteryResult WHERE id = :id " +
            "AND DATE(resultDate) < DATE(:date)" +
            "ORDER BY resultDate ASC")
    public abstract List<LotterySubResult> queryLotterySubResult(String id, int position, String date);

}
