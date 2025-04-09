package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
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
import risk.engine.dto.enums.PenaltyActionEnum;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.dto.vo.PenaltyFieldVO;
import risk.engine.service.service.IEngineResultService;
import risk.engine.service.service.IPenaltyActionService;
import risk.engine.service.service.IPenaltyRecordService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 处理业务请求数据
 * @Author: X
 * @Date: 2025/3/16 13:39
 * @Version: 1.0
 */
@Slf4j
@Component
public class RiskEngineExecutorHandler {

    @Resource
    private IEngineResultService engineResultService;

    @Resource
    private ElasticsearchClientApi elasticsearchClientApi;

    @Resource
    private IPenaltyActionService penaltyService;

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
        if (CollectionUtils.isEmpty(executeEngineDTO.getHitOnlineRules())) {
            return;
        }
        PenaltyActionPO actionPO = new PenaltyActionPO();
        actionPO.setStatus(1);
        List<PenaltyActionPO> penaltieList = penaltyService.selectByExample(actionPO);
        if (CollectionUtils.isEmpty(penaltieList)) {
            throw new RuntimeException("PenaltyAction is empty");
        }
        List<PenaltyRecordPO> recordList = new ArrayList<>();
        executeEngineDTO
                .getHitOnlineRules()
                .stream()
                .filter(hit -> StringUtils.isNotBlank(hit.getRulePenaltyAction()))
                .forEach(hitOnlineRule -> {
                    PenaltyActionPO penalty = penaltieList.stream().filter(e -> StringUtils.equals(hitOnlineRule.getRulePenaltyAction(), e.getPenaltyCode())).findFirst().orElse(null);
                    if (Objects.isNull(penalty)) {
                        return;
                    }
                    List<PenaltyFieldVO> penaltyFieldVOList = JSON.parseArray(penalty.getPenaltyJson(), PenaltyFieldVO.class);
                    if (CollectionUtils.isEmpty(penaltyFieldVOList)) {
                        return;
                    }
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
                    String penaltyJson = getPenaltyJson(penalty.getPenaltyCode(), penaltyFieldVOList, executeEngineDTO.getRequestPayload());
                    penaltyRecord.setPenaltyJson(penaltyJson);
                    penaltyRecord.setStatus(PenaltyStatusEnum.WAIT.getCode());
                    penaltyRecord.setRetry(0);
                    penaltyRecord.setPenaltyTime(LocalDateTime.now());
                    penaltyRecord.setCreateTime(LocalDateTime.now());
                    penaltyRecord.setUpdateTime(LocalDateTime.now());
                    recordList.add(penaltyRecord);
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
    }

    /**
     * 获取处置报文
     * @param penaltyCode 处置code
     * @param penaltyFieldVOList 处置字段
     * @param requestPayload 请求报文
     * @return 结果
     */
    private String getPenaltyJson (String penaltyCode, List<PenaltyFieldVO> penaltyFieldVOList, Map<String, Object> requestPayload) {
        if (StringUtils.equals(penaltyCode, PenaltyActionEnum.BUSINESS_WECHAT_BOT.getCode())) {
            Map<String, Object> businessWeChatMap = new HashMap<>();
            Object value = requestPayload.get("announcement");
            if (Objects.isNull(value)) {
                return null;
            }
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(value));
            for (PenaltyFieldVO fieldVO : penaltyFieldVOList) {
                businessWeChatMap.put(fieldVO.getFieldCode(), jsonObject.get(fieldVO.getFieldCode()));
            }
            return JSON.toJSONString(businessWeChatMap);
        } else if (StringUtils.equals(penaltyCode, PenaltyActionEnum.APPEND_LIST.getCode())) {
            Map<String, Object> addListMap = new HashMap<>();
            for (PenaltyFieldVO fieldVO : penaltyFieldVOList) {
                Object value = requestPayload.get(fieldVO.getFieldCode());
                if (Objects.isNull(value)) {
                    continue;
                }
                addListMap.put(fieldVO.getFieldCode(), value);
            }
            return JSON.toJSONString(addListMap);
        } else {
            throw new RuntimeException("处罚手段未配置");
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
        engineResult.setPrimaryElement(JSON.toJSONString(executeEngineDTO.getPrimaryElement()));
        engineResult.setMetric(JSON.toJSONString(executeEngineDTO.getMetric()));
        engineResult.setExtra(JSON.toJSONString(executeEngineDTO.getExtra()));
        engineResult.setPrimaryRule(JSON.toJSONString(executeEngineDTO.getPrimaryRule()));
        engineResult.setDecisionResult(executeEngineDTO.getDecisionResult());
        engineResult.setExecutionTime(executeEngineDTO.getExecutionTime());
        engineResult.setRequestPayload(JSON.toJSONString(executeEngineDTO.getRequestPayload()));
        engineResult.setIncidentCode(executeEngineDTO.getIncidentCode());
        engineResult.setIncidentName(executeEngineDTO.getIncidentName());
        engineResult.setCreateTime(LocalDateTime.now());
        engineResult.setHitMockRules(JSON.toJSONString(executeEngineDTO.getHitMockRules()));
        engineResult.setHitOnlineRules(JSON.toJSONString(executeEngineDTO.getHitOnlineRules()));
        engineResultService.insert(engineResult);
        return engineResult;
    }

}
