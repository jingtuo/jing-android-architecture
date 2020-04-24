package com.jing.android.arch.demo.ui.model;

/**
 *
 * @param <CD>
 * @param <SD>
 */
public class MyChartData<CD, SD> {

    private CD chartData;

    private SD sourceData;

    public CD getChartData() {
        return chartData;
    }

    public void setChartData(CD chartData) {
        this.chartData = chartData;
    }

    public SD getSourceData() {
        return sourceData;
    }

    public void setSourceData(SD sourceData) {
        this.sourceData = sourceData;
    }
}
