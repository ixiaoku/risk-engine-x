package risk.engine.rest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: X
 * @Date: 2025/3/9 18:58
 * @Version: 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"risk.engine.rest", "risk.engine.service", "risk.engine.db", "risk.engine.common", "risk.engine.crawler"})
@MapperScan("risk.engine.db.dao")
public class RiskEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskEngineApplication.class, args);
    }

}
