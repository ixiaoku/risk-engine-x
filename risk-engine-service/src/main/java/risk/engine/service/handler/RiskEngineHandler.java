package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.components.es.ElasticsearchRestApi;
import risk.engine.db.entity.EngineResult;
import risk.engine.db.entity.Penalty;
import risk.engine.db.entity.PenaltyRecord;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.dto.dto.penalty.RulePenaltyListDTO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.service.IEngineResultService;
import risk.engine.service.service.IPenaltyRecordService;
import risk.engine.service.service.IPenaltyService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Resource
    private IPenaltyService penaltyService;

    @Resource
    private IPenaltyRecordService penaltyRecordService;

    /**
     * 保存数据
     * @param executeEngineDTO 参数
     */
    public void saveDataAndDoPenalty(RiskExecuteEngineDTO executeEngineDTO) {
        executeEngineDTO.setCreateTime(LocalDateTime.now());
        engineResultService.insert(getEngineResult(executeEngineDTO));
        insertEsEngineResult(executeEngineDTO);
        doPenalty(executeEngineDTO);
    }

    /**
     * 处罚
     * @param executeEngineDTO 参数
     */
    private void doPenalty(RiskExecuteEngineDTO executeEngineDTO) {
        Penalty p = new Penalty();
        p.setStatus(1);
        List<Penalty> penalties = penaltyService.selectByExample(p);
        if (CollectionUtils.isEmpty(penalties)) {
            return;
        }
        Map<String, List<Penalty>> listMap = penalties.stream().collect(Collectors.groupingBy(Penalty::getPenaltyCode));
        List<PenaltyRecord> recordList = new ArrayList<>();
        executeEngineDTO
                .getHitOnlineRules()
                .stream().filter(e -> StringUtils.isNoneBlank(e.getPenaltyAction()))
                .forEach(hitOnlineRule -> {
                    String penaltyJson = hitOnlineRule.getPenaltyAction();
                    List<RulePenaltyListDTO> penaltyListDTOList = JSON.parseArray(penaltyJson, RulePenaltyListDTO.class);
                    List<PenaltyRecord> penaltyRecordList = penaltyListDTOList.stream().map(listDTO ->  {
                        String penaltyCode = listDTO.getPenaltyCode();
                        List<Penalty> penaltyList = listMap.get(penaltyCode);
                        Penalty penalty = penaltyList.get(0);
                        PenaltyRecord penaltyRecord = new PenaltyRecord();
                        penaltyRecord.setFlowNo(executeEngineDTO.getFlowNo());
                        penaltyRecord.setRuleCode(hitOnlineRule.getRuleCode());
                        penaltyRecord.setRuleName(hitOnlineRule.getRuleName());
                        penaltyRecord.setIncidentCode(executeEngineDTO.getIncidentCode());
                        penaltyRecord.setIncidentName(executeEngineDTO.getIncidentName());
                        penaltyRecord.setPenaltyCode(penalty.getPenaltyCode());
                        penaltyRecord.setPenaltyName(penalty.getPenaltyName());
                        penaltyRecord.setPenaltyDef(penalty.getPenaltyDef());
                        penaltyRecord.setPenaltyReason(penalty.getPenaltyDescription());
                        penaltyRecord.setPenaltyResult("");
                        penaltyRecord.setPenaltyDescription(penalty.getPenaltyDescription());
                        penaltyRecord.setPenaltyJson(new Gson().toJson(listDTO.getPenaltyJson()));
                        listDTO.getPenaltyJson().forEach(json -> {

                        });
                        penaltyRecord.setStatus(PenaltyStatusEnum.WAIT.getCode());
                        penaltyRecord.setRetry(0);
                        penaltyRecord.setPenaltyTime(LocalDateTime.now());
                        penaltyRecord.setCreateTime(LocalDateTime.now());
                        penaltyRecord.setUpdateTime(LocalDateTime.now());
                        return penaltyRecord;
                    }).collect(Collectors.toList());
                    recordList.addAll(penaltyRecordList);
                });
        //保存处罚记录
        recordList.forEach(record -> penaltyRecordService.insert(record));
        log.info("PenaltyRecord 保存成功");
    }

    /**
     * 业务数据 保存es
     * @param engineResult 参数
     */
    private void insertEsEngineResult(RiskExecuteEngineDTO engineResult) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(engineResult));
        map.put("id", UUID.randomUUID().toString());
        mapList.add(map);
        elasticsearchRestApi.saveDocument(BusinessConstant.ENGINE_INDEX, mapList);
        log.info("es 保存成功");
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
