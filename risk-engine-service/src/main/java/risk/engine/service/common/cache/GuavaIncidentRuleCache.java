package risk.engine.service.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.dao.RuleMapper;
import risk.engine.db.entity.IncidentPO;
import risk.engine.db.entity.RulePO;
import risk.engine.db.entity.example.RuleExample;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.RuleStatusEnum;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: X
 * @Date: 2025/4/8 19:19
 * @Version: 1.0
 */
@Slf4j
@Component
public class GuavaIncidentRuleCache implements ApplicationRunner {

    @Resource
    private IncidentMapper incidentMapper;

    @Resource
    private RuleMapper ruleMapper;

    private static final GroovyShell groovyShell = new GroovyShell();

    private static final String CACHE_KEY = "CacheKey:";

    private final Cache<String, List<RulePO>> ruleCache = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private final Cache<String, IncidentPO> incidentCache = CacheBuilder.newBuilder()
            .maximumSize(128)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private final Map<String, Script> scriptCache = new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        loadRulesToCache();
    }

    /**
     * 加载规则到缓存
     */
    private void loadRulesToCache() {
        long start = System.currentTimeMillis();
        IncidentPO query = new IncidentPO();
        query.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<IncidentPO> incidentList = incidentMapper.selectByExample(query);
        if (CollectionUtils.isEmpty(incidentList)) {
            log.error("No online incidents found");
            return;
        }
        for (IncidentPO incident : incidentList) {
            //加载事件缓存
            incidentCache.put(CACHE_KEY + incident.getIncidentCode(), incident);
            List<Integer> statusList = List.of(RuleStatusEnum.ONLINE.getCode(), RuleStatusEnum.MOCK.getCode());
            RuleExample ruleExample = new RuleExample();
            ruleExample.setIncidentCode(incident.getIncidentCode());
            ruleExample.setStatusList(statusList);
            List<RulePO> rules = ruleMapper.selectByIncidentCode(ruleExample);
            if (CollectionUtils.isEmpty(rules)) {
                continue;
            }
            //加载规则缓存
            ruleCache.put(CACHE_KEY + incident.getIncidentCode(), rules);
            //加载rule缓存
            rules.forEach(rule -> scriptCache.computeIfAbsent(CACHE_KEY + rule.getRuleCode(),
                    k -> groovyShell.parse(rule.getGroovyScript())));
        }
    }

    /**
     * 获取规则缓存
     * @param incidentCode 事件code
     * @return 结果
     */
    public List<RulePO> getCacheRuleList(String incidentCode) {
        List<RulePO> rules = ruleCache.getIfPresent(CACHE_KEY + incidentCode);
        if (CollectionUtils.isEmpty(rules)) {
            loadRulesToCache();
            return ruleCache.getIfPresent(CACHE_KEY + incidentCode);
        }
        return rules;
    }

    /**
     * 获取事件缓存
     * @param incidentCode 事件code
     * @return 结果
     */
    public IncidentPO getCacheIncident(String incidentCode) {
        IncidentPO incidentPO = incidentCache.getIfPresent(CACHE_KEY + incidentCode);
        if (Objects.isNull(incidentPO)) {
            loadRulesToCache();
            return incidentCache.getIfPresent(CACHE_KEY + incidentCode);
        }
        return incidentPO;
    }

    /**
     * 获取预编译脚本
     * @param ruleCode 规则code
     * @return 结果
     */
    public Script getCacheScript(String ruleCode) {
        return scriptCache.get(CACHE_KEY + ruleCode);
    }

    /**
     * 异步刷新缓存
     */
    public void refreshCache() {
        CompletableFuture.runAsync(() -> {
            ruleCache.invalidateAll();
            scriptCache.clear();
            incidentCache.invalidateAll();
            loadRulesToCache();
        }).exceptionally(ex -> {
            log.error("Failed to refresh Guava cache: {}", ex.getMessage(), ex);
            return null;
        });
    }

}
