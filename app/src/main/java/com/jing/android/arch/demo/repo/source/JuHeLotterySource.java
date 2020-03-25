package com.jing.android.arch.demo.repo.source;

import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.repo.db.LotteryDao;
import com.jing.android.arch.demo.repo.service.ApiException;
import com.jing.android.arch.demo.repo.service.LotteryService;
import com.jing.android.arch.demo.repo.service.model.JuHeLottery;
import com.jing.android.arch.demo.repo.service.model.JuHeResponse;
import com.jing.android.arch.repo.source.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.FlowableEmitter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 *
 * @author JingTuo
 */
public class JuHeLotterySource extends DataSource<List<Lottery>, List<JuHeLottery>> {

    private LotteryDao dao;

    public JuHeLotterySource(LotteryDao dao) {
        super(true, true, false);
        this.dao = dao;
    }

    @Override
    protected List<Lottery> getDataFromDatabase() {
        return dao.queryAllLottery();
    }

    @Override
    protected List<JuHeLottery> getDataFromServer(FlowableEmitter<List<Lottery>> emitter) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://apis.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LotteryService service = retrofit.create(LotteryService.class);
        Call<JuHeResponse<List<JuHeLottery>>> call = service.queryLottery("0480f243c8492e168037d7a0cf957f76");
        try {
            Response<JuHeResponse<List<JuHeLottery>>> response = call.execute();
            if (200 == response.code()) {
                //网络调用成功
                JuHeResponse<List<JuHeLottery>> fResponse = response.body();
                if (fResponse !=null ) {
                    if (0 == fResponse.getErrorCode()) {
                        //接口调用成功
                        return fResponse.getResult();
                    }
                    emitter.onError(new ApiException(fResponse.getReason(), fResponse.getErrorCode()));
                } else {
                    emitter.onError(new Exception("Http Response Body is empty!"));
                }
            } else {
                emitter.onError(new ApiException(response.message(), response.code()));
            }
        } catch (IOException e) {
            emitter.onError(e);
        }
        return null;
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
