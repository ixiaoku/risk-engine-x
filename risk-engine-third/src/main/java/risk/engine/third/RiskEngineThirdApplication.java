package risk.engine.third;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @Author: X
 * @Date: 2025/3/9 18:58
 * @Version: 1.0
 */
@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class RiskEngineThirdApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(RiskEngineThirdApplication.class, args);
            log.info("HELLO WORLD ----->>> 服务third启动成功 -------->>> SUCCESS" );
        } catch (Exception e) {
            log.error("服务启动报错信息：{}", e.getMessage(), e);
            throw e;
        }
    }
}
