package com.jing.android.arch.demo.repo.source;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jing.android.arch.demo.repo.db.LotterySubResult;
import com.jing.android.arch.demo.ui.model.MyChartData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

/**
 * 彩票开奖结果转换为LineData
 *
 * @author JingTuo
 */
public class LotterySubResultToLineDataSource implements SingleOnSubscribe<MyChartData<LineData, List<LotterySubResult>>> {

    private static final String TAG = LotterySubResultToLineDataSource.class.getSimpleName();

    private String label;

    private List<LotterySubResult> result;

    public LotterySubResultToLineDataSource(String label, List<LotterySubResult> result) {
        this.label = label;
        this.result = result;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<MyChartData<LineData, List<LotterySubResult>>> emitter) throws Throwable {
        MyChartData<LineData, List<LotterySubResult>> myChartData = new MyChartData<>();
        myChartData.setSourceData(result);
        List<Entry> entries = new ArrayList<>();
        if (result != null) {
            int length = result.size();
            for (int i = 0; i < length; i++) {
                LotterySubResult item = result.get(i);
                String ballNo = item.getBallNo();
                int y = 0;

                try {
                    y =  Integer.parseInt(ballNo);
                    Entry entry = new Entry(i, y, item);
                    entries.add(entry);
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

            }
        }
        LineDataSet dataSet = new LineDataSet(entries, label);
        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);
        myChartData.setChartData(lineData);
        emitter.onSuccess(myChartData);
    }
}
