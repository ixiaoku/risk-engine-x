package risk.engine.third.controller;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.third.gewe.AnnouncementDTO;
import risk.engine.third.gewe.MessageApi;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * "47530161525@chatroom"禁言群
 * @Author: X
 * @Date: 2025/4/25 21:16
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/wechat")
public class WeChatMessageController {

    private final static String appId = "wx_u27HapBW1Jabn4t8oSNss";
    private final static String PERSON_BOT_CONTENT =
            "\uD83D\uDEA8 【监控结果通知】\n" +
            "\uD83D\uDD14 【标题】: %s \n" +
            "\uD83D\uDD25 【内容】: %s \n" +
            "\uD83D\uDDD3 【时间】: %s \n";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/sendMsg")
    public ResponseEntity<String> sendMessage(@RequestBody AnnouncementDTO announcementDTO) {
        try {
            log.info("请求参数：{}", JSON.toJSONString(announcementDTO));
            String textContent = String.format(PERSON_BOT_CONTENT, announcementDTO.getTitle(), announcementDTO.getContent(), announcementDTO.getCreatedAt());
            List<String> toWechatIds = Arrays.asList("47530161525@chatroom");
            for (String toWechatId : toWechatIds) {
                MessageApi.postText(appId, toWechatId, textContent,"");
                int i = new Random().nextInt(6);
                try {
                    Thread.sleep(i*1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("发送失败: " + e.getMessage());
        }
        return ResponseEntity.status(200).body("发送成功:");
    }

    @PostMapping("/callback")
    public String handleCallback(@RequestBody String rawJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawJson);
            // 判断 TypeName
            String typeName = rootNode.path("TypeName").asText();
            if (!"AddMsg".equals(typeName)) {
                return "Ignored non-AddMsg type";
            }
            // 提取 Content
            JsonNode contentNode = rootNode.path("Data").path("Content").path("string");
            String content = contentNode.asText("");
            if (content.contains("/X")) {
                // 发现了带 "/" 的消息
                recordMessage(content, rootNode);
            }
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing JSON";
        }
    }

    private void recordMessage(String content, JsonNode fullData) {
        // 这里做你要的记录，比如打印、存数据库等等
        String fromUser = fullData.path("Data").path("FromUserName").path("string").asText("");
        String toUser = fullData.path("Data").path("ToUserName").path("string").asText("");
        long createTime = fullData.path("Data").path("CreateTime").asLong(0);
        System.out.println("检测到带斜杠消息！");
        System.out.println("消息内容: " + content);
        System.out.println("发送人: " + fromUser);
        System.out.println("接收人: " + toUser);
        System.out.println("发送时间: " + createTime);
        MessageApi.postText(appId, fromUser, "你好啊 贾宝玉 想死你了","");
    }

}
