package com.jing.android.arch.demo.repo.service;

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

}
