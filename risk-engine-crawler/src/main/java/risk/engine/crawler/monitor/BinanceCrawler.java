package risk.engine.crawler.monitor;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.common.util.OkHttpUtil;
import risk.engine.dto.dto.crawler.GroupChatBotDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

@Slf4j
public class BinanceCrawler {

    private final static String weChatBotUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4b9616dd-d798-46ba-af63-3bd0cda405a3";

    private final static String botContent = "监控结果通知\n" +
            ">监控项目:<font color=\"comment\">Binance公告1</font>\n" +
            ">内容:<font color=\"comment\">%s</font>\n" +
            ">时间:<font color=\"comment\">%s</font>";;

    public static void main(String[] args) {
        String url = "https://www.binance.com/bapi/composite/v1/public/market/notice/get?page=1&rows=20";
        String keyword = "Binance Futures Will Launch";


        try {
            // 发送 HTTP GET 请求
            String jsonResponse = fetchData(url);
            if (jsonResponse != null) {
                System.out.println("原始响应: " + jsonResponse); // 调试用，打印原始 JSON
                // 解析 JSON 并提取 title
                extractTitles(jsonResponse, keyword);
            } else {
                System.out.println("请求失败，未获取到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送 HTTP GET 请求并返回响应内容
    private static String fetchData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate"); // 支持 GZIP
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 检查是否使用了 GZIP 压缩
            String contentEncoding = connection.getHeaderField("Content-Encoding");
            BufferedReader reader;
            if ("gzip".equalsIgnoreCase(contentEncoding)) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            return response.toString();
        } else {
            log.error("HTTP 请求失败，响应码: {}", responseCode);
            connection.disconnect();
            return null;
        }
    }

    // 使用 Gson 解析 JSON 并提取 title 字段
    private static void extractTitles(String jsonResponse, String keyword) {
        JsonObject rootObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        // 检查返回状态
        String code = rootObject.get("code").getAsString();
        if (!"000000".equals(code)) {
            System.out.println("API 返回错误，code: " + code + ", message: " + rootObject.get("message"));
            return;
        }

        String typeValue = "New Cryptocurrency Listing";

        // 获取 data 数组
        JsonArray dataArray = rootObject.getAsJsonArray("data");
        GroupChatBotDTO groupChatBotDTO = new GroupChatBotDTO();
        groupChatBotDTO.setMsgtype("markdown");
        GroupChatBotDTO.Markdown markdown = new GroupChatBotDTO.Markdown();
        if (dataArray != null) {
            for (JsonElement element : dataArray) {
                JsonObject dataObject = element.getAsJsonObject();
                String title = dataObject.get("title").getAsString();
                String time = DateTimeUtil.getTimeByTimestamp(dataObject.get("time").getAsLong());
                String type = dataObject.get("type").getAsString();


                String content = String.format(botContent, title, time);
                if (title != null && typeValue.equals(type)) {
                    System.out.println("找到匹配标题: " + title);
                    markdown.setContent(content);
                    groupChatBotDTO.setMarkdown(markdown);
                    System.out.println(gson.toJson(groupChatBotDTO));
                    String result = OkHttpUtil.post(weChatBotUrl, gson.toJson(groupChatBotDTO));
                    System.out.println("机器人发消息成功: " + result);
                    break;
                }
            }
        } else {
            System.out.println("未找到 data 数组");
        }
    }
}