package risk.engine.third.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.third.gewe.AnnouncementDTO;
import risk.engine.third.gewe.MessageApi;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Author: X
 * @Date: 2025/4/25 21:16
 * @Version: 1.0
 */
@RestController
public class WeChatMessageController {

    private final static String appId = "wx_XSy8zHvubFNgCNNUT_r3v";
    private final static String PERSON_BOT_CONTENT =
            "\uD83D\uDEA8 【监控结果通知】\n" +
            "\uD83D\uDD14 【标题】: %s \n" +
            "\uD83D\uDD25 【内容】: %s \n" +
            "\uD83D\uDDD3 【时间】: %s \n";

    @PostMapping("/sendMsg")
    public ResponseEntity<String> sendMessage(@RequestBody AnnouncementDTO announcementDTO) {
        try {
            String textContent = String.format(PERSON_BOT_CONTENT, announcementDTO.getTitle(), announcementDTO.getContent(), announcementDTO.getCreatedAt());
            List<String> toWechatIds = Arrays.asList("44760028169@chatroom", "52067326265@chatroom", "48977305404@chatroom");
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
}
