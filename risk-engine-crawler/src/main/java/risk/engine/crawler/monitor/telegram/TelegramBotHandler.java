package risk.engine.crawler.monitor.telegram;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
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
@Component
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

    public final List<String> USERNAME_LIST = List.of("btcismoonflyqaq");
    public final List<String> BOT_USERNAME_LIST = List.of(
            "OfficialBinanceFeedBot", "t00b_bot", "xxbit_bot",
            "GMGNAI_bot", "oooooyoung_bot", "Alert_GMGNBOT"
    );

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update receive: {}", JSON.toJSONString(update));

        List<CrawlerTaskPO> crawlerTaskList = new ArrayList<>();

        // 监听频道消息
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
                // 组装告警消息报文体
                CrawlerNoticeDTO noticeDTO = CrawlerNoticeDTO.getCrawlerNoticeDTO(
                        "Telegram", 1,
                        IncidentCodeEnum.TELEGRAM_BEW_NEWS.getDesc(),
                        messageText,
                        DateTimeUtil.getCurrentDateTime()
                );
                String flowNo = update.getChannelPost().getChatId() + update.getChannelPost().getForwardFromMessageId().toString();
                CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(
                        flowNo, IncidentCodeEnum.TELEGRAM_BEW_NEWS.getCode(), JSON.toJSONString(noticeDTO)
                );
                if (Objects.isNull(crawlerTask)) {
                    return;
                }
                crawlerTaskList.add(crawlerTask);
            }
        }

        // 监听群组消息（用户和 Bot）
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (!StringUtils.equals(chatId, GROUP_CHAT_ID)) {
                return;
            }

            String messageText = message.getText();
            if (StringUtils.isEmpty(messageText)) {
                return;
            }

            // 检查消息发送者
            String senderUsername = null;
            boolean isBotMessage;

            // 1. 尝试通过 message.getFrom() 获取发送者（普通用户）
            User user = message.getFrom();
            if (Objects.nonNull(user)) {
                senderUsername = user.getUserName();
                if (USERNAME_LIST.contains(senderUsername)) {
                    log.info("监听到用户发言 - 用户名：{}, 消息内容：{}", senderUsername, messageText);
                    processMessage(message, crawlerTaskList, messageText);
                }
            }

            // 2. 尝试通过 message.getSenderChat() 获取发送者（可能是 Bot 或匿名管理员）
            Chat senderChat = message.getSenderChat();
            if (Objects.nonNull(senderChat)) {
                senderUsername = senderChat.getUserName();
                isBotMessage = senderChat.isGroupChat();  // 检查是否为 Bot
                if (isBotMessage && BOT_USERNAME_LIST.contains(senderUsername)) {
                    log.info("监听到 Bot 发言 - Bot 用户名：{}, 消息内容：{}", senderUsername, messageText);
                    processMessage(message, crawlerTaskList, messageText);
                }
            }

            // 3. 如果 senderUsername 仍为空，尝试通过 authorSignature（匿名管理员或特殊情况）
            if (StringUtils.isEmpty(senderUsername)) {
                String authorSignature = message.getAuthorSignature();
                if (StringUtils.isNotEmpty(authorSignature)) {
                    log.info("监听到匿名消息 - AuthorSignature：{}, 消息内容：{}", authorSignature, messageText);
                    // 如果需要处理匿名消息，可以在这里添加逻辑
                }
            }
        }

        // 异步插入任务
        if (!crawlerTaskList.isEmpty()) {
            CompletableFuture.runAsync(() -> crawlerTaskService.batchInsert(crawlerTaskList)).exceptionally(ex -> {
                log.error("监听 Telegram 异步任务失败, 异常: {}", ex.getMessage(), ex);
                return null;
            });
        }
    }

    // 处理消息的通用方法
    private void processMessage(Message message, List<CrawlerTaskPO> crawlerTaskList, String messageText) {
        CrawlerNoticeDTO noticeDTO = CrawlerNoticeDTO.getCrawlerNoticeDTO(
                "Telegram", 1,
                IncidentCodeEnum.TELEGRAM_BEW_NEWS.getDesc(),
                messageText,
                DateTimeUtil.getTimeByTimestamp(message.getDate().longValue() * 1000)
        );
        String flowNo = message.getMessageId().toString() + "_" + message.getDate().toString();
        CrawlerTaskPO crawlerTask = crawlerTaskService.getCrawlerTask(
                flowNo, IncidentCodeEnum.TELEGRAM_BEW_NEWS.getCode(), JSON.toJSONString(noticeDTO)
        );
        if (Objects.nonNull(crawlerTask)) {
            crawlerTaskList.add(crawlerTask);
        }
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