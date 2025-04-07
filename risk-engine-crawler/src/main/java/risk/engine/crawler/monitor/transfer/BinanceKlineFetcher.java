package risk.engine.crawler.monitor.transfer;

/**
 * @Author: X
 * @Date: 2025/4/7 10:56
 * @Version: 1.0
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BinanceKlineFetcher {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "https://api.binance.com";

    // 获取指定交易对、周期的 K 线数据
    public static List<Kline> fetchKlines(String symbol, String interval, int limit) throws IOException {
        String url = BASE_URL + "/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JsonArray jsonArray = JsonParser.parseString(response.body().string()).getAsJsonArray();
            List<Kline> klineList = new ArrayList<>();

            for (JsonElement element : jsonArray) {
                JsonArray arr = element.getAsJsonArray();
                Kline kline = new Kline(
                        arr.get(0).getAsLong(),  // openTime
                        arr.get(1).getAsDouble(),// open
                        arr.get(2).getAsDouble(),// high
                        arr.get(3).getAsDouble(),// low
                        arr.get(4).getAsDouble(),// close
                        arr.get(5).getAsDouble(),// volume
                        arr.get(6).getAsLong()   // closeTime
                );
                klineList.add(kline);
            }
            return klineList;
        }
    }

    // 计算布林带（基于收盘价）
    public static void calculateBollingerBands(List<Kline> klines, int period) {
        if (klines.size() < period) return;

        for (int i = period - 1; i < klines.size(); i++) {
            List<Double> closes = new ArrayList<>();
            for (int j = i - period + 1; j <= i; j++) {
                closes.add(klines.get(j).getClose());
            }
            double ma = closes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double std = Math.sqrt(closes.stream().mapToDouble(c -> Math.pow(c - ma, 2)).sum() / period);

            klines.get(i).setBollingerBands(ma, ma + 2 * std, ma - 2 * std);
        }
    }

    public static void main(String[] args) throws IOException {
        String symbol = "BTCUSDT";
        String interval = "5m";
        int limit = 21; // 最少要20根K线计算布林带

        List<Kline> klines = fetchKlines(symbol, interval, limit);
        calculateBollingerBands(klines, 20);

        Kline latest = klines.get(klines.size() - 1);
        double changePercent = ((latest.getClose() - latest.getOpen()) / latest.getOpen()) * 100;

        System.out.printf("时间段: %s - %s%n",
                new Date(latest.getOpenTime()), new Date(latest.getCloseTime()));
        System.out.printf("开盘价: %.2f, 收盘价: %.2f, 涨跌幅: %.2f%%%n",
                latest.getOpen(), latest.getClose(), changePercent);
        System.out.printf("布林中轨: %.2f, 上轨: %.2f, 下轨: %.2f%n",
                latest.getMiddleBand(), latest.getUpperBand(), latest.getLowerBand());

        // 示例：如果收盘价跌破下轨
        if (latest.getClose() < latest.getLowerBand()) {
            System.out.println("⚠️ 收盘价跌破布林带下轨，可能是买入信号");
        }
    }

    // K线数据结构
    public static class Kline {
        private long openTime;
        private double open, high, low, close, volume;
        private long closeTime;
        private double middleBand, upperBand, lowerBand;

        public Kline(long openTime, double open, double high, double low, double close, double volume, long closeTime) {
            this.openTime = openTime;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
            this.closeTime = closeTime;
        }

        public double getOpen() { return open; }
        public double getClose() { return close; }
        public long getOpenTime() { return openTime; }
        public long getCloseTime() { return closeTime; }

        public void setBollingerBands(double middle, double upper, double lower) {
            this.middleBand = middle;
            this.upperBand = upper;
            this.lowerBand = lower;
        }

        public double getMiddleBand() { return middleBand; }
        public double getUpperBand() { return upperBand; }
        public double getLowerBand() { return lowerBand; }
    }
}