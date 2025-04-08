//package risk.engine.service.common.cache;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import risk.engine.common.redis.RedisUtil;
//import risk.engine.db.entity.IncidentPO;
//import risk.engine.db.entity.RulePO;
//import risk.engine.dto.enums.IncidentStatusEnum;
//import risk.engine.service.service.IIncidentService;
//import risk.engine.service.service.IRuleService;
//
//import javax.annotation.Resource;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CompletableFuture;
//
///**
// * @Author: X
// * @Date: 2025/4/8 19:13
// * @Version: 1.0
// */
//@Slf4j
//@Component
//public class RedisRuleCache implements ApplicationRunner {
//
//    @Resource
//    private RedisUtil redisUtil;
//
//    @Resource
//    private IRuleService ruleService;
//
//    @Resource
//    private IIncidentService incidentService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        // 启动时加载规则和事件
//        loadRulesToRedis();
//    }
//
//    /**
//     * 加载规则到 Redis
//     */
//    private void loadRulesToRedis() {
//        // 查询所有上线事件
//        IncidentPO incidentQuery = new IncidentPO();
//        incidentQuery.setStatus(IncidentStatusEnum.ONLINE.getCode());
//        List<IncidentPO> incidents = incidentService.selectByExample(incidentQuery);
//        if (CollectionUtils.isEmpty(incidents)) {
//            log.error("No online incidents found");
//            return;
//        }
//
//        // 按事件分组加载规则
//        for (IncidentPO incident : incidents) {
//            List<RulePO> rules = ruleService.selectByIncidentCode(incident.getIncidentCode());
//            if (CollectionUtils.isEmpty(rules)) {
//                continue;
//            }
//
//            // 存规则脚本
//            for (RulePO rule : rules) {
//                String scriptKey = "Script:" + rule.getRuleCode();
//                redisUtil.set(scriptKey, rule.getGroovyScript(), 3600); // 过期时间 1 小时
//            }
//
//            // 存事件下的规则列表
//            String incidentKey = "Incident:" + incident.getIncidentCode();
//            redisUtil.set(incidentKey, rules, 3600); // 存整个 RulePO 列表
//        }
//        log.info("Loaded {} incidents and their rules to Redis", incidents.size());
//    }
//
//    /**
//     * 获取脚本
//     */
//    public String getScript(String ruleCode) {
//        String key = "Script:" + ruleCode;
//        return Objects.isNull(redisUtil.get(key)) ? null : redisUtil.get(key).toString();
//    }
//
//    /**
//     * 获取事件下的规则列表
//     */
//    public List<RulePO> getRulesByIncidentCode(String incidentCode) {
//        String key = "Incident:" + incidentCode;
//        Object value = redisUtil.get(key);
//        return Objects.isNull(value) ? (List<RulePO>) value : Collections.emptyList();
//    }
//
//    /**
//     * 刷新缓存
//     */
//    public void refreshCache() {
//        CompletableFuture.runAsync(() -> {
//            loadRulesToRedis();
//            log.info("Redis rule cache refreshed");
//        }).exceptionally(ex -> {
//            log.error("Failed to refresh Redis cache: {}", ex.getMessage(), ex);
//            return null;
//        });
//    }
//}
