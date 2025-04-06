package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
