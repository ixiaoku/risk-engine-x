package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
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

@Slf4j
@Component
@RocketMQMessageListener(topic = "test_topic1", consumerGroup = "consumer-group-test1")
public class RiskEngineConsumer implements RocketMQListener<String> {

    @Resource
    private IEngineResultService engineResultService;

    @Resource
    private ElasticsearchRestApi elasticsearchRestApi;

    @Override
    public void onMessage(String message) {
        log.info("RiskEngineConsumer.onMessage: {}", message);
        RiskExecuteEngineDTO executeEngineDTO = new Gson().fromJson(message, RiskExecuteEngineDTO.class);
        engineResultService.insert(getEngineResult(executeEngineDTO));
        insertEsEngineResult(executeEngineDTO);
    }

    /**
     * 保存数据
     * @param executeEngineDTO 参数
     */
    public void save(RiskExecuteEngineDTO executeEngineDTO) {
        engineResultService.insert(getEngineResult(executeEngineDTO));
        insertEsEngineResult(executeEngineDTO);
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