package risk.engine.admin;

import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/6/11 17:55
 * @Version: 1.0
 */
@Component
public class LiteFlowConfig {

    @Resource
    private RuleService ruleService;

    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    private RuleComponent ruleComponent;

    @PostConstruct
    public void init() {
        ruleService.getAllRules().forEach(e -> {
                    // 动态注册节点
                    LiteFlowNodeBuilder.createNode()
                            .setId(e.getNodeId())
                            .setName(e.getRuleId())
                            .setType(NodeTypeEnum.COMMON)
                            .setClazz(ruleComponent.getClass())
                            .build();
                });

        // 为每个事件类型动态构建 chain
        List<String> eventTypes = List.of("DEPOSIT", "WITHDRAWAL");
        for (String eventType : eventTypes) {
            String chainEL = ruleService.generateChainEL(eventType);
            if (chainEL == null || chainEL.isBlank()) {
                System.out.println("❌ Chain EL is empty for eventType: " + eventType + ", skip.");
                continue;
            }

            String chainId = eventType.toLowerCase() + "Chain";

            LiteFlowChainELBuilder.createChain()
                    .setChainName(chainId)
                    .setEL(chainEL)
                    .build();

            System.out.println("✅ Registered chain: " + chainId + " with EL: " + chainEL);
        }
    }
}