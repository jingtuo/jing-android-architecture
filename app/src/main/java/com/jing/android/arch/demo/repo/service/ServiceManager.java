package com.jing.android.arch.demo.repo.service;


import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author JingTuo
 */
public class ServiceManager {

    private Map<String, Object> services = new HashMap<>();

    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取彩票的服务,本方法不加同步锁,是因为现有场景不会多线程操作
     * @return
     */
    public LotteryService getLotteryService() {
        String key = LotteryService.class.getSimpleName();
        if (services.containsKey(key)) {
            return (LotteryService) services.get(key);
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://apis.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LotteryService service = retrofit.create(LotteryService.class);
        services.put(key, service);
        return service;
    }

    private static class SingletonHolder {
        private static final ServiceManager INSTANCE = new ServiceManager();
        private SingletonHolder() {

        }
    }
}
