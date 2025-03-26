package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.crawler.monitor.binance.MarketNoticeMonitorHandler;

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

    @PostMapping("/request")
    public void request() {
        marketNoticeMonitorHandler.start();
    }

}
