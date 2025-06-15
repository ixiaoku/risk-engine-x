package risk.engine.admin;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/6/11 15:55
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = "risk.engine.admin")
public class ApplicationTestLiteFlow implements CommandLineRunner {

    @Resource
    private FlowExecutor flowExecutor;
    @Resource
    private RuleService ruleService;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTestLiteFlow.class, args);
    }

    @Override
    public void run(String... args) {
        // 测试用例1：存款事件（触发rule1, rule2, rule4）
        Map<String, Object> depositData = new HashMap<>();
        depositData.put("userKyc", 1);
        depositData.put("amount", new BigDecimal("12000"));
        depositData.put("ip", "127.0.0.1");
        depositData.put("txId", "tx123");
        RiskContext depositContext = new RiskContext();
        depositContext.setEventType("DEPOSIT");
        depositContext.setData(depositData);
        LiteflowResponse depositResponse = flowExecutor.execute2Resp("depositChain", args, depositContext);
        System.out.println("Deposit event results: " + depositContext.getRuleResults());

        // 测试用例2：提币事件（触发rule3）
        Map<String, Object> withdrawData = new HashMap<>();
        withdrawData.put("userKyc", 0);
        withdrawData.put("amount", new BigDecimal("5000"));
        withdrawData.put("ip", "192.168.1.1");
        withdrawData.put("deviceId", "device123");
        withdrawData.put("countryCode", "CN");
        RiskContext withdrawContext = new RiskContext();
        withdrawContext.setEventType("WITHDRAWAL");
        withdrawContext.setData(withdrawData);
        LiteflowResponse withdrawResponse = flowExecutor.execute2Resp("withdrawalChain", args, withdrawContext);
        System.out.println("Withdrawal event results: " + withdrawContext.getRuleResults());
    }

}
