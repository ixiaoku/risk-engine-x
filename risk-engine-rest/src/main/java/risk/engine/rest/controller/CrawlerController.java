package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.crawler.monitor.log.BitcoinMonitor;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/13 19:50
 * @Version: 1.0
 */
@RestController
@RequestMapping("/crawler")
@Slf4j
public class CrawlerController {

    @Resource
    private BitcoinMonitor bitcoinMonitor;

    @PostMapping("/btc/block")
    public void execute() {
        try {
            bitcoinMonitor.start();
        } catch (Exception e) {
            System.err.println("启动失败: " + e.getMessage());
        }
    }

}
