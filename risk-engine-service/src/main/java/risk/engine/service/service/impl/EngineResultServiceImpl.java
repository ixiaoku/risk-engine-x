package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Service;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.components.es.ElasticsearchClientApi;
import risk.engine.components.es.EngineExecutorBoolQuery;
import risk.engine.db.dao.EngineResultMapper;
import risk.engine.db.dao.RuleVersionMapper;
import risk.engine.db.entity.EngineResultPO;
import risk.engine.db.entity.RuleVersionPO;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.dto.rule.HitRuleDTO;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.MetricTypeEnum;
import risk.engine.dto.enums.MetricValueTypeEnum;
import risk.engine.dto.enums.OperationSymbolEnum;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ReplyRuleVO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
@Slf4j
@Service
public class EngineResultServiceImpl implements IEngineResultService {

    @Resource
    private EngineResultMapper engineResultMapper;

    @Resource
    private ElasticsearchClientApi elasticsearchClientApi;

    @Resource
    private RuleVersionMapper ruleVersionMapper;

    @Override
    public boolean insert(EngineResultPO record) {
        return engineResultMapper.insert(record) > 0;
    }

    @Override
    public Map<String, BigDecimal> getDashboard(EngineExecutorParam executorParam) {
        BoolQueryBuilder boolQuery = EngineExecutorBoolQuery.getBoolQuery(executorParam);
        // 聚合部分
        TermsAggregationBuilder decisionAgg = AggregationBuilders
                .terms("decision_group")
                .field("decisionResult")
                .size(2);
        Pair<Aggregations,Long> pair = elasticsearchClientApi.queryWithAggregations(BusinessConstant.ENGINE_INDEX, boolQuery, decisionAgg);
        if (Objects.isNull(pair)) {
            return Collections.emptyMap();
        }
        Aggregations aggregations = pair.getLeft();
        if (Objects.isNull(aggregations)) {
            return Collections.emptyMap();
        }
        Terms decisionTerms = aggregations.get("decision_group");
        long total = pair.getRight(), pass = 0L, reject = 0L;
        for (Terms.Bucket bucket : decisionTerms.getBuckets()) {
            String key = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            if ("1".equalsIgnoreCase(key)) {
                pass = count;
            } else if ("0".equalsIgnoreCase(key)) {
                reject = count;
            }
        }
        BigDecimal approvalRate = total == 0L ? BigDecimal.ZERO : new BigDecimal(pass).divide(BigDecimal.valueOf(total), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal rejectionRate = total == 0L ? BigDecimal.ZERO : new BigDecimal(reject).divide(BigDecimal.valueOf(total), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("dailyVolume", BigDecimal.valueOf(total));
        result.put("approvalRate", approvalRate);
        result.put("rejectionRate",rejectionRate );
        return result;
    }

    @Override
    public EngineExecutorVO getOne(EngineExecutorParam executorParam) {
        Pair<List<EngineExecutorVO>, Long> pair = getRiskExecuteEngineDTOList(executorParam);
        if (CollectionUtils.isEmpty(pair.getLeft())) {
            return new EngineExecutorVO();
        }
        List<EngineExecutorVO> engineExecutorVOS = pair.getLeft();
        return engineExecutorVOS.get(0);
    }

    @Override
    public Pair<List<EngineExecutorVO>, Long> list(EngineExecutorParam executorParam) {
        return getRiskExecuteEngineDTOList(executorParam);
    }

    @Override
    public ReplyRuleVO replay(EngineExecutorParam executorParam) {
        ReplyRuleVO replyRuleVO = new ReplyRuleVO();
        GroovyShell groovyShell = new GroovyShell();
        EngineExecutorVO engineExecutorVO = getOne(executorParam);
        if (Objects.isNull(engineExecutorVO) || Objects.isNull(engineExecutorVO.getPrimaryRule())) return replyRuleVO;
        Map<String, Object> metricMap = new HashMap<>(engineExecutorVO.getMetric());
        HitRuleDTO hitRuleDTO = engineExecutorVO.getPrimaryRule();
        RuleVersionPO ruleVersionQuery = new RuleVersionPO();
        ruleVersionQuery.setVersion(hitRuleDTO.getRuleVersion());
        ruleVersionQuery.setRuleCode(hitRuleDTO.getRuleCode());
        RuleVersionPO ruleVersion = ruleVersionMapper.selectByExample(ruleVersionQuery);
        if (Objects.isNull(ruleVersion) || StringUtils.isEmpty(ruleVersion.getJsonScript())) return replyRuleVO;
        List<RuleMetricDTO> hitRuleDTOList = JSON.parseArray(ruleVersion.getJsonScript(), RuleMetricDTO.class);
        List<LinkedHashMap<String, Object>> mapList = hitRuleDTOList.stream()
                .map(metricDTO -> {
                    Map<String, Object> map = new HashMap<>();
                    String ruleScript = "";
                    if (StringUtils.equals(metricDTO.getMetricValueType(), MetricValueTypeEnum.CUSTOM.getCode())) {
                        boolean isString = Objects.equals(metricDTO.getMetricType(), MetricTypeEnum.STRING.getCode());
                        String value = isString ? "'" + metricDTO.getMetricValue() + "'" : metricDTO.getMetricValue();
                        map.put(metricDTO.getMetricCode(), metricMap.get(metricDTO.getMetricCode()));
                        ruleScript = metricDTO.getMetricCode() + " " + OperationSymbolEnum.getOperationSymbolEnumByCode(metricDTO.getOperationSymbol()).getName() + " " + value;
                    } else if (StringUtils.equals(metricDTO.getMetricValueType(), MetricValueTypeEnum.METRIC.getCode())) {
                        map.put(metricDTO.getMetricCode(), metricMap.get(metricDTO.getMetricCode()));
                        map.put(metricDTO.getRightMetricCode(), metricMap.get(metricDTO.getRightMetricCode()));
                        ruleScript = metricDTO.getMetricCode() + " " + OperationSymbolEnum.getOperationSymbolEnumByCode(metricDTO.getOperationSymbol()).getName() + " " + metricDTO.getMetricValue();
                    } else {
                        throw new RuntimeException();
                    }
                    Script script = groovyShell.parse(ruleScript);
                    boolean resultFlag = GroovyShellUtil.runGroovy(script, map);
                    LinkedHashMap<String, Object> conditionMap = new LinkedHashMap<>();
                    conditionMap.put("resultFlag", resultFlag);
                    conditionMap.put("ruleScript", ruleScript);
                    return conditionMap;
                }).collect(Collectors.toList());
        replyRuleVO.setRuleName(hitRuleDTO.getRuleName());
        replyRuleVO.setConditions(mapList);
        replyRuleVO.setLogicScript(ruleVersion.getLogicScript());
        replyRuleVO.setResultFlag(Boolean.TRUE);
        return replyRuleVO;
    }

    /**
     * 查询es
     * @param executorParam 参数
     * @return 结果
     */
    private Pair<List<EngineExecutorVO>, Long> getRiskExecuteEngineDTOList(EngineExecutorParam executorParam) {
        BoolQueryBuilder boolQuery = EngineExecutorBoolQuery.getBoolQuery(executorParam);
        Pair<SearchHit[], Long> pair = elasticsearchClientApi.queryWithPaging(BusinessConstant.ENGINE_INDEX, boolQuery, executorParam.getPageNum(), executorParam.getPageSize());
        if (pair == null) {
            return Pair.of(Collections.emptyList(), 0L);
        }
        SearchHit[] searchHits = pair.getLeft();
        List<EngineExecutorVO> executeEngineDTOList = Arrays.stream(searchHits)
                .map(searchHit -> JSON.parseObject(searchHit.getSourceAsString(), EngineExecutorVO.class))
                .collect(Collectors.toList());
        return Pair.of(executeEngineDTOList, pair.getRight());
    }

}
