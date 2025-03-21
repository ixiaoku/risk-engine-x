package risk.engine.service.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.Incident;
import risk.engine.dto.dto.IncidentDTO;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.service.service.IIncidentService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/20 17:16
 * @Version: 1.0
 */

@Slf4j
@Component
public class GuavaStartupCache {

    @Resource
    private IIncidentService incidentService;

    private final Cache<String, IncidentDTO> cache = CacheBuilder.newBuilder()
            .maximumSize(100) // 限制最大存储 1000 条数据
            .build();

    @PostConstruct
    public void loadCache() {
        // 1. 启动时手动预加载数据
        ConcurrentHashMap<String, IncidentDTO> data = getDataFromDB(); // 预加载数据库数据
        cache.putAll(data);
        log.info("缓存已初始化完成！");
    }

    public IncidentDTO getIncident(String incidentCode) {
        return cache.getIfPresent(incidentCode);
    }

    /**
     * 初始化 事件数据
     * @return 结果
     */
    private ConcurrentHashMap<String, IncidentDTO> getDataFromDB() {
        ConcurrentHashMap<String, IncidentDTO> data = new ConcurrentHashMap<>();
        Incident incident = new Incident();
        incident.setStatus(IncidentStatusEnum.ONLINE.getCode());
        List<Incident> incidentList = incidentService.selectByExample(incident);
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
