package com.jing.android.arch.demo.ui.lottery.ssq;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jing.android.arch.component.BaseActivity;
import com.jing.android.arch.demo.R;
import com.jing.android.arch.demo.databinding.ActivitySsqAnalysisBinding;
import com.jing.android.arch.demo.repo.db.LotteryResult;
import com.jing.android.arch.demo.util.AndroidViewModelFactory;

/**
 * @author JingTuo
 */
public class SsqAnalysisActivity extends BaseActivity {

    private SsqAnalysisViewModel vm;
    private ActivitySsqAnalysisBinding binding;
    private BarChart barChartRedFrequency;
    private BarChart barChartBlueFrequency;
    private LineChart lineChartBlueTrend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ssq_analysis);
        Intent intent = getIntent();
        String title = intent.getStringExtra("name");
        setActionBar(R.id.tool_bar, title, true);
        vm = new ViewModelProvider(this, new AndroidViewModelFactory<SsqAnalysisViewModel>() {
            @Override
            protected Application getApplication() {
                return SsqAnalysisActivity.this.getApplication();
            }

            @Override
            protected Bundle getBundle() {
                return getIntent().getExtras();
            }
        }).get(SsqAnalysisViewModel.class);

        vm.result().observe(this, new Observer<LotteryResult>() {
            @Override
            public void onChanged(LotteryResult lotteryResult) {
                binding.setItem(lotteryResult);
                binding.executePendingBindings();
            }
        });
        initChart();
    }

    private void initChart() {
        barChartRedFrequency = findViewById(R.id.bar_chart_red_frequency);
        barChartBlueFrequency = findViewById(R.id.bar_chart_blue_frequency);
        lineChartBlueTrend = findViewById(R.id.line_chart_blue_trend);

        initBarChart(barChartRedFrequency);
        initBarChart(barChartBlueFrequency);
        initLineChart(lineChartBlueTrend);

        //红色球出现频率
        vm.redBallFrequency().observe(this, myChartData -> {
            BarDataSet set = (BarDataSet) myChartData.getChartData().getDataSets().get(0);
            set.setHighLightAlpha(10);
            set.setColor(ContextCompat.getColor(SsqAnalysisActivity.this, R.color.pink_700));
            barChartRedFrequency.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return myChartData.getSourceData().get((int) value).getBallNo();
                }
            });
            barChartRedFrequency.setData(myChartData.getChartData());
            barChartRedFrequency.invalidate();
        });

        //蓝色球出现频率
        vm.blueBallFrequency().observe(this, myChartData -> {
            BarDataSet set = (BarDataSet) myChartData.getChartData().getDataSets().get(0);
            set.setHighLightAlpha(10);
            set.setColor(ContextCompat.getColor(SsqAnalysisActivity.this, R.color.indigo_700));
            barChartBlueFrequency.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return myChartData.getSourceData().get((int) value).getBallNo();
                }
            });
            barChartBlueFrequency.getXAxis().setAxisMaximum(myChartData.getSourceData().size());
            barChartBlueFrequency.setData(myChartData.getChartData());
            barChartBlueFrequency.invalidate();
        });

        //蓝色球出现趋势
        vm.blueBallTrend().observe(this, myChartData -> {
            LineDataSet set = (LineDataSet) myChartData.getChartData().getDataSets().get(0);
            set.setDrawCircles(false);
            set.setColor(ContextCompat.getColor(SsqAnalysisActivity.this, R.color.indigo_700));
            lineChartBlueTrend.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return myChartData.getSourceData().get((int) value).getResultDate();
                }
            });
            int size = myChartData.getSourceData().size();
            //每一页最少30
            lineChartBlueTrend.setScaleMinima(size / 30f, 1f);
            lineChartBlueTrend.setVisibleXRangeMaximum(30);
            lineChartBlueTrend.setVisibleXRangeMinimum(60);
            lineChartBlueTrend.setData(myChartData.getChartData());
            lineChartBlueTrend.invalidate();
        });
    }


    private void initBarChart(BarChart chart) {
        chart.setTouchEnabled(true);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setNoDataText(getString(R.string.lottery_chart_no_data));
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getDescription().setEnabled(false);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0.0f);
        yAxis.setValueFormatter(new PercentFormatter());
    }

    private void initLineChart(LineChart chart) {
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setNoDataText(getString(R.string.lottery_chart_no_data));
        chart.getAxisRight().setEnabled(false);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setDragXEnabled(true);
        chart.setDragYEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(1f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setLabelCount(5, true);
    }
}
