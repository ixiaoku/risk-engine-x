package risk.engine.crawler.monitor.telegram;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.entity.CrawlerTaskPO;
import risk.engine.dto.dto.crawler.CrawlerNoticeDTO;
import risk.engine.dto.enums.IncidentCodeEnum;
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
//@Component
public class TelegramBotHandler implements LongPollingBot, ApplicationRunner {

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

    public final List<String> USERNAME_LIST = List.of("btcismoonflyqaq", "OfficialBinanceFeedBot", "t00b_bot");


    @Override
    public void onUpdateReceived(Update update) {
        List<CrawlerTaskPO> crawlerTaskList = new ArrayList<>();
        //监听频道
        if (update.hasChannelPost()) {
            Chat chat = update.getChannelPost().getChat();
            if (Objects.isNull(chat)) {
                return;
            }
            String channelUsername = chat.getUserName();
            if (StringUtils.equals(channelUsername, CHANNEL_USERNAME) || StringUtils.equals(channelUsername, "binance_cn")) {
                Message message = update.getChannelPost();
                if (Objects.isNull(message) || StringUtils.isEmpty(message.getText())) {
                    return;
                }
                String messageText = message.getText();
                //组装告警消息报文体
                CrawlerNoticeDTO noticeDTO = CrawlerNoticeDTO.getCrawlerNoticeDTO("Telegram", 1,
                        IncidentCodeEnum.TELEGRAM_BEW_NEWS.getDesc(), messageText, DateTimeUtil.getCurrentDateTime());
                String flowNo = update.getChannelPost().getChatId() + update.getChannelPost().getForwardFromMessageId().toString();
                CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(flowNo, IncidentCodeEnum.TELEGRAM_BEW_NEWS.getCode(), JSON.toJSONString(noticeDTO));
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
            if (StringUtils.isEmpty(message.getText()) || !USERNAME_LIST.contains(senderUsername)) {
                return;
            }
            //组装告警消息报文体
            String messageText = message.getText();
            CrawlerNoticeDTO noticeDTO = CrawlerNoticeDTO.getCrawlerNoticeDTO("Telegram", 1,
                    IncidentCodeEnum.TELEGRAM_BEW_NEWS.getDesc(), messageText, DateTimeUtil.getTimeByTimestamp(message.getDate().longValue() * 1000));
            String flowNo = message.getMessageId().toString() + "_" + message.getDate().toString();
            CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(flowNo, IncidentCodeEnum.TELEGRAM_BEW_NEWS.getCode(), JSON.toJSONString(noticeDTO));
            if (Objects.isNull(crawlerTask)) {
                return;
            }
            crawlerTaskList.add(crawlerTask);
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
        log.info("Bot 手动注册成功");
    }
}