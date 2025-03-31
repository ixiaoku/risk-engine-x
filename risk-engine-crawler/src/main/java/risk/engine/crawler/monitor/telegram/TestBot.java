package risk.engine.crawler.monitor.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

/**
 * @Author: X
 * @Date: 2025/3/28 20:30
 * @Version: 1.0
 */
@Slf4j
@Component
public class TestBot implements LongPollingBot {
    @Override
    public String getBotUsername() {
        return "xxbit_bot";
    }

    @Override
    public String getBotToken() {
        return "7617341376:AAHZ5Gw7X6fAF4myY7JuAXrT-vLJ3S7Pt_Y";
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("TestBot 收到更新: {}", update);
    }

    @Override
    public BotOptions getOptions() {
        return new DefaultBotOptions();
    }

    @Override
    public void clearWebhook() {
    }
}
