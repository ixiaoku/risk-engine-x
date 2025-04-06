package risk.engine.components.es;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import risk.engine.dto.param.EngineExecutorParam;

/**
 * @Author: X
 * @Date: 2025/4/1 22:57
 * @Version: 1.0
 */
public class EngineExecutorBoolQuery {

    public static BoolQueryBuilder getBoolQuery(EngineExecutorParam executorParam) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(executorParam.getIncidentCode())) {
            boolQuery.filter(QueryBuilders.termQuery("incidentCode", executorParam.getIncidentCode()));
        }
        if (StringUtils.isNotEmpty(executorParam.getFlowNo())) {
            boolQuery.filter(QueryBuilders.termQuery("flowNo", executorParam.getFlowNo()));
        }
        if (StringUtils.isNotEmpty(executorParam.getRiskFlowNo())) {
            boolQuery.filter(QueryBuilders.termQuery("riskFlowNo", executorParam.getRiskFlowNo()));
        }
//        if (StringUtils.isNotEmpty(executorParam.getIncidentName())) {
//            boolQuery.filter(QueryBuilders.wildcardQuery("incidentName", "*" + executorParam.getIncidentName() + "*"));
//        }
//        if (StringUtils.isNotEmpty(executorParam.getRuleName())) {
//            boolQuery.filter(QueryBuilders.matchPhraseQuery("ruleName", executorParam.getRuleName()));
//        }
        if (StringUtils.isNotEmpty(executorParam.getStartTime()) && StringUtils.isNotEmpty(executorParam.getEndTime())) {
            boolQuery.must(QueryBuilders.rangeQuery("createTime")
                    .gte(executorParam.getStartTime())
                    .lte(executorParam.getEndTime())
                    .format("yyyy-MM-dd HH:mm:ss"));
        }
        //boolQuery.must(QueryBuilders.termsQuery("eventCode", Arrays.asList("ChainTransfer", "FiatDeposit")));
        return boolQuery;
    }

}
