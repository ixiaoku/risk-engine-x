package risk.engine.crawler.monitor;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: X
 * @Date: 2025/3/9 18:58
 * @Version: 1.0
 */
@Slf4j
@SpringBootApplication()
@ComponentScan(basePackages = {"risk.engine.crawler", "risk.engine.db", "risk.engine.common"})
@MapperScan("risk.engine.db.dao")
public class RiskEngineCrawlerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(RiskEngineCrawlerApplication.class, args);
            log.info("HELLO WORLD ----->>> 服务crawler启动成功 -------->>> SUCCESS" );
        } catch (Exception e) {
            log.error("服务crawler启动报错信息：{}", e.getMessage(), e);
            throw e;
        }
    }
}
