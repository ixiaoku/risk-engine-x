package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Service;
import risk.engine.components.es.ElasticsearchClientApi;
import risk.engine.components.es.EngineExecutorBoolQuery;
import risk.engine.db.dao.EngineResultMapper;
import risk.engine.db.entity.EngineResultPO;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
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
@Service
public class EngineResultServiceImpl implements IEngineResultService {

    @Resource
    private EngineResultMapper engineResultMapper;

    @Resource
    private ElasticsearchClientApi elasticsearchClientApi;

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
        Aggregations aggregations = elasticsearchClientApi.queryWithAggregations(BusinessConstant.ENGINE_INDEX, boolQuery, decisionAgg);
        if (Objects.isNull(aggregations)) {
            return Collections.emptyMap();
        }
        Terms decisionTerms = aggregations.get("decision_group");
        long total = 0L, pass = 0L, reject = 0L;
        for (Terms.Bucket bucket : decisionTerms.getBuckets()) {
            String key = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            total += count;
            if ("1".equalsIgnoreCase(key)) {
                pass = count;
            } else if ("0".equalsIgnoreCase(key)) {
                reject = count;
            }
        }
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("dailyVolume", BigDecimal.valueOf(total));
        result.put("approvalRate", new BigDecimal(pass).divide(BigDecimal.valueOf(total), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
        result.put("rejectionRate", new BigDecimal(reject).divide(BigDecimal.valueOf(total), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
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
