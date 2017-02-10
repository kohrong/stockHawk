package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.udacity.stockhawk.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class DetailActivity extends AppCompatActivity {

    private String symbol;
    private Map<String, Stock> stockMap;
    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        symbol = intent.getExtras().getString(MainActivity.SYMBOL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (symbol != null) {
            getSupportActionBar().setTitle(symbol);
            new GetStockOperation().execute(symbol);
            mChart = (BarChart) findViewById(R.id.bar_chart);
        }
    }

    private class GetStockOperation extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... symbol) {
            try {
                stockMap = YahooFinance.get(symbol, true);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            updateChart();
        }
    }

    private void updateChart() {
        if(mChart != null) {
            Stock stock = stockMap.get(symbol);
            try {
                setData(stock.getHistory().size(), stock.getHistory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setData(int count, List<HistoricalQuote> historicalQuotes) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            yVals1.add(new BarEntry(i, historicalQuotes.get(i-1).getAdjClose().floatValue()));
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals1, getString(R.string.historical));
        set1.setColor(getResources().getColor(R.color.colorPrimaryDark));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        mChart.setData(data);
        mChart.animateY(3000);
    }
}
