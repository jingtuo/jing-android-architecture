package com.jing.android.arch.demo.repo.source;

import android.net.TrafficStats;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.jing.android.arch.demo.constant.Constants;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.repo.service.ApiException;
import com.jing.android.arch.demo.repo.service.LotteryService;
import com.jing.android.arch.demo.repo.service.model.JuHeLotteryResult;
import com.jing.android.arch.demo.repo.service.model.JuHeLotteryResultHistory;
import com.jing.android.arch.demo.repo.service.model.JuHeResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;


/**
 * 彩票结果的数据源
 *
 * @author JingTuo
 */
public class LotteryResultDataSource extends PageKeyedDataSource<PageNoInfo, LotteryResult> {

    private static final String TAG = LotteryResultDataSource.class.getSimpleName();

    private LotteryService service;

    private LotteryDao dao;

    private String id;

    private String todayDate;

    public LotteryResultDataSource(LotteryService service, LotteryDao dao, String id) {
        this.service = service;
        this.dao = dao;
        this.id = id;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        todayDate = format.format(new Date());
    }

    /**
     * @return
     * @throws Exception
     */
    private JuHeLotteryResultHistory getDataFromServer(int pageNo, int pageSize) throws Exception {
        Call<JuHeResponse<JuHeLotteryResultHistory>> call = service.queryLotteryHistory(Constants.JU_HE_LOTTERY_KEY, id, pageNo, pageSize);
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


    @Override
    public void loadInitial(@NonNull LoadInitialParams<PageNoInfo> params, @NonNull LoadInitialCallback<PageNoInfo, LotteryResult> callback) {
        List<LotteryResult> result;
        PageNoInfo nextPageKey = null;
        boolean serverError = false;

        //先从本地数据查询
        result = dao.queryLotteryHistory(id, 1, params.requestedLoadSize);
        if (result != null && result.size() >= 1) {
            //本地有数据
            LotteryResult firstItem = result.get(0);
            String firstDate = firstItem.getDate();
            if (todayDate.equals(firstDate)) {
                //最新数据已经保存在数据库了，不需要从服务器查询
                if (result.size() >= params.requestedLoadSize) {
                    nextPageKey = new PageNoInfo();
                    nextPageKey.pageNo = 3;
                    //插入数量小于服务器返回的数量,说明本地已经有一部分数据,下一页优先从本地查询
                    nextPageKey.nextPageGetFromDbFirst = true;
                }
                callback.onResult(result, null, nextPageKey);
                return;
            }
        }

        try {
            //加载首页数据,优先从服务器加载,如果服务器出错,则从数据库加载
            JuHeLotteryResultHistory lotteryResultHistory = getDataFromServer(1, params.requestedLoadSize);
            long[] iResult = dao.insertLotteryHistory(convert(lotteryResultHistory.getList()));
            int insertCount = getInsertCount(iResult);
            int serverSize = lotteryResultHistory.getList().size();
            if (serverSize >= params.requestedLoadSize) {
                //服务器首页数据大于请求的size
                nextPageKey = new PageNoInfo();
                nextPageKey.pageNo = 3;
                //插入数量小于服务器返回的数量,说明本地已经有一部分数据,下一页优先从本地查询
                nextPageKey.nextPageGetFromDbFirst = insertCount < serverSize;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            serverError = true;
        }
        result = dao.queryLotteryHistory(id, 1, params.requestedLoadSize);
        int dbSize = result.size();
        if (serverError) {
            if (dbSize >= params.requestedLoadSize) {
                //当从服务器查询出错,直接从本地查询,如果本地数量大于请求的size,下一页优先从本地查询
                nextPageKey = new PageNoInfo();
                nextPageKey.pageNo = 3;
                nextPageKey.nextPageGetFromDbFirst = true;
            }
        }
        callback.onResult(result, null, nextPageKey);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<PageNoInfo> params, @NonNull LoadCallback<PageNoInfo, LotteryResult> callback) {
        //查询前一页面,始终从数据库中查询
        Log.i(TAG, "loadBefore: " + params.requestedLoadSize);
        int pageNo = params.key.pageNo;
        Log.i(TAG, "param:" + pageNo);
        List<LotteryResult> result = dao.queryLotteryHistory(id, pageNo, params.requestedLoadSize);
        if (pageNo == 1) {
            //已经到第一页
            callback.onResult(result, null);
        }
        PageNoInfo previousPage = new PageNoInfo();
        previousPage.pageNo = pageNo - 1;
        callback.onResult(result, params.key);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<PageNoInfo> params, @NonNull LoadCallback<PageNoInfo, LotteryResult> callback) {
        PageNoInfo pageNoInfo = params.key;
        int pageNo = pageNoInfo.pageNo;
        List<LotteryResult> result = null;
        if (pageNoInfo.nextPageGetFromDbFirst) {
            result = dao.queryLotteryHistory(id, pageNo, params.requestedLoadSize);
        }
        if (result != null && result.size() >= params.requestedLoadSize) {
            PageNoInfo nextPageInfo = new PageNoInfo();
            nextPageInfo.pageNo = pageNo + 1;
            nextPageInfo.nextPageGetFromDbFirst = true;
            callback.onResult(result, nextPageInfo);
            return;
        }
        //数据库的数量不满足要求数量,此时从服务器查询,如果服务器也不满足,则认为没有更多数据
        PageNoInfo nextPageInfo = null;
        try {
            JuHeLotteryResultHistory lotteryResultHistory = getDataFromServer(pageNo, params.requestedLoadSize);
            dao.insertLotteryHistory(convert(lotteryResultHistory.getList()));
            int serverSize = lotteryResultHistory.getList().size();
            if (serverSize >= params.requestedLoadSize) {
                //服务器返回数量大于请求的size,表示服务器还有更多数量
                nextPageInfo = new PageNoInfo();
                nextPageInfo.pageNo = pageNo + 1;
                nextPageInfo.nextPageGetFromDbFirst = false;
            }
            result = dao.queryLotteryHistory(id, pageNo, params.requestedLoadSize);
            callback.onResult(result, nextPageInfo);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callback.onResult(Collections.emptyList(), null);
        }

    }

    private List<LotteryResult> convert(List<JuHeLotteryResult> data) {
        List<LotteryResult> result = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            for (JuHeLotteryResult from : data) {
                LotteryResult to = new LotteryResult();
                to.setId(from.getLotteryId());
                to.setNo(from.getLotteryNo());
                to.setResult(from.getLotteryResult());
                to.setDate(from.getLotteryDate());
                to.setExpireDate(from.getLotteryExpireDate());
                to.setSaleAmount(from.getLotterySaleAmount());
                to.setPoolAmount(from.getLotteryPoolAmount());
                result.add(to);
            }
        }
        return result;
    }

}
