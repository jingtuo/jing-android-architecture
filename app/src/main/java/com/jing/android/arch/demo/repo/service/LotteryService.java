package com.jing.android.arch.demo.repo.service;

import com.jing.android.arch.demo.repo.service.model.JuHeLotteryResultHistory;
import com.jing.android.arch.demo.repo.service.model.JuHeResponse;
import com.jing.android.arch.demo.repo.service.model.JuHeLottery;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author JingTuo
 */
public interface LotteryService {

    /**
     * 查询彩票
     * @param key
     * @return
     */
    @GET("lottery/types")
    Call<JuHeResponse<List<JuHeLottery>>> queryLottery(@Query("key") String key);

    /**
     * 查询彩票的开奖结果
     * @param key
     * @param id 彩票ID
     * @param pageNo 当前页数，默认1
     * @param pageSize 每次返回条数，默认10，最大50
     * @return
     */
    @GET("lottery/history")
    Call<JuHeResponse<JuHeLotteryResultHistory>> queryLotteryHistory(@Query("key") String key,
                                                                     @Query("lottery_id") String id,
                                                                     @Query("page") int pageNo,
                                                                     @Query("page_size") int pageSize);

}
