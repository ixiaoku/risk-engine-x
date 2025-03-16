package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import risk.engine.common.es.ElasticsearchRestApi;
import risk.engine.db.entity.EngineResult;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 处理业务请求数据
 * @Author: X
 * @Date: 2025/3/16 13:39
 * @Version: 1.0
 */
@Slf4j
@Component
public class RiskEngineHandler {

    @Resource
    private IEngineResultService engineResultService;

    @Resource
    private ElasticsearchRestApi elasticsearchRestApi;

    /**
     * 保存数据
     * @param executeEngineDTO 参数
     */
    public void saveDataAndPenalty(RiskExecuteEngineDTO executeEngineDTO) {
        engineResultService.insert(getEngineResult(executeEngineDTO));
        insertEsEngineResult(executeEngineDTO);
        doPenalty(executeEngineDTO);
    }

    /**
     * 处罚
     * @param executeEngineDTO 参数
     */
    private void doPenalty(RiskExecuteEngineDTO executeEngineDTO) {



    }

    /**
     * 保存es
     * @param engineResult 参数
     */
    private void insertEsEngineResult(RiskExecuteEngineDTO engineResult) {
        System.out.println(new Gson().toJson(engineResult));
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(engineResult));
        map.put("id", UUID.randomUUID().toString());
        mapList.add(map);
        elasticsearchRestApi.saveDocument("engine-record-202501", mapList);
        log.info("es保存成功");
    }

    /**
     * 转换引擎执行结果
     * @param executeEngineDTO 参数
     * @return 结果
     */
    private EngineResult getEngineResult(RiskExecuteEngineDTO executeEngineDTO) {
        EngineResult engineResult = new EngineResult();
        engineResult.setFlowNo(executeEngineDTO.getFlowNo());
        engineResult.setRiskFlowNo(executeEngineDTO.getRiskFlowNo());
        engineResult.setRequestPayload(executeEngineDTO.getRequestPayload());
        engineResult.setIncidentCode(executeEngineDTO.getIncidentCode());
        engineResult.setIncidentName(executeEngineDTO.getIncidentName());
        engineResult.setRuleCode(executeEngineDTO.getRuleCode());
        engineResult.setRuleName(executeEngineDTO.getRuleName());
        engineResult.setRuleStatus(executeEngineDTO.getRuleStatus());
        engineResult.setRuleScore(executeEngineDTO.getRuleScore());
        engineResult.setRuleDecisionResult(executeEngineDTO.getRuleDecisionResult());
        engineResult.setRuleLabel(executeEngineDTO.getRuleLabel());
        engineResult.setRulePenaltyAction(executeEngineDTO.getRulePenaltyAction());
        engineResult.setRuleVersion(executeEngineDTO.getRuleVersion());
        engineResult.setCreateTime(executeEngineDTO.getCreateTime());
        engineResult.setHitMockRules(new Gson().toJson(executeEngineDTO.getHitMockRules()));
        engineResult.setHitOnlineRules(new Gson().toJson(executeEngineDTO.getHitOnlineRules()));
        engineResultService.insert(engineResult);
        return engineResult;
    }

}
