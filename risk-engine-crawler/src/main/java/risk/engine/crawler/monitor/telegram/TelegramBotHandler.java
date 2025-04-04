package risk.engine.crawler.monitor.telegram;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.dto.crawler.CrawlerNoticeDTO;
import risk.engine.service.service.ICrawlerTaskService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: X
 * @Date: 2025/3/28 13:14
 * @Version: 1.0
 */
@Slf4j
@Component
public class TelegramBotHandler implements LongPollingBot {

    @Resource
    private ICrawlerTaskService crawlerTaskService;

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;
    @Value("${telegram.bot.username}")
    private String BOT_USERNAME;
    @Value("${telegram.channel.username}")
    private String CHANNEL_USERNAME;
    @Value("${telegram.group.chat.id}")
    private String GROUP_CHAT_ID;
    @Value("${telegram.target.bot.username}")
    private String TARGET_BOT_USERNAME_DCR;

    public final String BINANCE_BOT_USERNAME = "OfficialBinanceFeedBot";

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived--------------------->start");
        List<CrawlerTaskPO> crawlerTaskList = new ArrayList<>();
        //监听频道
        if (update.hasChannelPost()) {
            Chat chat = update.getChannelPost().getChat();
            if (Objects.isNull(chat)) {
                return;
            }
            String channelUsername = chat.getUserName();
            if (StringUtils.equals(channelUsername, CHANNEL_USERNAME) || StringUtils.equals(channelUsername, "binance_cn")) {
                String messageText = update.getChannelPost().getText();
                if (StringUtils.isEmpty(messageText)) {
                    return;
                }
                log.info("监听频道发言 messageText:{}", messageText);
                CrawlerNoticeDTO noticeDTO = new CrawlerNoticeDTO();
                noticeDTO.setCreatedAt(DateTimeUtil.getCurrentDateTime());
                noticeDTO.setTitle(messageText);
                noticeDTO.setFlowNo(chat.getId().toString());
                CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(noticeDTO.getFlowNo(), "TelegramBEWnews", JSON.toJSONString(noticeDTO));
                if (Objects.isNull(crawlerTask)) {
                    return;
                }
                crawlerTaskList.add(crawlerTask);
            }
        }
        //监听群组某个用户发言
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (!StringUtils.equals(chatId, GROUP_CHAT_ID)) {
                return;
            }
            User from = message.getFrom();
            String senderUsername = Objects.isNull(from) ? null : from.getUserName();
            List<String> usernameList = List.of(TARGET_BOT_USERNAME_DCR, BINANCE_BOT_USERNAME);
            if (!usernameList.contains(senderUsername)) {
                return;
            }
            String messageText = message.getText();
            log.info("监听群组某个用户发言 messageText:{}", messageText);
            CrawlerNoticeDTO noticeDTO = new CrawlerNoticeDTO();
            noticeDTO.setCreatedAt(DateTimeUtil.getTimeByTimestamp(message.getDate().longValue() * 1000));
            noticeDTO.setTitle(messageText);
            noticeDTO.setFlowNo(message.getMessageId().toString() + "_" + message.getDate().toString());
            CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(noticeDTO.getFlowNo(), "TelegramBEWnews", JSON.toJSONString(noticeDTO));
            if (Objects.isNull(crawlerTask)) {
                return;
            }
            crawlerTaskList.add(crawlerTask);
            log.info("update.hasMessage()--------------------->end");
        }
        CompletableFuture.runAsync(() -> crawlerTaskService.batchInsert(crawlerTaskList)).exceptionally(ex -> {
            log.error("监听telegram 异步任务失败, 异常: {}", ex.getMessage(), ex);
            return null;
        });
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public BotOptions getOptions() {
        return new DefaultBotOptions();
    }

    @Override
    public void clearWebhook() {
    }

    @Override
    public void onClosing() {
        LongPollingBot.super.onClosing();
    }

}