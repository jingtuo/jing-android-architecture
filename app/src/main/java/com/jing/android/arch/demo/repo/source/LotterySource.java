package com.jing.android.arch.demo.repo.source;

import android.net.TrafficStats;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.service.ApiException;
import com.jing.android.arch.demo.repo.service.LotteryService;
import com.jing.android.arch.demo.repo.service.model.JuHeLottery;
import com.jing.android.arch.demo.repo.service.model.JuHeResponse;
import com.jing.android.arch.repo.source.DataSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 *
 *
 * @author JingTuo
 */
public class LotterySource extends DataSource<List<Lottery>, List<JuHeLottery>> {

    private LotteryDao dao;

    private LotteryService service;

    public LotterySource(LotteryDao dao, LotteryService service) {
        super(true, true, false);
        this.dao = dao;
        this.service = service;
    }

    @Override
    protected List<Lottery> getDataFromDatabase() {
        return dao.queryAllLottery();
    }

    @Override
    protected List<JuHeLottery> getDataFromServer() throws Exception {
        Call<JuHeResponse<List<JuHeLottery>>> call = service.queryLottery(Constants.JU_HE_LOTTERY_KEY);
        TrafficStats.setThreadStatsTag(Constants.LOTTERY_SERVICE_QUERY_LOTTERY_TAG);
        Response<JuHeResponse<List<JuHeLottery>>> response = call.execute();
        TrafficStats.clearThreadStatsTag();
        if (Constants.HTTP_CODE_OK == response.code()) {
            //网络调用成功
            JuHeResponse<List<JuHeLottery>> fResponse = response.body();
            if (fResponse !=null ) {
                if (0 == fResponse.getErrorCode()) {
                    //接口调用成功
                    return fResponse.getResult();
                }
                throw new ApiException(fResponse.getReason(), fResponse.getErrorCode());
            } else {
                throw new Exception("Http Response Body is empty!");
            }
        } else {
            throw new ApiException(response.message(), response.code());
        }
    }

    @Override
    protected List<Lottery> convert(List<JuHeLottery> data) {
        List<Lottery> result = new ArrayList<>();
        if (data !=null && !data.isEmpty()) {
            for (JuHeLottery fromItem: data) {
                Lottery toItem = new Lottery();
                toItem.setId(fromItem.getLotteryId());
                toItem.setName(fromItem.getLotteryName());
                toItem.setType(fromItem.getLotteryTypeId());
                toItem.setRemark(fromItem.getRemarks());
                toItem.setCreateTime(System.currentTimeMillis());
                result.add(toItem);
            }
        }
        return result;
    }

    @Override
    protected void insertToDatabase(List<Lottery> target) {
        dao.insertAll(target);
    }

    @Override
    protected void updateToDatabase(List<Lottery> target) {
        dao.insertAllAfterDelete(target);
    }
}
