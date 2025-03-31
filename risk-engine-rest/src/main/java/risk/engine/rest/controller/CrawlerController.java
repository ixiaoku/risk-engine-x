package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import risk.engine.crawler.monitor.binance.MarketNoticeMonitorHandler;
import risk.engine.crawler.monitor.telegram.TelegramBotHandler;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/24 17:05
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    @Resource
    private MarketNoticeMonitorHandler marketNoticeMonitorHandler;

    @Resource
    private TelegramBotHandler telegramBotHandler;

    @PostMapping("/request")
    public void request() {
        marketNoticeMonitorHandler.start();
    }

    @PostMapping("/bot")
    public void bot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBotHandler);
        log.info("Bot 手动注册成功");
    }

}
