package com.jing.android.arch.demo.repo.source;

import android.net.TrafficStats;
import android.util.Log;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.repo.service.ApiException;
import com.jing.android.arch.demo.repo.service.LotteryService;
import com.jing.android.arch.demo.repo.service.model.JuHeLotteryResult;
import com.jing.android.arch.demo.repo.service.model.JuHeLotteryResultHistory;
import com.jing.android.arch.demo.repo.service.model.JuHeResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;


/**
 * 彩票结果的数据源
 *
 * @author JingTuo
 */
public class SyncLotteryResultListSource implements FlowableOnSubscribe<String> {

    private static final int PAGE_SIZE = 50;

    private static final String TAG = SyncLotteryResultListSource.class.getSimpleName();

    private LotteryService service;

    private LotteryDao dao;

    private String id;

    public SyncLotteryResultListSource(LotteryService service, LotteryDao dao, String id) {
        this.service = service;
        this.dao = dao;
        this.id = id;
    }

    /**
     * @return
     * @throws Exception
     */
    private JuHeLotteryResultHistory getDataFromServer(int pageNo) throws Exception {
        Call<JuHeResponse<JuHeLotteryResultHistory>> call = service.queryLotteryHistory(Constants.JU_HE_LOTTERY_KEY, id, pageNo, PAGE_SIZE);
        TrafficStats.setThreadStatsTag(Constants.LOTTERY_SERVICE_QUERY_LOTTERY_HISTORY_TAG);
        Response<JuHeResponse<JuHeLotteryResultHistory>> response = call.execute();
        TrafficStats.clearThreadStatsTag();
        if (Constants.HTTP_CODE_OK == response.code()) {
            //网络调用成功
            JuHeResponse<JuHeLotteryResultHistory> fResponse = response.body();
            if (fResponse != null) {
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

    /**
     * 获取插入数据的真实行数
     *
     * @param data
     * @return
     */
    private int getInsertCount(long[] data) {
        int length = data.length;
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (data[i] != -1) {
                count++;
            }
        }
        return count;
    }

    private List<LotteryResult> convert(List<JuHeLotteryResult> data) {
        List<LotteryResult> result = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            for (JuHeLotteryResult from : data) {
                LotteryResult to = new LotteryResult();
                to.setId(from.getLotteryId());
                to.setLotteryNo(from.getLotteryNo());
                to.setResult(from.getLotteryResult());
                to.setResultDate(from.getLotteryDate());
                to.setExpireDate(from.getLotteryExpireDate());
                to.setSaleAmount(from.getLotterySaleAmount());
                to.setPoolAmount(from.getLotteryPoolAmount());
                result.add(to);
            }
        }
        return result;
    }

    /**
     * 首页必须直接从服务器获取
     *
     * @param pageNo
     * @param firstGetFromServer
     */
    private void syncData(int pageNo, boolean firstGetFromServer, @NonNull FlowableEmitter<String> emitter) {
        if (firstGetFromServer) {
            try {
                JuHeLotteryResultHistory lotteryResultHistory = getDataFromServer(pageNo);
                long[] iResult = dao.insertLotteryHistory(convert(lotteryResultHistory.getList()));
                int insertCount = getInsertCount(iResult);
                int serverSize = lotteryResultHistory.getList().size();
                if (serverSize >= PAGE_SIZE) {
                    //服务器首页数据大于请求的size
                    if (insertCount < PAGE_SIZE) {
                        //本地数据有部分数据
                        syncData(pageNo + 1, false, emitter);
                    } else {
                        syncData(pageNo + 1, true, emitter);
                    }
                } else {
                    //服务器首页数据少于请求的size,表示服务器没有更多数据,此时不再同步
                    emitter.onNext(Constants.SYNC_STATUS_SUCCESS);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                emitter.onError(e);
            }
            return;
        }
        //从本地数据查询
        List<LotteryResult> result = dao.queryLotteryResult(id, pageNo, PAGE_SIZE);
        if (result != null && result.size() >= PAGE_SIZE) {
            syncData(pageNo + 1, false, emitter);
            return;
        }
        try {
            JuHeLotteryResultHistory lotteryResultHistory = getDataFromServer(pageNo);
            dao.insertLotteryHistory(convert(lotteryResultHistory.getList()));
            int serverSize = lotteryResultHistory.getList().size();
            if (serverSize >= PAGE_SIZE) {
                //服务器首页数据大于请求的size
                syncData(pageNo + 1, true, emitter);
            } else {
                //服务器首页数据少于请求的size,表示服务器没有更多数据,此时不再同步
                emitter.onNext(Constants.SYNC_STATUS_SUCCESS);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            emitter.onError(e);
        }
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
        emitter.onNext(Constants.SYNC_STATUS_SYNCING);
        syncData(1, true, emitter);
    }
}
