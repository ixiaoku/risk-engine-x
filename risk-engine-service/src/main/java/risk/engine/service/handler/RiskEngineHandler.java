package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.components.es.ElasticsearchClientApi;
import risk.engine.db.entity.EngineResultPO;
import risk.engine.db.entity.PenaltyActionPO;
import risk.engine.db.entity.PenaltyRecordPO;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.dto.engine.RiskExecuteEngineDTO;
import risk.engine.dto.dto.penalty.RulePenaltyListDTO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.dto.enums.PenaltyActionEnum;
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
    private ElasticsearchClientApi elasticsearchClientApi;

    @Resource
    private IPenaltyService penaltyService;

    @Resource
    private IPenaltyRecordService penaltyRecordService;

    /**
     * 保存数据
     * @param executeEngineDTO 参数
     */
    public void saveEngineResult(RiskExecuteEngineDTO executeEngineDTO) {
        engineResultService.insert(getEngineResult(executeEngineDTO));
        insertEsEngineResult(executeEngineDTO);
    }

    /**
     * 处罚
     * @param executeEngineDTO 参数
     */
    public void savePenalty(RiskExecuteEngineDTO executeEngineDTO) {
        PenaltyActionPO p = new PenaltyActionPO();
        p.setStatus(1);
        List<PenaltyActionPO> penalties = penaltyService.selectByExample(p);
        if (CollectionUtils.isEmpty(penalties)) {
            return;
        }
        Map<String, List<PenaltyActionPO>> listMap = penalties.stream().collect(Collectors.groupingBy(PenaltyActionPO::getPenaltyCode));
        List<PenaltyRecordPO> recordList = new ArrayList<>();
        executeEngineDTO
                .getHitOnlineRules()
                .stream()
                .filter(hit -> StringUtils.isNotBlank(hit.getPenaltyAction()))
                .forEach(hitOnlineRule -> {
                    String penaltyJson = hitOnlineRule.getPenaltyAction();
                    List<RulePenaltyListDTO> penaltyListDTOList = JSON.parseArray(penaltyJson, RulePenaltyListDTO.class);
                    List<PenaltyRecordPO> penaltyRecordList = penaltyListDTOList.stream().map(listDTO ->  {
                        String penaltyCode = listDTO.getPenaltyCode();
                        List<PenaltyActionPO> penaltyList = listMap.get(penaltyCode);
                        PenaltyActionPO penalty = penaltyList.get(0);
                        PenaltyRecordPO penaltyRecord = new PenaltyRecordPO();
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
                        getPenaltyJson(listDTO, executeEngineDTO);
                        penaltyRecord.setPenaltyJson(new Gson().toJson(listDTO.getPenaltyJson()));
                        penaltyRecord.setStatus(PenaltyStatusEnum.WAIT.getCode());
                        penaltyRecord.setRetry(0);
                        penaltyRecord.setPenaltyTime(LocalDateTime.now());
                        penaltyRecord.setCreateTime(LocalDateTime.now());
                        penaltyRecord.setUpdateTime(LocalDateTime.now());
                        return penaltyRecord;
                    }).collect(Collectors.toList());
                    recordList.addAll(penaltyRecordList);
                });
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        //进行分组处理
        List<List<PenaltyRecordPO>> list = Lists.partition(recordList, 200);
        list.forEach(penaltyRecords -> {
            //保存处罚记录
            penaltyRecordService.batchInsert(penaltyRecords);
        });
        log.info("PenaltyRecord 保存成功");
    }

    private void getPenaltyJson (RulePenaltyListDTO penaltyListDTO, RiskExecuteEngineDTO executeEngineDTO) {
        if (StringUtils.equals(penaltyListDTO.getPenaltyCode(), PenaltyActionEnum.APPEND_LIST.getCode())) {
            penaltyListDTO.getPenaltyJson().forEach(json -> {
                String listValue = (String) executeEngineDTO.getRequestPayload().get(json.getListCode());
                json.setListValue(listValue);
            });
        }
    }

    /**
     * 业务数据 保存es
     * @param engineResult 参数
     */
    private void insertEsEngineResult(RiskExecuteEngineDTO engineResult) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(engineResult));
        map.put("id", UUID.randomUUID().toString());
        map.put("createTime", DateTimeUtil.getTimeByTimestamp(System.currentTimeMillis()));
        mapList.add(map);
        elasticsearchClientApi.saveDocument(BusinessConstant.ENGINE_INDEX, mapList);
    }

    /**
     * 转换引擎执行结果
     * @param executeEngineDTO 参数
     * @return 结果
     */
    private EngineResultPO getEngineResult(RiskExecuteEngineDTO executeEngineDTO) {
        EngineResultPO engineResult = new EngineResultPO();
        engineResult.setFlowNo(executeEngineDTO.getFlowNo());
        engineResult.setRiskFlowNo(executeEngineDTO.getRiskFlowNo());
        engineResult.setRequestPayload(JSON.toJSONString(executeEngineDTO.getRequestPayload()));
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
        engineResult.setCreateTime(LocalDateTime.now());
        engineResult.setHitMockRules(new Gson().toJson(executeEngineDTO.getHitMockRules()));
        engineResult.setHitOnlineRules(new Gson().toJson(executeEngineDTO.getHitOnlineRules()));
        engineResultService.insert(engineResult);
        return engineResult;
    }

}
