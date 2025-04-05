package risk.engine.service.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.IncidentPO;
import risk.engine.dto.dto.IncidentDTO;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.service.service.IIncidentService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 事件缓存
 * @Author: X
 * @Date: 2025/3/20 17:16
 * @Version: 1.0
 */

@Slf4j
@Component
public class GuavaIncidentCache {

    @Resource
    private IIncidentService incidentService;

    private final Cache<String, IncidentDTO> incidentCache = CacheBuilder.newBuilder()
            .maximumSize(256)
            .build();

    @PostConstruct
    private void loadCache() {
        ConcurrentHashMap<String, IncidentDTO> incidentMap = selectIncidentList();
        incidentCache.putAll(incidentMap);
    }

    /**
     * 获取事件缓存 如果为空主动加载
     * @param incidentCode 事件code
     * @return 结果
     */
    public IncidentDTO getCache(String incidentCode) {
        IncidentDTO incidentDTO = incidentCache.getIfPresent(incidentCode);
        if (Objects.nonNull(incidentDTO)) {
            return incidentDTO;
        }
        ConcurrentHashMap<String, IncidentDTO> concurrentHashMap = selectIncidentList();
        return concurrentHashMap.get(incidentCode);
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        CompletableFuture.runAsync(() -> {
            ConcurrentHashMap<String, IncidentDTO> newData = selectIncidentList();
            incidentCache.invalidateAll();
            incidentCache.putAll(newData);
            log.info("Incident缓存刷新完毕，条数：{}", newData.size());
        }).exceptionally(ex -> {
            log.error("刷新缓存 异步任务失败, 异常: {}", ex.getMessage(), ex);
            return null;
        });
    }

    /**
     * 查库缓存事件
     * @return 结果
     */
    private ConcurrentHashMap<String, IncidentDTO> selectIncidentList() {
        ConcurrentHashMap<String, IncidentDTO> data = new ConcurrentHashMap<>();
        IncidentPO incident = new IncidentPO();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<IncidentPO> incidentList = incidentService.selectByExample(incident);
        if (CollectionUtils.isEmpty(incidentList)) {
            return data;
        }
        data = (ConcurrentHashMap<String, IncidentDTO>) incidentList.stream().map(i -> {
            IncidentDTO incidentDTO = new IncidentDTO();
            incidentDTO.setIncidentCode(i.getIncidentCode());
            incidentDTO.setIncidentName(i.getIncidentName());
            incidentDTO.setDecisionResult(i.getDecisionResult());
            incidentDTO.setRequestPayload(i.getRequestPayload());
            return incidentDTO;
        }).collect(Collectors.toConcurrentMap(IncidentDTO::getIncidentCode, Function.identity()));
        return data;
    }

}
