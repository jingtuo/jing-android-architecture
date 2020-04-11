package com.jing.android.arch.demo.ui.lottery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.jing.android.arch.demo.repo.LotteryRepo;
import com.jing.android.arch.demo.repo.db.Lottery;
import com.jing.android.arch.demo.repo.db.LotteryDatabase;
import com.jing.android.arch.demo.repo.service.LotteryService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author JingTuo
 */
public class LotteryViewModel extends AndroidViewModel {


    private LotteryRepo repo;

    public LotteryViewModel(@NonNull Application application) {
        super(application);
        LotteryDatabase db = Room.databaseBuilder(application, LotteryDatabase.class, "lottery").build();
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
        repo = new LotteryRepo(db.lotteryDao(), retrofit.create(LotteryService.class));
    }

    public LiveData<List<Lottery>> lotteryList() {
        return repo.getLotteryList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.release();
    }
}